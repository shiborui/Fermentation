package mods.shiborui.fermentation.tileentity;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.Arrays;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.relauncher.Side;
import mods.shiborui.fermentation.Fermentation;
import mods.shiborui.fermentation.inventory.RestrictedSlot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;

public class TileEntityWaterproofBarrel extends TileEntity implements IInventory {
	
	private ItemStack[] inventory;
	private int liquidType = 0;
	private int liquidVolume = 0;
	
	private RestrictedSlot[] inventorySlots = new RestrictedSlot[2];
	
	private Item[] validLiquids = {Item.bucketWater, Fermentation.bucketSweetWort, Fermentation.bucketHoppedWort, Fermentation.bucketBeer};
	
	private static final int INPUT = 0;
	private static final int OUTPUT = 1;
	
	public static final int EMPTY = 0;
	public static final int WATER = 1;
	public static final int SWEETWORT = 2;
	public static final int HOPPEDWORT = 3;
	public static final int BEER = 4;
	
	public TileEntityWaterproofBarrel() {
		inventory = new ItemStack[2];
	}
	
	@Override
    public int getSizeInventory() {
        return inventory.length;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return inventory[slot];
    }
    
    @Override
    public void setInventorySlotContents(int slot, ItemStack stack) {
        inventory[slot] = stack;
        if (stack != null && stack.stackSize > getInventoryStackLimit()) {
        	stack.stackSize = getInventoryStackLimit();
        }
    }
    
    @Override
    public ItemStack decrStackSize(int slot, int amt) {
            ItemStack stack = getStackInSlot(slot);
            if (stack != null) {
                    if (stack.stackSize <= amt) {
                            setInventorySlotContents(slot, null);
                    } else {
                            stack = stack.splitStack(amt);
                            if (stack.stackSize == 0) {
                                    setInventorySlotContents(slot, null);
                            }
                    }
            }
            return stack;
    }
    
    @Override
    public ItemStack getStackInSlotOnClosing(int slot) {
            ItemStack stack = getStackInSlot(slot);
            if (stack != null) {
                    setInventorySlotContents(slot, null);
            }
            return stack;
    }
    
