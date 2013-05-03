package mods.shiborui.fermentation;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;

public class TileEntityTank extends TileEntity implements IInventory{
	 private ItemStack[] inventory;
	 private int liquidType = 0;
	 private int liquidVolume = 0;
	 private int solidType = 0;
	 private int solidCount = 0;
	 private int progress = 0;
	 private int tickCount = 0;
	 private boolean active = true;
	 
	 private RestrictedSlot[] inventorySlots = new RestrictedSlot[3];
	 
	 private static final int INPUT = 0;
	 private static final int OUTPUT = 1;
	 private static final int SOLID = 2;
	 
	 public static final int RUINEDBREW = -1;
	 public static final int EMPTY = 0;
	 public static final int WATER = 1;
	 public static final int SWEETWORT = 2;
	 public static final int HOPPEDWORT = 3;
	 public static final int YOUNGCLOUDYBEER = 4;
	 public static final int AGEDCLOUDYBEER = 5;
	 public static final int YOUNGBEER = 6;
	 public static final int AGEDBEER = 7;
	 
	 public static final int DRIEDGRAIN = 1;
	 public static final int HYDRATEDGRAIN = 2;
	 public static final int MILLEDGRAIN = 3;
	 public static final int HOPS = 4;
	 public static final int YEAST = 5;
	 public static final int EGG = 6;
	 

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
             if(slot == SOLID) {
            	 Item solidItem = null;
            	 if(inventory[SOLID] != null) {
            		 solidItem = inventory[SOLID].getItem();
            	 }
            	 if(solidItem == null) {
            		 solidType = EMPTY;
             	 } else if (solidItem.equals(Fermentation.driedGrain)) {
            		 solidType = DRIEDGRAIN;
            	 } else if (solidItem.equals(Fermentation.hydratedGrain)) { 
            		 solidType = HYDRATEDGRAIN;
            	 } else {
            		 solidType = EMPTY;
            	 }
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
        	 this.solidType = tag.getByte("SolidType");
        	 this.solidCount = tag.getByte("SolidCount");
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
             contentTag.setByte("LiquidType", (byte) liquidType);
             contentTag.setByte("LiquidVolume", (byte) liquidVolume);
             contentTag.setByte("SolidType", (byte) solidType);
             contentTag.setByte("SolidCount", (byte) solidCount);
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
			if(++tickCount == 24) {
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
	
	public int getLiquidType() {
		return liquidType;
	}
	
	public int getLiquidVolume() {
		return liquidVolume;
	}
	
	public int getSolidType() {
		return solidType;
	}
	
	public int getSolidCount() {
		return solidCount;
	}
	
	public int getProgress() {
		return progress;
	}
	
	public boolean isActive() {
		return active;
	}
	
	public boolean setLiquidType(int liquidType) {
		this.liquidType = liquidType;
		return true;
	}
	
	public boolean setLiquidVolume(int liquidVolume) {
		this.liquidVolume = liquidVolume;
		return true;
	}
	
	public boolean setSolidType(int solidType) {
		this.solidType = solidType;
		return true;
	}
	
	public boolean setSolidCount(int solidCount) {
		this.solidCount = solidCount;
		return true;
	}
	
	public boolean setProgress(int progress) {
		this.progress = progress;
		updateSlotConfiguration();
		Side side = FMLCommonHandler.instance().getEffectiveSide();
		tickCount = 0;
		
		if(progress == 100) {
			if(inventory[SOLID].getItem().equals(Fermentation.driedGrain) && liquidType == WATER) {
				setInventorySlotContents(SOLID, new ItemStack(Fermentation.hydratedGrain, inventory[SOLID].stackSize));
			} else if (inventory[SOLID].getItem().equals(Fermentation.milledGrain) && liquidType == WATER) {
				setInventorySlotContents(SOLID, null);
				setLiquidType(SWEETWORT);
			}
		}
		
		this.worldObj.updateTileEntityChunkAndDoNothing(this.xCoord, this.yCoord, this.zCoord, this);
		return true;
	}
	
	private void updateSlotConfiguration() {
		updateActive();
		if (inventorySlots[INPUT] == null || inventorySlots[SOLID] == null) {
			return;
		}
		if (progress < 5) {
			inventorySlots[INPUT].setPlayerCanPut(true);
			inventorySlots[SOLID].setPlayerCanTake(true);
		} else if (progress >= 5 && progress < 100) {
			inventorySlots[INPUT].setPlayerCanPut(false);
			inventorySlots[SOLID].setPlayerCanTake(false);
		} else if (progress == 100) {
			inventorySlots[INPUT].setPlayerCanPut(true);
			inventorySlots[SOLID].setPlayerCanTake(true);
		}
	}
	
	public boolean incrementProgress() {
		setProgress(getProgress() + 1);
		return true;
	}
	
	public void updateActive() {
		if(liquidVolume > 0 && solidCount > 0 && progress < 100) {
			active = true;
		} else {
			active = false;
		}
	}
	
	public void onInventoryChanged() {
		if(inventory[INPUT] != null) {
			Item inputItem = inventory[INPUT].getItem();
			Item outputItem = null;
			if(inventory[OUTPUT] != null) {
				outputItem = inventory[OUTPUT].getItem();
			}
			if(inputItem.equals(Item.bucketWater) && liquidVolume < 64 &&(getLiquidType() == WATER || getLiquidType() == EMPTY)) {
				setLiquidType(WATER);
				if(inventory[OUTPUT] == null) {
					setInventorySlotContents(OUTPUT, new ItemStack(Item.bucketEmpty));
					setInventorySlotContents(INPUT, null);
					setLiquidVolume(getLiquidVolume() + 1);
				} else if (outputItem.equals(Item.bucketEmpty) && inventory[OUTPUT].stackSize < 16){
					inventory[OUTPUT].stackSize++;
					setInventorySlotContents(INPUT, null);
					setLiquidVolume(getLiquidVolume() + 1);
				}
			} else if (inputItem.equals(Fermentation.driedGrain) && (inventory[SOLID] == null || inventory[SOLID].getItem().equals(Fermentation.driedGrain))) {
				if (inventory[SOLID] == null) {
					setInventorySlotContents(SOLID, inventory[INPUT]);
					setInventorySlotContents(INPUT, null);
				} else {
					int space = inventory[SOLID].getMaxStackSize() - inventory[SOLID].stackSize;
					int present = inventory[INPUT].stackSize;
					if (space > 0) {
						int transfer = Math.min(space, present);
						inventory[SOLID].stackSize += transfer;
						inventory[INPUT].stackSize -= transfer;
						solidCount = inventory[SOLID].stackSize;
						if(present - transfer == 0) {
							setInventorySlotContents(INPUT, null);
						}
					}
				}
			}
		}
		
		if(inventory[SOLID] != null) {
			setSolidCount(inventory[SOLID].stackSize);
		} else {
			setSolidCount(0);
		}
		
		updateSlotConfiguration();
		
		if (liquidVolume > 0 && solidCount > 0) {
			
		} else {
			tickCount = 0;
			setProgress(0);
		}
	}
}
