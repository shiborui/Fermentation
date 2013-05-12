package mods.shiborui.fermentation;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;

public class TileEntityKettle extends TileEntity implements IInventory {
	private ItemStack[] inventory;
	private int liquidType;
	private int liquidVolume;
	private int kettleBurnTime = 0; //Number of ticks kettle will keep burning
	private int currentFuelBurnTime = 0; //The number of ticks that a fresh copy of the currently-burning item would keep the kettle burning for
	private int storedHeat = 0; //Heat added by fuel to liquid
	private int progress = 0; //0 to 100, increments when boiling
	private int tickCount = 0; //for finer progress resolution
	private boolean active = true; //should anything happen on ticks?
	
	private RestrictedSlot[] inventorySlots = new RestrictedSlot[4];
	
	private static final int INPUT = 0;
	private static final int OUTPUT = 1;
	private static final int HOPS = 2;
	private static final int FUEL = 3;
	
	private static final int EMPTY = 0;
	private static final int SWEETWORT = 1;
	private static final int HOPPEDWORT = 2;
	
	public TileEntityKettle() {
		inventory = new ItemStack[4];
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
       	 this.kettleBurnTime = tag.getInteger("KettleBurnTime");
       	 this.storedHeat = tag.getInteger("StoredHeat");
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
            contentTag.setByte("LiquidType", (byte) this.liquidType);
            contentTag.setByte("LiquidVolume", (byte) this.liquidVolume);
            contentTag.setInteger("KettleBurnTime", this.kettleBurnTime);
            contentTag.setInteger("StoredHeat", this.storedHeat);
            contentTag.setByte("Progress", (byte) progress);
            tagCompound.setTag("Content", contentTag);
    }
    
    @Override
    public String getInvName() {
            return "fermentation.tileEntityKettle";
    }

	@Override
	public boolean isInvNameLocalized() {
		return false;
	}
	
