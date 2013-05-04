package mods.shiborui.fermentation.item;

import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mods.shiborui.fermentation.Fermentation;
import mods.shiborui.fermentation.block.VineScaffold;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemSeeds;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.common.IPlantable;

public class VineAssembly extends ItemSeeds implements IPlantable {
	
	private int blockType;
	
	public static final String[] subItemNames = {"Vine Assembly", "Hops Vine Assembly"};
	
	public VineAssembly(int id, int blockType, int soilBlockID) {
		super(id, blockType, soilBlockID);
		this.blockType = blockType;
		setMaxStackSize(64);
        setCreativeTab(CreativeTabs.tabMisc);
        setUnlocalizedName("fermentationVineAssembly");
        this.setHasSubtypes(true);
        this.setMaxDamage(0); //suppresses damage bar
	}
	
	public int getBlockType(ItemStack stack) {
		switch (stack.getItemDamage()) {
		case 1:
			return Fermentation.hopsVine.blockID;
		default:
			return Fermentation.vineScaffold.blockID;
		}
	}
	
	@Override
    public void registerIcons(IconRegister iconRegister)
    {
             this.itemIcon = iconRegister.registerIcon("shiborui/fermentation:VineAssembly");
    }
	
	@Override
	public boolean onItemUse(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, World par3World, int par4, int par5, int par6, int par7, float par8, float par9, float par10) {
		if (par7 != 1)
        {
            return false;
        }
		
        else if (par2EntityPlayer.canPlayerEdit(par4, par5, par6, par7, par1ItemStack) && par2EntityPlayer.canPlayerEdit(par4, par5 + 1, par6, par7, par1ItemStack))
        {
            int i1 = par3World.getBlockId(par4, par5, par6);
            Block soil = Block.blocksList[i1];
            if (soil != null && soil.canSustainPlant(par3World, par4, par5, par6, ForgeDirection.UP, this) && par3World.isAirBlock(par4, par5 + 1, par6))
            {
                par3World.setBlock(par4, par5 + 1, par6, this.getBlockType(par1ItemStack));
                --par1ItemStack.stackSize;
                return true;
            }
            else
            {
                return false;
            }
        }
        else
        {
            return false;
        }
	}
	
	@Override
    public EnumPlantType getPlantType(World world, int x, int y, int z)
    {
        return EnumPlantType.Crop;
    }
	
	@Override
    public int getPlantID(World world, int x, int y, int z)
    {
        return blockType;
    }

    @Override
    public int getPlantMetadata(World world, int x, int y, int z)
    {
        return 0;
    }
    
    public String getUnlocalizedName(ItemStack par1ItemStack)
    {
        return super.getUnlocalizedName() + "." + subItemNames[par1ItemStack.getItemDamage()];
    }
    
    @SideOnly(Side.CLIENT)
	public void getSubItems(int par1, CreativeTabs tab, List subItems) {
		for (int subItem = 0; subItem < 2; subItem++) {
			subItems.add(new ItemStack(this, 1, subItem));
		}
	}
}
