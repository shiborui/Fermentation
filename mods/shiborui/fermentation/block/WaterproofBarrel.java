package mods.shiborui.fermentation.block;

import java.util.Random;

import mods.shiborui.fermentation.Fermentation;
import mods.shiborui.fermentation.tileentity.TileEntityTank;
import mods.shiborui.fermentation.tileentity.TileEntityWaterproofBarrel;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class WaterproofBarrel extends BlockContainer {
	
	public WaterproofBarrel (int id, Material material) {
        super(id, material);
        this.setCreativeTab(CreativeTabs.tabBlock);
        this.setUnlocalizedName("fermentationWaterproofBarrel");
        this.setHardness(2.5F);
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
		dropItems(world, x, y, z);
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
	
	public int idDropped(int damage, Random random, int zero) {
        return Fermentation.waterproofBarrel.blockID;
	}
	
	private void dropItems(World world, int x, int y, int z){
        Random rand = new Random();
        System.out.println("dropItems");

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
                                item.setTagCompound((NBTTagCompound) item.getTagCompound().copy());
                        }

                        float factor = 0.05F;
                        entityItem.motionX = rand.nextGaussian() * factor;
                        entityItem.motionY = rand.nextGaussian() * factor + 0.2F;
                        entityItem.motionZ = rand.nextGaussian() * factor;
                        world.spawnEntityInWorld(entityItem);
                        item.stackSize = 0;
                }
        }
	}
}
