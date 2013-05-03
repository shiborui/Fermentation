package mods.shiborui.fermentation;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class DriedGrain extends Item {
	public DriedGrain(int id) {
		super(id);
		setMaxStackSize(64);
        setCreativeTab(CreativeTabs.tabMisc);
        setUnlocalizedName("fermentationDriedGrain");
	}
	
	@Override
    public void registerIcons(IconRegister iconRegister)
    {
             this.itemIcon = iconRegister.registerIcon("Fermentation:DriedGrain");
    }
}
