package mods.shiborui.fermentation.tileentity;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
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
import net.minecraftforge.liquids.LiquidDictionary;
import net.minecraftforge.liquids.LiquidStack;
import net.minecraftforge.liquids.LiquidTank;

public class TileEntityTank extends TileEntityGenericTank implements IInventory {
	 private ItemStack[] inventory;
	 private int progress = 0;
	 private int tickCount = 0;
	 private boolean active = true;
	 
	 private RestrictedSlot[] inventorySlots = new RestrictedSlot[3];
	 
	 private Item[] validLiquids = {Item.bucketWater, Fermentation.bucketSweetWort, Fermentation.bucketHoppedWort, Fermentation.bucketBeer};
	 
	 private static final int INPUT = 0;
	 private static final int OUTPUT = 1;
	 private static final int SOLID = 2;
	 
     public TileEntityTank(){
             inventory = new ItemStack[3];
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
             int liquidVolume = tag.getInteger("LiquidVolume");
             ((LiquidTank) this.getTank()).setLiquid(LiquidDictionary.getLiquid(tag.getString("LiquidType"), liquidVolume));
        	 this.progress = tag.getByte("Progress");
        	 updateSlotConfiguration();
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
             contentTag.setByte("Progress", (byte) progress);
             
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
	public void updateEntity() {
		if(active) {
			//if(++tickCount == 240) {
			if(++tickCount == 24) { //10x speed for testing
				incrementProgress();
			}
		}
	}

	@Override
	public boolean isStackValidForSlot(int i, ItemStack itemstack) {
		return false;
	}
	
	public void registerInventorySlot(RestrictedSlot slot, int slotID) {
		inventorySlots[slotID] = slot;
		updateSlotConfiguration();
	}
	
	public int getProgress() {
		return progress;
	}
	
	public boolean isActive() {
		return active;
	}
	
	public boolean setProgress(int progress) {
		this.progress = progress;
		tickCount = 0;
		
		if(progress >= 100 && inventory[SOLID] != null) {
			Side side = FMLCommonHandler.instance().getEffectiveSide();
			if (side == Side.SERVER) {
				if(inventory[SOLID].getItem().equals(Fermentation.driedGrain) && this.getTank().getLiquidName().equals("Water")) {
					setInventorySlotContents(SOLID, new ItemStack(Fermentation.hydratedGrain, inventory[SOLID].stackSize));
				} else if (inventory[SOLID].getItem().equals(Fermentation.milledGrain) && this.getTank().getLiquidName().equals("Water")) {
					setInventorySlotContents(SOLID, null);
					this.getTank().setLiquid(new LiquidStack(Fermentation.liquidSweetWort.itemID, this.getTank().getLiquid().amount));
				} else if (inventory[SOLID].getItem().equals(Fermentation.yeast) && this.getTank().getLiquidName().equals("Hopped Wort")) {
					setInventorySlotContents(SOLID, null);
					this.getTank().setLiquid(new LiquidStack(Fermentation.liquidBeer.itemID, this.getTank().getLiquid().amount));
				}
				List<EntityPlayer> players = worldObj.playerEntities;
				for (int i = 0; i < players.size(); i++) {
					if (this.isUseableByPlayer(players.get(i))) {
						this.sendStateToClient(players.get(i));
					}
				}
			}
		}
		
		updateSlotConfiguration();
		this.worldObj.updateTileEntityChunkAndDoNothing(this.xCoord, this.yCoord, this.zCoord, this);
		return true;
	}
	
	private void updateSlotConfiguration() {
		updateActive();
		if (inventorySlots[INPUT] == null || inventorySlots[SOLID] == null) {
			return;
		}
		if (progress < 5 || progress == 100) {
			inventorySlots[INPUT].setPlayerCanPut(true);
			inventorySlots[SOLID].setPlayerCanPut(true);
			inventorySlots[SOLID].setPlayerCanTake(true);
		} else if (progress >= 5 && progress < 100) {
			inventorySlots[INPUT].setPlayerCanPut(false);
			inventorySlots[SOLID].setPlayerCanPut(false);
			inventorySlots[SOLID].setPlayerCanTake(false);
		}
	}
	
	public boolean incrementProgress() {
		setProgress(getProgress() + 1);
		return true;
	}
	
	public boolean updateActive() {
		if (this.getTank().getLiquid() != null && inventory[SOLID] != null && progress < 100) {
			String liquidName = this.getTank().getLiquidName();
			Item solid = inventory[SOLID].getItem();
			if (liquidName.equals("Water") && solid.equals(Fermentation.driedGrain)) {
				active = true;
			} else if (liquidName.equals("Water") && solid.equals(Fermentation.milledGrain)) {
				active = true;
			} else if (liquidName.equals("Hopped Wort") && solid.equals(Fermentation.yeast)) {
				active = true;
			} else {
				active = false;
			}
		} else {
			active = false;
		}
		return active;
	}
	
	public void onInventoryChanged() {
		if(inventory[INPUT] != null) {
			ItemStack[] newStacks = this.transferLiquid(inventory[INPUT], inventory[OUTPUT]);
			inventory[INPUT] = newStacks[INPUT];
			inventory[OUTPUT] = newStacks[OUTPUT];
		}

		updateSlotConfiguration();
		
		boolean canProgress = false;
		if (this.getTank().getLiquid() != null && inventory[SOLID] != null) {
			
		} else if (this.getTank().getLiquid() == null || !this.getTank().getLiquidName().equals("Sweet Wort") && !this.getTank().getLiquidName().equals("Beer")) {
			setProgress(0);
		}
	}
	
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
                    outputStream.writeByte(this.getProgress());
            } catch (Exception ex) {
                    ex.printStackTrace();
            }

            Packet250CustomPayload packet = new Packet250CustomPayload();
            packet.channel = "FmtnTank";
            packet.data = bos.toByteArray();
            packet.length = bos.size();
            PacketDispatcher.sendPacketToPlayer(packet, (Player) player);
        }
	}
	
	@Override
	public void handleServerState(Packet250CustomPayload packet,
			Player playerEntity) {
		Side side = FMLCommonHandler.instance().getEffectiveSide();
		
		if (side == Side.CLIENT) {
			
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
				progress = inputStream.readByte();
				
				LiquidStack liquid = LiquidDictionary.getLiquid(liquidName, liquidVolume);
				
				this.getTank().setLiquid(liquid);
				this.setProgress(progress);
				
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
		}
	}
}
