package mods.shiborui.fermentation.inventory;

import java.util.HashSet;

import mods.shiborui.fermentation.Fermentation;
import mods.shiborui.fermentation.tileentity.TileEntityWaterproofBarrel;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ContainerWaterproofBarrel extends Container {
	
	protected TileEntityWaterproofBarrel tileEntity;
	protected RestrictedSlot inputSlot;
	protected RestrictedSlot outputSlot;
	
	public ContainerWaterproofBarrel (InventoryPlayer inventoryPlayer, TileEntityWaterproofBarrel te) {
		tileEntity = te;
		
		addSlotToContainer(inputSlot = new RestrictedSlot(tileEntity, 0, 62, 17));
        addSlotToContainer(outputSlot = new RestrictedSlot(tileEntity, 1, 62, 53));
        
        te.registerInventorySlot(inputSlot, 0);
        te.registerInventorySlot(outputSlot, 1);
        
        HashSet allowedInput = new HashSet();
        allowedInput.add(Item.bucketWater);
        allowedInput.add(Fermentation.bucketSweetWort);
        allowedInput.add(Fermentation.bucketHoppedWort);
        allowedInput.add(Fermentation.bucketBeer);
        allowedInput.add(Item.bucketEmpty);
        allowedInput.add(Fermentation.mug);
        inputSlot.setAllowedItems(allowedInput);
        
        outputSlot.setPlayerCanPut(false);
        
        bindPlayerInventory(inventoryPlayer);
	}

	@Override
    public boolean canInteractWith(EntityPlayer player) {
            return tileEntity.isUseableByPlayer(player);
    }
    
    protected void bindPlayerInventory(InventoryPlayer inventoryPlayer) {
        for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 9; j++) {
                        addSlotToContainer(new Slot(inventoryPlayer, j + i * 9 + 9,
                                        8 + j * 18, 84 + i * 18));
                }
        }

        for (int i = 0; i < 9; i++) {
                addSlotToContainer(new Slot(inventoryPlayer, i, 8 + i * 18, 142));
        }
    }
    
    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int slot) {
            ItemStack stack = null;
            Slot slotObject = (Slot) inventorySlots.get(slot);

            //null checks and checks if the item can be stacked (maxStackSize > 1)
            if (slotObject != null && slotObject.getHasStack()) {
                    ItemStack stackInSlot = slotObject.getStack();
                    stack = stackInSlot.copy();

                    //merges the item into player inventory since its in the tileEntity
                    if (slot < 3) {
                            if (!this.mergeItemStack(stackInSlot, 3, 39, true)) {
                                    return null;
                            }
                    }
                    //places it into the tileEntity is possible since its in the player inventory
                    else if (!this.mergeItemStack(stackInSlot, 0, 3, false)) {
                            return null;
                    }

                    if (stackInSlot.stackSize == 0) {
                            slotObject.putStack(null);
                    } else {
                            slotObject.onSlotChanged();
                    }

                    if (stackInSlot.stackSize == stack.stackSize) {
                            return null;
                    }
                    slotObject.onPickupFromSlot(player, stackInSlot);
            }
            return stack;
    }

}
