package mods.shiborui.fermentation;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class MaltedGrain extends Item {
	public MaltedGrain(int id) {
		super(id);
		setMaxStackSize(64);
        setCreativeTab(CreativeTabs.tabMisc);
        setUnlocalizedName("fermentationMaltedGrain");
	}
	
	@Override
    public void registerIcons(IconRegister iconRegister)
    {
             this.itemIcon = iconRegister.registerIcon("Fermentation:MaltedGrain");
    }
}
