package mods.shiborui.fermentation.block;

import java.util.Random;

import mods.shiborui.fermentation.Fermentation;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.world.World;

public class VineScaffold extends Block {

	public VineScaffold(int id) {
		super(id, Material.plants);
		setCreativeTab(CreativeTabs.tabBlock);
        setUnlocalizedName("fermentationVineScaffold");
		this.setTickRandomly(true);
	}
	
	public int getRenderType() {
        return 6; // crops
    }
    
    public boolean isOpaqueCube() {
        return false;
    }
    
    @Override
    /**
     * Returns the ID of the items to drop on destruction.
     */
    public int idDropped(int growthStage, Random random, int par3)
    {
        return Fermentation.vineScaffoldID;
    }

    @Override
    /**
     * Returns the quantity of items to drop on block destruction.
     */
    public int quantityDropped(Random random)
    {
        return 1;
    }
	
    @Override
    public void registerIcons(IconRegister iconRegister)
    {
             this.blockIcon = iconRegister.registerIcon("shiborui/fermentation:VineScaffold");
    }
    
    @Override
    public void onNeighborBlockChange (World world, int x, int y, int z,
            int neighborId) {
        if (!canBlockStay(world, x, y, z)) {
            dropBlockAsItem(world, x, y, z, world.getBlockMetadata(x, y, z), 0);
            world.setBlock(x, y, z, 0);
        }
    }
    
}
