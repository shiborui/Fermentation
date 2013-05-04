package mods.shiborui.fermentation.block;

import java.util.Random;

import mods.shiborui.fermentation.Fermentation;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.common.IPlantable;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class HopsVine extends VineScaffold{
	
	@SideOnly(Side.CLIENT)
    private Icon[] iconArray;

	public HopsVine(int id) {
		super(id);
		setCreativeTab(CreativeTabs.tabBlock);
        setUnlocalizedName("fermentationHopsVine");
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

            if (l < 2)
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
    	this.iconArray = new Icon[3];
        this.iconArray[0] = iconRegister.registerIcon("shiborui/fermentation:VineYoung");
        this.iconArray[1] = iconRegister.registerIcon("shiborui/fermentation:VineMature");
        this.iconArray[2] = iconRegister.registerIcon("shiborui/fermentation:HopsVineFruit");
    }
    
    @SideOnly(Side.CLIENT)

    /**
     * From the specified side and block metadata retrieves the blocks texture. Args: side, metadata
     */
    public Icon getIcon(int par1, int par2)
    {
        if (par2 < 0 || par2 > 2)
        {
            par2 = 1;
        }

        return this.iconArray[par2];
    }
    
    public boolean onBlockActivated(World world, int x, int y, int z,
            EntityPlayer player, int idk, float what, float these, float are) {
    	if (player.isSneaking() || world.getBlockMetadata(x, y, z) < 2 || Side.CLIENT == FMLCommonHandler.instance().getEffectiveSide()) {
            return false;
    	}
    	
    	ItemStack drop = new ItemStack(Fermentation.hops);
    	
    	EntityItem entityItem = new EntityItem(world,
                player.posX, player.posY, player.posZ,
                new ItemStack(drop.itemID, drop.stackSize, drop.getItemDamage()));
    	
    	world.spawnEntityInWorld(entityItem);
    	
    	world.setBlockMetadataWithNotify(x, y, z, 1, 3);
    	
    	return true;
    }
    
    @Override
    public boolean canBlockStay (World world, int x, int y, int z) {
        Block soil = blocksList[world.getBlockId(x, y - 1, z)];
        return (soil != null && soil.canSustainPlant(world, x, y - 1, z,
                        ForgeDirection.UP, (IPlantable) Fermentation.vineAssembly));
    }
}
