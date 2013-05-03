package mods.shiborui.fermentation.item;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class BucketSweetWort extends Item {
	
	public BucketSweetWort(int id)
    {
        super(id);
        this.setMaxStackSize(1);
        this.setCreativeTab(CreativeTabs.tabMisc);
        this.setUnlocalizedName("fermentationBucketSweetWort");
    }
	
	@Override
    public void registerIcons(IconRegister iconRegister)
    {
             this.itemIcon = iconRegister.registerIcon("Fermentation:BucketSweetWort");
    }
}
