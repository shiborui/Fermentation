package mods.shiborui.fermentation;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.Random;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.relauncher.Side;

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
            
            //packet building
            Side side = FMLCommonHandler.instance().getEffectiveSide();
            
            if(side == Side.SERVER) {
            	ByteArrayOutputStream bos = new ByteArrayOutputStream(8);
                DataOutputStream outputStream = new DataOutputStream(bos);
                try {
                		outputStream.writeInt(x);
                        outputStream.writeInt(y);
                        outputStream.writeInt(z);
                        outputStream.writeByte(tileEntity.getLiquidType());
                        outputStream.writeByte(tileEntity.getLiquidVolume());
                        outputStream.writeByte(tileEntity.getSolidType());
                        outputStream.writeByte(tileEntity.getSolidCount());
                        outputStream.writeByte(tileEntity.getProgress());
                } catch (Exception ex) {
                        ex.printStackTrace();
                }

                Packet250CustomPayload packet = new Packet250CustomPayload();
                packet.channel = "FermentationTank";
                packet.data = bos.toByteArray();
                packet.length = bos.size();
                PacketDispatcher.sendPacketToPlayer(packet, (Player) player);
            }
            
            return true;
    }
	
	@Override
    public void breakBlock(World world, int x, int y, int z, int par5, int par6) {
            dropItems(world, x, y, z);
            super.breakBlock(world, x, y, z, par5, par6);
    }
	
	private void dropItems(World world, int x, int y, int z){
        /*Random rand = new Random();

        TileEntity tileEntity = world.getBlockTileEntity(x, y, z);
        if (!(tileEntity instanceof IInventory)) {
                return;
        }
        IInventory inventory = (IInventory) tileEntity;

        for (int i = 0; i < inventory.getSizeInventory(); i++) {
                ItemStack item = inventory.getStackInSlot(i);

                if (item != null && item.stackSize > 0) {
                        float rx = rand.nextFloat() * 0.8F + 0.1F;
                        float ry = rand.nextFloat() * 0.8F + 0.1F;
                        float rz = rand.nextFloat() * 0.8F + 0.1F;

                        EntityItem entityItem = new EntityItem(world,
                                        x + rx, y + ry, z + rz,
                                        new ItemStack(item.itemID, item.stackSize, item.getItemDamage()));

                        if (item.hasTagCompound()) {
                                entityItem.func_92014_d().setTagCompound((NBTTagCompound) item.getTagCompound().copy());
                        }

                        float factor = 0.05F;
                        entityItem.motionX = rand.nextGaussian() * factor;
                        entityItem.motionY = rand.nextGaussian() * factor + 0.2F;
                        entityItem.motionZ = rand.nextGaussian() * factor;
                        world.spawnEntityInWorld(entityItem);
                        item.stackSize = 0;
                }
        }*/
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
             this.blockIcon = iconRegister.registerIcon("Fermentation:Tank");
    }
}
