package mods.shiborui.fermentation;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;

public class WaterproofBarrel extends Block {
	public WaterproofBarrel (int id, Material material) {
        super(id, material);
        setCreativeTab(CreativeTabs.tabBlock);
        setUnlocalizedName("fermentationWaterproofBarrel");
	}

	@Override
    public void registerIcons(IconRegister iconRegister)
    {
             this.blockIcon = iconRegister.registerIcon("Fermentation:WaterproofBarrel");
    }
}
