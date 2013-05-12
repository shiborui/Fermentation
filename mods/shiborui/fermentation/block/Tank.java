package mods.shiborui.fermentation.block;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.Random;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.relauncher.Side;

import mods.shiborui.fermentation.Fermentation;
import mods.shiborui.fermentation.TileEntityTank;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class Tank extends BlockContainer {
	public Tank (int id, Material material) {
        super(id, material);
        setCreativeTab(CreativeTabs.tabBlock);
        setUnlocalizedName("fermentationTank");
	}
	
	@Override
    public boolean onBlockActivated(World world, int x, int y, int z,
                    EntityPlayer player, int idk, float what, float these, float are) {
            TileEntityTank tileEntity = (TileEntityTank) world.getBlockTileEntity(x, y, z);
            
            if (tileEntity == null || player.isSneaking()) {
                    return false;
            }
            player.openGui(Fermentation.instance, 0, world, x, y, z);
            
            tileEntity.sendStateToClient(player);
            
            return true;
    }
	
	@Override
    public void breakBlock(World world, int x, int y, int z, int par5, int par6) {
            super.breakBlock(world, x, y, z, par5, par6);
    }

	@Override
	public TileEntity createNewTileEntity(World world) {
	        return new TileEntityTank();
	}
	
	public boolean hasTileEntity(int metadata)
	{
	    return true;
	}

	@Override
    public void registerIcons(IconRegister iconRegister)
    {
             this.blockIcon = iconRegister.registerIcon("shiborui/fermentation:Tank");
    }
}
