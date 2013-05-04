package mods.shiborui.fermentation.item;

import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.BlockCloth;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;

public class BucketBeer extends Item {
	
	public static final String[] subItemNames = {"Young Cloudy Beer", "Aged Cloudy Beer", "Young Beer", "Aged Beer"};
	
	public BucketBeer(int id)
    {
        super(id);
        this.setMaxStackSize(1);
        this.setCreativeTab(CreativeTabs.tabMisc);
        this.setUnlocalizedName("fermentationBucketBeer");
        this.setHasSubtypes(true);
        this.setMaxDamage(0); //suppresses damage bar
    }
	
	@Override
    public void registerIcons(IconRegister iconRegister)
    {
             this.itemIcon = iconRegister.registerIcon("shiborui/fermentation:BucketBeer");
    }
	
	@Override
	public Icon getIconFromDamage(int damage)
    {
        return this.itemIcon;
    }
	
	public String getUnlocalizedName(ItemStack par1ItemStack)
    {
        return super.getUnlocalizedName() + "." + subItemNames[par1ItemStack.getItemDamage()];
    }
	
	@SideOnly(Side.CLIENT)
	public void getSubItems(int par1, CreativeTabs tab, List subItems) {
		for (int subItem = 0; subItem < 4; subItem++) {
			subItems.add(new ItemStack(this, 1, subItem));
		}
	}
}
