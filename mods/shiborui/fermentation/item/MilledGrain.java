package mods.shiborui.fermentation.item;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class MilledGrain extends Item {
	public MilledGrain(int id) {
		super(id);
		setMaxStackSize(64);
        setCreativeTab(CreativeTabs.tabMisc);
        setUnlocalizedName("fermentationMilledGrain");
	}
	
	@Override
    public void registerIcons(IconRegister iconRegister)
    {
             this.itemIcon = iconRegister.registerIcon("Fermentation:MilledGrain");
    }
}
