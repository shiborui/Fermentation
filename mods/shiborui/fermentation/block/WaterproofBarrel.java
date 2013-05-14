package mods.shiborui.fermentation.block;

import mods.shiborui.fermentation.Fermentation;
import mods.shiborui.fermentation.tileentity.TileEntityTank;
import mods.shiborui.fermentation.tileentity.TileEntityWaterproofBarrel;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class WaterproofBarrel extends BlockContainer {
	
	public WaterproofBarrel (int id, Material material) {
        super(id, material);
        setCreativeTab(CreativeTabs.tabBlock);
        setUnlocalizedName("fermentationWaterproofBarrel");
	}
	
	@Override
    public boolean onBlockActivated(World world, int x, int y, int z,
                    EntityPlayer player, int idk, float what, float these, float are) {
            TileEntityWaterproofBarrel tileEntity = (TileEntityWaterproofBarrel) world.getBlockTileEntity(x, y, z);
            
            if (tileEntity == null || player.isSneaking()) {
                    return false;
            }
            player.openGui(Fermentation.instance, 3, world, x, y, z);
            
            tileEntity.sendStateToClient(player);
            
            return true;
    }

	@Override
    public void breakBlock(World world, int x, int y, int z, int par5, int par6) {
            super.breakBlock(world, x, y, z, par5, par6);
    }

	@Override
	public TileEntity createNewTileEntity(World world) {
	        return new TileEntityWaterproofBarrel();
	}
	
	public boolean hasTileEntity(int metadata)
	{
	    return true;
	}

	@Override
    public void registerIcons(IconRegister iconRegister)
    {
             this.blockIcon = iconRegister.registerIcon("shiborui/fermentation:WaterproofBarrel");
    }
}
