package mods.shiborui.fermentation.tileentity;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

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
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.liquids.ITankContainer;
import net.minecraftforge.liquids.LiquidContainerRegistry;
import net.minecraftforge.liquids.LiquidDictionary;
import net.minecraftforge.liquids.LiquidStack;
import net.minecraftforge.liquids.LiquidTank;

public class TileEntityWaterproofBarrel extends TileEntityGenericTank implements IInventory {
	
	private ItemStack[] inventory;
	private int progress = 0;
	private int tickCount = 0;
	
	private RestrictedSlot[] inventorySlots = new RestrictedSlot[2];
	
	private Item[] validLiquids = {Item.bucketWater, Fermentation.bucketSweetWort, Fermentation.bucketHoppedWort, Fermentation.bucketBeer};
	
	private static final int INPUT = 0;
	private static final int OUTPUT = 1;
	
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
    
    public int getProgress() {
    	return progress;
    }
    
    public void setProgress(int progress) {
    	this.progress = progress;
		tickCount = 0;
		
		if(progress >= 100 && this.getTank().getLiquid().asItemStack().getItem().equals(Fermentation.liquidBeer)) {
			this.getTank().setLiquid(new LiquidStack(Fermentation.liquidBeer.itemID, this.getTank().getLiquid().amount, this.getTank().getLiquid().itemMeta | 16));
			this.progress = 0;
			
			if (this.worldObj != null) {
				List<EntityPlayer> players = this.worldObj.playerEntities;
				for (int i = 0; i < players.size(); i++) {
					if (this.isUseableByPlayer(players.get(i))) {
						this.sendStateToClient(players.get(i));
					}
				}
			}
		}
		
		if (this.worldObj != null) {
			this.worldObj.updateTileEntityChunkAndDoNothing(this.xCoord, this.yCoord, this.zCoord, this);
		}
    }
    
    @Override
	public void updateEntity() {
		if (this.getTank().getLiquid() != null && 
				this.getTank().getLiquid().asItemStack().getItem().equals(Fermentation.liquidBeer) &&
				((this.getTank().getLiquid().itemMeta & 16) >> 4) == 0) {
			if(++tickCount >= 24) { //100x speed for testing
				incrementProgress();
			}
		}
	}
    
    public boolean incrementProgress() {
		setProgress(getProgress() + 1);
		return true;
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
            
            int liquidVolume = tag.getInteger("LiquidVolume");
            ((LiquidTank) this.getTank()).setLiquid(LiquidDictionary.getLiquid(tag.getString("LiquidType"), liquidVolume));
            setProgress(tag.getInteger("Progress"));
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
            
            LiquidStack liquid = this.getTank().getLiquid();

            if (liquid != null) {
            	contentTag.setInteger("LiquidVolume", liquid.amount);
                contentTag.setString("LiquidType", LiquidDictionary.findLiquidName(liquid));
            } else {
            	contentTag.setInteger("LiquidVolume", 0);
                contentTag.setString("LiquidType", "Empty");
            }
            contentTag.setInteger("Progress", this.getProgress());
            
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
	
	public void onInventoryChanged() {
		if (inventory[INPUT] != null && !inventory[INPUT].getItem().equals(Item.bucketLava)) {
			int liquid = 0;
			if (this.getTank().getLiquid() != null) {
				liquid = this.getTank().getLiquid().amount;
			}
			ItemStack[] newStacks = this.transferLiquid(inventory[INPUT], inventory[OUTPUT]);
			inventory[INPUT] = newStacks[INPUT];
			inventory[OUTPUT] = newStacks[OUTPUT];
			if (this.getTank().getLiquid() == null || this.getTank().getLiquid().amount > liquid) {
				setProgress(0);
			}
		}
	}
	
	@Override
	public void sendStateToClient(EntityPlayer player) {
        Side side = FMLCommonHandler.instance().getEffectiveSide();
        
        if(side == Side.SERVER) {
        	
        	LiquidStack liquid = this.getTank().getLiquid();
        	
        	ByteArrayOutputStream bos = new ByteArrayOutputStream(8);
            DataOutputStream outputStream = new DataOutputStream(bos);
            try {
            		outputStream.writeInt(this.xCoord);
                    outputStream.writeInt(this.yCoord);
                    outputStream.writeInt(this.zCoord);
                    if (liquid != null) {
                    	outputStream.writeUTF(LiquidDictionary.findLiquidName(liquid));
                        outputStream.writeInt(liquid.amount);
                    } else {
                    	outputStream.writeUTF("Empty");
                        outputStream.writeInt(0);
                    }
                    outputStream.writeInt(this.getProgress());
                    
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
	
	@Override
	public void handleServerState(Packet250CustomPayload packet,
			Player playerEntity) {
		Side side = FMLCommonHandler.instance().getEffectiveSide();
		
		DataInputStream inputStream = new DataInputStream(new ByteArrayInputStream(packet.data));
		
		int x, y, z;
		String liquidName;
		int liquidVolume;
		
		try {
			x = inputStream.readInt();
			y = inputStream.readInt();
			z = inputStream.readInt();
			liquidName = inputStream.readUTF();
			liquidVolume = inputStream.readInt();
			setProgress(inputStream.readInt());
			
			LiquidStack liquid = LiquidDictionary.getLiquid(liquidName, liquidVolume);
			
			if (side == Side.CLIENT) {
				this.getTank().setLiquid(liquid);
			}
			
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
	}

}
