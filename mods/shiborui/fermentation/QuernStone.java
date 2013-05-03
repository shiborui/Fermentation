package mods.shiborui.fermentation;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class QuernStone extends Item {
	public QuernStone(int id) {
        super(id);
        // Constructor Configuration
        setMaxStackSize(16);
        setCreativeTab(CreativeTabs.tabMisc);
        setUnlocalizedName("fermentationQuernStone");
	}
	
	@Override
	public boolean doesContainerItemLeaveCraftingGrid(ItemStack itemStack) {
		return false;
	}
	
	@Override
	public void registerIcons(IconRegister iconRegister)
	{
	         this.itemIcon = iconRegister.registerIcon("Fermentation:QuernStone");
	}
}