	@Override
	public void updateEntity() {
		
		//Update heat stored and temperature
		int oldTemp = this.getKettleTemperature();
		if (this.kettleBurnTime > 0) {
			this.kettleBurnTime--;
			if (this.liquidVolume > 0) {
				this.storedHeat += 100;
			}
		} else if (storedHeat > 0) {
			this.storedHeat -= 10;
		}
		if (this.getKettleTemperature() != oldTemp) {
			this.worldObj.updateTileEntityChunkAndDoNothing(this.xCoord, this.yCoord, this.zCoord, this);
		}
		
    	//Burn next fuel item if necessary and possible
    	if (this.kettleBurnTime == 0 && this.canBoil()) {
    		this.currentFuelBurnTime = getItemBurnTime(this.inventory[FUEL]);
    		this.kettleBurnTime = this.currentFuelBurnTime;
    		if (this.kettleBurnTime > 0) {
    			if (this.inventory[FUEL] != null) {
    				this.inventory[FUEL].stackSize--;
    				if (this.inventory[FUEL].stackSize == 0) {
    					//this might cause synchronization bugs
    					//if it does, try making it server-side only
    					this.inventory[FUEL] = null;
    				}
    			}
    		}
    	}
    	//Update progress if boiling
    	if (this.isBoiling()) {
    		if (++this.tickCount == 24) {
    			this.incrementProgress();
    			tickCount = 0;
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
	
	public boolean setLiquidType(int liquidType) {
		this.liquidType = liquidType;
		return true;
	}
	
	public boolean setLiquidVolume(int liquidVolume) {
		if (liquidVolume < this.liquidVolume) {
			this.storedHeat = (int) (this.storedHeat * ((double) liquidVolume) / this.liquidVolume);
		}
		this.liquidVolume = liquidVolume;
		if (liquidVolume == 0) {
			liquidType = EMPTY;
		}
		return true;
	}
	
	public boolean setKettleBurnTime(int kettleBurnTime) {
		this.kettleBurnTime = kettleBurnTime;
		return true;
	}
	
	public boolean setStoredHeat(int storedHeat) {
		this.storedHeat = storedHeat;
		return true;
	}
	
	public boolean setProgress(int progress) {
		this.progress = progress;
		
		if (progress >= 100 & inventory[HOPS] != null) {
			this.liquidType = HOPPEDWORT;
			inventory[HOPS] = null;
		}
		
		updateSlotConfiguration();
		this.worldObj.updateTileEntityChunkAndDoNothing(this.xCoord, this.yCoord, this.zCoord, this);
		return true;
	}
	
	private void updateSlotConfiguration() {
		updateActive();
		if (inventorySlots[INPUT] == null || inventorySlots[HOPS] == null) {
			return;
		}
		if (progress < 5) {
			inventorySlots[INPUT].setPlayerCanPut(true);
			inventorySlots[HOPS].setPlayerCanTake(true);
		} else if (progress >= 5 && progress < 100) {
			inventorySlots[INPUT].setPlayerCanPut(false);
			inventorySlots[HOPS].setPlayerCanTake(false);
		} else if (progress == 100) {
			inventorySlots[INPUT].setPlayerCanPut(true);
			inventorySlots[HOPS].setPlayerCanTake(true);
		}
	}
	
	public boolean incrementProgress() {
		setProgress(this.progress + 1);
		return true;
	}
	
	public void updateActive() {
		if(liquidVolume > 0 && inventory[HOPS] != null && progress < 100 
				&& (this.kettleBurnTime > 0 || inventory[FUEL] != null || this.storedHeat > 0)) {
			active = true;
		} else {
			active = false;
		}
	}
	
	public int getLiquidType() {
		return this.liquidType;
	}
	
	public int getLiquidVolume() {
		return this.liquidVolume;
	}
	
	public int getProgress() {
		return this.progress;
	}
	
	public int getKettleTemperature() {
		if (this.liquidVolume > 0) {
			return Math.min(20 + this.storedHeat / 1000 / this.liquidVolume, 100);
		} else {
			return 20;
		}
	}
	
	public int getStoredHeat() {
		return this.storedHeat;
	}
	
	public boolean isActive() {
		return this.active;
	}
	
	public int getKettleBurnTime() {
		return this.kettleBurnTime;
	}
	
	public boolean canBoil() {
		if (this.inventory[HOPS] == null || this.liquidVolume == 0) {
			return false;
		} else {
			return true;
		}
	}
	
	public int getItemBurnTime(ItemStack item) {
		return TileEntityFurnace.getItemBurnTime(item);
	}
	
	public boolean isBoiling() {
		if (this.getKettleTemperature() == 100 && liquidType == SWEETWORT && inventory[HOPS] != null) {
			return true;
		} else {
			return false;
		}
	}
	
	public void onInventoryChanged() {
		
		if (inventory[INPUT] != null) {
			if (liquidVolume < 64 && inventory[INPUT].getItem().equals(Fermentation.bucketSweetWort)) {
				if (this.liquidType == SWEETWORT || this.liquidType == EMPTY) {
					this.setLiquidType(SWEETWORT);
					if (inventory[OUTPUT] == null) {
						setInventorySlotContents(OUTPUT, new ItemStack(Item.bucketEmpty));
						setInventorySlotContents(INPUT, null);
						setLiquidVolume(liquidVolume + 1);
					} else if (inventory[OUTPUT].getItem().equals(Item.bucketEmpty) && inventory[OUTPUT].stackSize < 16) {
						inventory[OUTPUT].stackSize++;
						setInventorySlotContents(INPUT, null);
						setLiquidVolume(liquidVolume + 1);
					}
				}
			} else if (inventory[INPUT].getItem().equals(Item.bucketEmpty) && liquidVolume > 0 && inventory[OUTPUT] == null) {
				switch(liquidType) {
				case SWEETWORT:
					setInventorySlotContents(OUTPUT, new ItemStack(Fermentation.bucketSweetWort));
					break;
				case HOPPEDWORT:
					setInventorySlotContents(OUTPUT, new ItemStack(Fermentation.bucketHoppedWort));
					break;
				}
				if(inventory[INPUT].stackSize > 1) {
					inventory[INPUT].stackSize--;
				} else {
					setInventorySlotContents(INPUT, null);
				}
				setLiquidVolume(liquidVolume - 1);
			} else if (inventory[INPUT].getItem().equals(Fermentation.hops)) {
				if (inventory[HOPS] == null) {
					setInventorySlotContents(HOPS, inventory[INPUT]);
					setInventorySlotContents(INPUT, null);
				} else {
					int space = inventory[HOPS].getMaxStackSize() - inventory[HOPS].stackSize;
					int present = inventory[INPUT].stackSize;
					if (space > 0) {
						int transfer = Math.min(space, present);
						inventory[HOPS].stackSize += transfer;
						inventory[INPUT].stackSize -= transfer;
						if(present - transfer == 0) {
							setInventorySlotContents(INPUT, null);
						}
					}
				}
			}
		}
		
		updateSlotConfiguration();
		
		if (liquidVolume > 0 && inventory[HOPS] != null) {
			
		} else {
			tickCount = 0;
			if (liquidVolume == 0) {
				setStoredHeat(0);
			}
			if (liquidType != HOPPEDWORT) {
				setProgress(0);
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
                    outputStream.writeByte(this.liquidType);
                    outputStream.writeByte(this.liquidVolume);
                    outputStream.writeInt(this.kettleBurnTime);
                    outputStream.writeInt(this.storedHeat);
                    outputStream.writeByte(this.progress);
            } catch (Exception ex) {
                    ex.printStackTrace();
            }

            Packet250CustomPayload packet = new Packet250CustomPayload();
            packet.channel = "FmtnKettle";
            packet.data = bos.toByteArray();
            packet.length = bos.size();
            PacketDispatcher.sendPacketToPlayer(packet, (Player) player);
        }
	}
	
}
