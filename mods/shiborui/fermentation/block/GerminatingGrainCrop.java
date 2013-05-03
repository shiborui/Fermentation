package mods.shiborui.fermentation.block;

import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mods.shiborui.fermentation.Fermentation;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Icon;
import net.minecraft.world.World;

public class GerminatingGrainCrop extends Block {
	@SideOnly(Side.CLIENT)
    private Icon[] iconArray;
	
		public GerminatingGrainCrop(int id) {
			super(id, Material.plants);
			this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F , 0.2F, 1.0F);
			this.setTickRandomly(true);
		}
	
		public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
	        return null;
	    }
	        
	    public int getRenderType() {
	        return 6; // Magic number.
	    }
	    
	    public boolean isOpaqueCube() {
	        return false;
	    }
	    
	    /**
	     * Returns the ID of the items to drop on destruction.
	     */
	    public int idDropped(int growthStage, Random par2Random, int par3)
	    {
	        return growthStage == 1 ? Fermentation.germinatedGrain.itemID : Fermentation.hydratedGrain.itemID;
	    }

	    /**
	     * Returns the quantity of items to drop on block destruction.
	     */
	    public int quantityDropped(Random par1Random)
	    {
	        return 1;
	    }
	    
	    /**
	     * Ticks the block if it's been scheduled
	     */
	    public void updateTick(World par1World, int par2, int par3, int par4, Random par5Random)
	    {
	        super.updateTick(par1World, par2, par3, par4, par5Random);

	        if (par1World.getBlockLightValue(par2, par3 + 1, par4) >= 9)
	        {
	            int l = par1World.getBlockMetadata(par2, par3, par4);

	            if (l < 1)
	            {
	                if (par5Random.nextInt(2) == 0)
	                {
	                    ++l;
	                    par1World.setBlockMetadataWithNotify(par2, par3, par4, l, 2);
	                }
	            }
	        }
	    }
	    
	    @Override
	    public void registerIcons(IconRegister iconRegister)
	    {
	    	this.iconArray = new Icon[2];
	        this.iconArray[0] = iconRegister.registerIcon("Fermentation:GerminatingGrainCrop");
	        this.iconArray[1] = iconRegister.registerIcon("Fermentation:GerminatedGrainCrop");
	    }
	    
	    @SideOnly(Side.CLIENT)

	    /**
	     * From the specified side and block metadata retrieves the blocks texture. Args: side, metadata
	     */
	    public Icon getIcon(int par1, int par2)
	    {
	        if (par2 < 0 || par2 > 1)
	        {
	            par2 = 1;
	        }

	        return this.iconArray[par2];
	    }
}
