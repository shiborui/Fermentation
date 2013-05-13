package mods.shiborui.fermentation.inventory;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class RestrictedSlot extends Slot {

	private Set<Item> allowedItems = new HashSet<Item>(8);
	private boolean playerCanTake = true;
	private boolean playerCanPut = true;
	
	public RestrictedSlot(IInventory inventory, int slotID, int x, int y) {
		super(inventory, slotID, x, y);
		// TODO Auto-generated constructor stub
	}
	
	public RestrictedSlot(IInventory inventory, int slotID, int x, int y, 
			HashSet<Item> allowedItems, boolean playerCanTake, boolean playerCanPut) {
		super(inventory, slotID, x, y);
		this.allowedItems = allowedItems;
		this.playerCanTake = playerCanTake;
		this.playerCanPut = playerCanPut;
	}
	
	@Override
	/**
     * Check if the stack is a valid item for this slot. Always true beside for the armor slots.
     */
    public boolean isItemValid(ItemStack itemStack)
    {
		if (playerCanPut && (allowedItems.contains(itemStack.getItem()) || allowedItems.isEmpty())) {
			return true;
		} else {
			return false;
		}
    }
	
	@Override
	/**
     * Return whether this slot's stack can be taken from this slot.
     */
    public boolean canTakeStack(EntityPlayer entityPlayer)
    {
        return playerCanTake;
    }
	
	public void setPlayerCanTake(boolean playerCanTake) {
		this.playerCanTake = playerCanTake;
	}
	
	public void setPlayerCanPut(boolean playerCanPut) {
		this.playerCanPut = playerCanPut;
	}
	
	public void setAllowedItems(Set<Item> allowedItems) {
		this.allowedItems = allowedItems;
	}
	
	public void allowItem(Item item) {
		allowedItems.add(item);
	}
	
	public void disallowItem(Item item) {
		allowedItems.remove(item);
	}
	
	public boolean canPlayerTake() {
		return playerCanTake;
	}
	
	public boolean canPlayerPut() {
		return playerCanPut;
	}
	
	public Set<Item> getAllowedItems() {
		return allowedItems;
	}

}
