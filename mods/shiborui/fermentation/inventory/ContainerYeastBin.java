package mods.shiborui.fermentation.inventory;

import java.util.HashSet;

import mods.shiborui.fermentation.Fermentation;
import mods.shiborui.fermentation.tileentity.TileEntityYeastBin;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ContainerYeastBin extends Container {

	protected TileEntityYeastBin tileEntity;
	protected RestrictedSlot potatoSlot;
	protected RestrictedSlot waterSlot;
	protected RestrictedSlot yeastSlot;
	
	public ContainerYeastBin (InventoryPlayer inventoryPlayer, TileEntityYeastBin te){
        tileEntity = te;
        
        HashSet allowedPotato = new HashSet();
        allowedPotato.add(Item.potato);
        
        HashSet allowedWater = new HashSet();
        allowedWater.add(Item.bucketWater);
        
        HashSet allowedYeast = new HashSet();
        allowedYeast.add(Fermentation.yeast);

        //the Slot constructor takes the IInventory and the slot number in that it binds to
        //and the x-y coordinates it resides on-screen
        addSlotToContainer(potatoSlot = new RestrictedSlot(tileEntity, 0, 62, 17, allowedPotato, true, true));
        addSlotToContainer(waterSlot = new RestrictedSlot(tileEntity, 1, 62, 53, allowedWater, true, true));
        addSlotToContainer(yeastSlot = new RestrictedSlot(tileEntity, 2, 116, 17, allowedYeast, true, true));

        //commonly used vanilla code that adds the player's inventory
        bindPlayerInventory(inventoryPlayer);
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
	public boolean canInteractWith(EntityPlayer player) {
		return tileEntity.isUseableByPlayer(player);
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
