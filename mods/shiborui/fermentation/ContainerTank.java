package mods.shiborui.fermentation;

import java.util.HashSet;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

public class ContainerTank extends Container {

	protected TileEntityTank tileEntity;
	protected RestrictedSlot inputSlot;
	protected RestrictedSlot outputSlot;
	protected RestrictedSlot solidSlot;

    public ContainerTank (InventoryPlayer inventoryPlayer, TileEntityTank te){
            tileEntity = te;

            //the Slot constructor takes the IInventory and the slot number in that it binds to
            //and the x-y coordinates it resides on-screen
            addSlotToContainer(inputSlot = new RestrictedSlot(tileEntity, 0, 62, 17));
            addSlotToContainer(outputSlot = new RestrictedSlot(tileEntity, 1, 80, 17));
            addSlotToContainer(solidSlot = new RestrictedSlot(tileEntity, 2, 98, 17));
            
            te.registerInventorySlot(inputSlot, 0);
            te.registerInventorySlot(outputSlot, 1);
            te.registerInventorySlot(solidSlot, 2);
            
            HashSet allowedInput = new HashSet();
            allowedInput.add(Item.bucketWater);
            allowedInput.add(Item.bucketEmpty);
            allowedInput.add(Fermentation.driedGrain);
            inputSlot.setAllowedItems(allowedInput);
            
            outputSlot.setPlayerCanPut(false);
            
            solidSlot.setPlayerCanPut(false);
            solidSlot.setPlayerCanTake(false);

            //commonly used vanilla code that adds the player's inventory
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
