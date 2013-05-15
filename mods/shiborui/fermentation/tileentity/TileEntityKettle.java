package mods.shiborui.fermentation.tileentity;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
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
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraftforge.liquids.LiquidContainerRegistry;
import net.minecraftforge.liquids.LiquidDictionary;
import net.minecraftforge.liquids.LiquidStack;
import net.minecraftforge.liquids.LiquidTank;

public class TileEntityKettle extends TileEntityGenericTank implements IInventory {
	private ItemStack[] inventory;
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
        int liquidVolume = tag.getInteger("LiquidVolume");
        ((LiquidTank) this.getTank()).setLiquid(LiquidDictionary.getLiquid(tag.getString("LiquidType"), liquidVolume));
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
            LiquidStack liquid = this.getTank().getLiquid();

            if (liquid != null) {
            	contentTag.setInteger("LiquidVolume", liquid.amount);
                contentTag.setString("LiquidType", LiquidDictionary.findLiquidName(liquid));
            } else {
            	contentTag.setInteger("LiquidVolume", 0);
                contentTag.setString("LiquidType", "Empty");
            }
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
			if (this.getTank().getLiquid() != null) {
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
		this.tickCount = 0;
		
		if (progress >= 100 & inventory[HOPS] != null) {
			Side side = FMLCommonHandler.instance().getEffectiveSide();
			if (side == Side.SERVER) {
				int liquid = this.getTank().getLiquid().amount;
				int solid = this.getTank().getLiquid().itemMeta;
				int hops = inventory[HOPS].stackSize;
				solid = (int) Math.pow(2, solid) * (liquid / LiquidContainerRegistry.BUCKET_VOLUME) + hops;
				int logSolidRatio = (int) Math.min(Math.max((Math.log((double) solid / (liquid / LiquidContainerRegistry.BUCKET_VOLUME)) / Math.log(2)), 0), 3);
				int logHopsRatio = 3 - (int) Math.min(Math.max((Math.log((double) solid / hops) / Math.log(2)), 0), 3);
				int metadata = logSolidRatio + (logHopsRatio << 2);
				this.getTank().setLiquid(new LiquidStack(Fermentation.liquidHoppedWort.itemID, this.getTank().getLiquid().amount, metadata));
				inventory[HOPS] = null;
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
		if (inventorySlots[INPUT] == null || inventorySlots[HOPS] == null) {
			return;
		}
		if (progress < 5 || progress == 100) {
			inventorySlots[INPUT].setPlayerCanPut(true);
			inventorySlots[HOPS].setPlayerCanPut(true);
			inventorySlots[HOPS].setPlayerCanTake(true);
		} else if (progress >= 5 && progress < 100) {
			inventorySlots[INPUT].setPlayerCanPut(false);
			inventorySlots[HOPS].setPlayerCanPut(false);
			inventorySlots[HOPS].setPlayerCanTake(false);
		}
	}
	
	public boolean incrementProgress() {
		setProgress(this.progress + 1);
		return true;
	}
	
	public void updateActive() {
		if(this.getTank().getLiquid() != null && inventory[HOPS] != null && progress < 100 
				&& (this.kettleBurnTime > 0 || inventory[FUEL] != null || this.storedHeat > 0)) {
			active = true;
		} else {
			active = false;
		}
	}
	
	public int getProgress() {
		return this.progress;
	}
	
	public int getKettleTemperature() {
		if (this.getTank().getLiquid() != null) {
			return Math.min(20 + this.storedHeat  / this.getTank().getLiquid().amount, 100);
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
		if (this.inventory[HOPS] == null || this.getTank().getLiquid() == null) {
			return false;
		} else if (this.getTank().getLiquidName().substring(this.getTank().getLiquidName().length() - 10).equals("Sweet Wort")) {
			return true;
		} else {
			return false;
		}
	}
	
	public int getItemBurnTime(ItemStack item) {
		return TileEntityFurnace.getItemBurnTime(item);
	}
	
	public boolean isBoiling() {
		if (this.getKettleTemperature() == 100 && this.getTank().getLiquidName().substring(this.getTank().getLiquidName().length() - 10).equals("Sweet Wort") && inventory[HOPS] != null) {
			return true;
		} else {
			return false;
		}
	}
	
	public void onInventoryChanged() {
		
		if (inventory[INPUT] != null) {
			int oldVolume = 0;
			if (this.getTank().getLiquid() != null) {
				oldVolume = this.getTank().getLiquid().amount;
			}
			ItemStack[] newStacks = this.transferLiquid(inventory[INPUT], inventory[OUTPUT]);
			if (this.getTank().getLiquid() != null && oldVolume > this.getTank().getLiquid().amount) {
				this.setStoredHeat((int) ((double) this.getStoredHeat() * this.getTank().getLiquid().amount / oldVolume));
			}
			inventory[INPUT] = newStacks[INPUT];
			inventory[OUTPUT] = newStacks[OUTPUT];
		}
		
		updateSlotConfiguration();
		
		if (this.getTank().getLiquid() != null && inventory[HOPS] != null) {
			
		} else {
			tickCount = 0;
			if (this.getTank().getLiquid() != null) {
				if (!this.getTank().getLiquidName().equals("Hopped Wort")) {
					setProgress(0);
				}
			} else {
				this.setStoredHeat(0);
			}
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
				kettleBurnTime = inputStream.readInt();
				storedHeat = inputStream.readInt();
				progress = inputStream.readByte();
				
				LiquidStack liquid = LiquidDictionary.getLiquid(liquidName, liquidVolume);
				
				this.getTank().setLiquid(liquid);
				this.setKettleBurnTime(kettleBurnTime);
				this.setStoredHeat(storedHeat);
				this.setProgress(progress);
				
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
		}
	}
}
