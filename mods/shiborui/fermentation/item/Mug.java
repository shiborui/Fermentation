package mods.shiborui.fermentation.item;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.util.Icon;

public class Mug extends Item {
	
        public Mug(int id) {
                super(id);
             // Constructor Configuration
                setMaxStackSize(16);
                setCreativeTab(CreativeTabs.tabMisc);
                setUnlocalizedName("fermentationMug");
        }
 
        @Override
        public void registerIcons(IconRegister iconRegister)
        {
                 this.itemIcon = iconRegister.registerIcon("shiborui/fermentation:Mug");
        }
}