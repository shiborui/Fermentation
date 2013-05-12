package mods.shiborui.fermentation.block;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.relauncher.Side;
import mods.shiborui.fermentation.Fermentation;
import mods.shiborui.fermentation.TileEntityTank;
import mods.shiborui.fermentation.TileEntityYeastBin;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class YeastBin extends BlockContainer {

	public YeastBin (int id, Material material) {
        super(id, material);
        this.setCreativeTab(CreativeTabs.tabBlock);
        this.setUnlocalizedName("fermentationYeastBin");
        this.setTickRandomly(true);
	}
	
	@Override
    public void breakBlock(World world, int x, int y, int z, int par5, int par6) {
            super.breakBlock(world, x, y, z, par5, par6);
    }
	
	@Override
    public boolean onBlockActivated(World world, int x, int y, int z,
                    EntityPlayer player, int idk, float what, float these, float are) {
            TileEntityYeastBin tileEntity = (TileEntityYeastBin) world.getBlockTileEntity(x, y, z);
            
            if (tileEntity == null || player.isSneaking()) {
                    return false;
            }
            player.openGui(Fermentation.instance, 1, world, x, y, z);
         
            return true;
    }
	
	@Override
	public TileEntity createNewTileEntity(World world) {
	        return new TileEntityYeastBin();
	}
	
	public boolean hasTileEntity(int metadata)
	{
	    return true;
	}

	@Override
    public void registerIcons(IconRegister iconRegister)
    {
             this.blockIcon = iconRegister.registerIcon("shiborui/fermentation:YeastBin");
    }

}
