package mods.shiborui.fermentation.item;

import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;

public class BucketHoppedWort extends Item {
	
	public BucketHoppedWort(int id)
    {
        super(id);
        this.setMaxStackSize(1);
        this.setCreativeTab(CreativeTabs.tabMisc);
        this.setUnlocalizedName("fermentationBucketHoppedWort");
        this.setHasSubtypes(true);
        this.setMaxDamage(0);
    }
	
	@Override
    public void registerIcons(IconRegister iconRegister)
    {
             this.itemIcon = iconRegister.registerIcon("shiborui/fermentation:BucketHoppedWort");
    }
	
	@Override
	public Icon getIconFromDamage(int damage)
    {
        return this.itemIcon;
    }
	
	public String getUnlocalizedName(ItemStack par1ItemStack)
    {
        return super.getUnlocalizedName() + "." + par1ItemStack.getItemDamage();
    }
	
	@SideOnly(Side.CLIENT)
	public void getSubItems(int par1, CreativeTabs tab, List subItems) {
		for (int subItem = 0; subItem < 16; subItem++) {
			subItems.add(new ItemStack(this, 1, subItem));
		}
	}
}