    @Override
    public int getInventoryStackLimit() {
            return 64;
    }
    
    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
            return worldObj.getBlockTileEntity(xCoord, yCoord, zCoord) == this &&
            player.getDistanceSq(xCoord + 0.5, yCoord + 0.5, zCoord + 0.5) < 64;
    }
    
    @Override
    public void openChest() {}

    @Override
    public void closeChest() {}
    
    @Override
    public void readFromNBT(NBTTagCompound tagCompound) {
            super.readFromNBT(tagCompound);
            
            NBTTagList tagList = tagCompound.getTagList("Inventory");
            for (int i = 0; i < tagList.tagCount(); i++) {
                    NBTTagCompound tag = (NBTTagCompound) tagList.tagAt(i);
                    byte slot = tag.getByte("Slot");
                    if (slot >= 0 && slot < inventory.length) {
                            inventory[slot] = ItemStack.loadItemStackFromNBT(tag);
                    }
            }
            
            NBTTagCompound tag = tagCompound.getCompoundTag("Content");
       	 this.liquidType = tag.getByte("LiquidType");
       	 this.liquidVolume = tag.getByte("LiquidVolume");
    }
    
    @Override
    public void writeToNBT(NBTTagCompound tagCompound) {
            super.writeToNBT(tagCompound);
                            
            NBTTagList itemList = new NBTTagList();
            for (int i = 0; i < inventory.length; i++) {
                    ItemStack stack = inventory[i];
                    if (stack != null) {
                            NBTTagCompound tag = new NBTTagCompound();
                            tag.setByte("Slot", (byte) i);
                            stack.writeToNBT(tag);
                            itemList.appendTag(tag);
                    }
            }
            tagCompound.setTag("Inventory", itemList);
            
            NBTTagCompound contentTag = new NBTTagCompound();
            contentTag.setByte("LiquidType", (byte) liquidType);
            contentTag.setByte("LiquidVolume", (byte) liquidVolume);
            tagCompound.setTag("Content", contentTag);
    }
    
    @Override
    public String getInvName() {
            return "fermentation.tileEntityTank";
    }

	@Override
	public boolean isInvNameLocalized() {
		return false;
	}
	
	@Override
	public boolean isStackValidForSlot(int i, ItemStack itemstack) {
		return false;
	}
	
	public void registerInventorySlot(RestrictedSlot slot, int slotID) {
		inventorySlots[slotID] = slot;
	}
	
	public int getLiquidType() {
		return liquidType;
	}
	
	public int getLiquidVolume() {
		return liquidVolume;
	}
	
	public boolean setLiquidType(int liquidType) {
		this.liquidType = liquidType;
		return true;
	}
	
	public boolean setLiquidVolume(int liquidVolume) {
		this.liquidVolume = liquidVolume;
		if (liquidVolume == 0) {
			liquidType = EMPTY;
		}
		return true;
	}
	
	public void onInventoryChanged() {
		if(inventory[INPUT] != null) {
			boolean removingLiquid = false;
			if (liquidVolume < 64 && Arrays.asList(validLiquids).contains(inventory[INPUT].getItem())) {
				boolean willTakeLiquid = false;
				if (inventory[INPUT].getItem().equals(Item.bucketWater) && (getLiquidType() == WATER || getLiquidType() == EMPTY)) {
					setLiquidType(WATER);
					willTakeLiquid = true;
				} else if (inventory[INPUT].getItem().equals(Fermentation.bucketSweetWort) && (getLiquidType() == SWEETWORT || getLiquidType() == EMPTY)) {
					setLiquidType(SWEETWORT);
					willTakeLiquid = true;
				} else if (inventory[INPUT].getItem().equals(Fermentation.bucketHoppedWort) && (getLiquidType() == HOPPEDWORT || getLiquidType() == EMPTY)) {
					setLiquidType(HOPPEDWORT);
					willTakeLiquid = true;
				} else if (inventory[INPUT].getItem().equals(Fermentation.bucketBeer) && (getLiquidType() == BEER || getLiquidType() == EMPTY)) {
					setLiquidType(BEER);
					willTakeLiquid = true;
				}
				if(willTakeLiquid) {
					if(inventory[OUTPUT] == null) {
						setInventorySlotContents(OUTPUT, new ItemStack(Item.bucketEmpty));
						setInventorySlotContents(INPUT, null);
						setLiquidVolume(getLiquidVolume() + 1);
					} else if (inventory[OUTPUT].getItem().equals(Item.bucketEmpty) && inventory[OUTPUT].stackSize < 16) {
						inventory[OUTPUT].stackSize++;
						setInventorySlotContents(INPUT, null);
						setLiquidVolume(getLiquidVolume() + 1);
					}
				}
			} else if (inventory[INPUT].getItem().equals(Item.bucketEmpty) && getLiquidVolume() > 0 && inventory[OUTPUT] == null) {
				removingLiquid = true;
				switch(liquidType) {
				case WATER:
					setInventorySlotContents(OUTPUT, new ItemStack(Item.bucketWater));
					break;
				case SWEETWORT:
					setInventorySlotContents(OUTPUT, new ItemStack(Fermentation.bucketSweetWort));
					break;
				case HOPPEDWORT:
					setInventorySlotContents(OUTPUT, new ItemStack(Fermentation.bucketHoppedWort));
					break;
				case BEER:
					setInventorySlotContents(OUTPUT, new ItemStack(Fermentation.bucketBeer));
					break;
				}
			} else if (inventory[INPUT].getItem().equals(Fermentation.mug) && getLiquidType() == BEER && getLiquidVolume() > 0 && inventory[OUTPUT] == null) {
				removingLiquid = true;
				setInventorySlotContents(OUTPUT, new ItemStack(Fermentation.beer));
			}
			if (removingLiquid) {
				if(inventory[INPUT].stackSize > 1) {
					inventory[INPUT].stackSize--;
					setLiquidVolume(getLiquidVolume() - 1);
				} else {
					setInventorySlotContents(INPUT, null);
					setLiquidVolume(getLiquidVolume() - 1);
				}
			}
		}
	}
	
	public void sendStateToClient(EntityPlayer player) {
        Side side = FMLCommonHandler.instance().getEffectiveSide();
        
        if(side == Side.SERVER) {
        	ByteArrayOutputStream bos = new ByteArrayOutputStream(8);
            DataOutputStream outputStream = new DataOutputStream(bos);
            try {
            		outputStream.writeInt(this.xCoord);
                    outputStream.writeInt(this.yCoord);
                    outputStream.writeInt(this.zCoord);
                    outputStream.writeByte(this.getLiquidType());
                    outputStream.writeByte(this.getLiquidVolume());
            } catch (Exception ex) {
                    ex.printStackTrace();
            }

            Packet250CustomPayload packet = new Packet250CustomPayload();
            packet.channel = "FmtnWPBarrel";
            packet.data = bos.toByteArray();
            packet.length = bos.size();
            PacketDispatcher.sendPacketToPlayer(packet, (Player) player);
        }
	}

}
