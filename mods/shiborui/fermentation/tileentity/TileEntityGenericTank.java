package mods.shiborui.fermentation.tileentity;

import cpw.mods.fml.common.network.Player;
import mods.shiborui.fermentation.Fermentation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.liquids.ILiquidTank;
import net.minecraftforge.liquids.ITankContainer;
import net.minecraftforge.liquids.LiquidContainerRegistry;
import net.minecraftforge.liquids.LiquidStack;
import net.minecraftforge.liquids.LiquidTank;

public class TileEntityGenericTank extends TileEntity implements ITankContainer, ISynchronizedTileEntity {
	
	private LiquidTank tank;
	
	public TileEntityGenericTank() {
		tank = new LiquidTank(null, LiquidContainerRegistry.BUCKET_VOLUME * 8, this);
	}
	
	public int fill(LiquidStack resource) {
		return fill(0, resource, true);
	}

	@Override
	public int fill(ForgeDirection from, LiquidStack resource, boolean doFill) {
		return fill(0, resource, doFill);
	}

	@Override
	public int fill(int tankIndex, LiquidStack resource, boolean doFill) {
		if (tankIndex != 0 || resource == null) {
			return 0;
		}
		
		resource = resource.copy();
		
		LiquidStack liquid = this.tank.getLiquid();
		
		if (liquid != null && liquid.amount > 0 && !liquid.isLiquidEqual(resource)) {
			return 0;
		}
		
		return this.tank.fill(resource, doFill);
	}

	@Override
	public LiquidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
		return drain(0, maxDrain, doDrain);
	}

	@Override
	public LiquidStack drain(int tankIndex, int maxDrain, boolean doDrain) {
		return this.tank.drain(maxDrain, doDrain);
	}

	@Override
	public LiquidTank[] getTanks(ForgeDirection direction) {
		LiquidTank[] tanks = {this.tank};
		return tanks;
	}

	@Override
	public LiquidTank getTank(ForgeDirection direction, LiquidStack type) {
		return this.tank;
	}
	
	public LiquidTank getTank() {
		return this.tank;
	}
	
	public ItemStack[] transferLiquid(ItemStack inputStack, ItemStack outputStack) {
		boolean transferMade = false;
		ItemStack outputFilled = LiquidContainerRegistry.fillLiquidContainer(this.getTank().getLiquid(), inputStack);
		if (outputFilled != null && (outputStack == null || outputStack.stackSize < outputStack.getMaxStackSize() && outputStack.isItemEqual(outputFilled))) {
			this.getTank().drain(LiquidContainerRegistry.getLiquidForFilledItem(outputFilled).amount, true);
			if (--inputStack.stackSize == 0) {
				inputStack = null;
			}
			if (outputStack == null) {
				outputStack = outputFilled;
			} else {
				outputStack.stackSize++;
			}
			transferMade = true;
		} else if (LiquidContainerRegistry.isFilledContainer(inputStack)) {
			if (this.getTank().fill(LiquidContainerRegistry.getLiquidForFilledItem(inputStack), false) >= LiquidContainerRegistry.getLiquidForFilledItem(inputStack).amount) {
				ItemStack outputEmptied = new ItemStack(inputStack.getItem().getContainerItem(), 1);
				if (outputStack == null || outputEmptied.isItemEqual(outputStack) && outputStack.stackSize < outputStack.getMaxStackSize()) {
					this.getTank().fill(LiquidContainerRegistry.getLiquidForFilledItem(inputStack), true);
					if (--inputStack.stackSize == 0) {
						inputStack = null;
					}
					if (outputStack == null) {
						outputStack = outputEmptied;
					} else {
						outputStack.stackSize++;
					}
				}
				transferMade = true;
			}
		}
		ItemStack[] inventory = {inputStack, outputStack};
		return inventory;
	}

	@Override
	public void sendStateToClient(EntityPlayer player) {
		
	}

	@Override
	public void handleServerState(Packet250CustomPayload packet,
			Player playerEntity) {
		
	}
}
