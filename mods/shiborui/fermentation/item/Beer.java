package mods.shiborui.fermentation.item;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import mods.shiborui.fermentation.Fermentation;

public class Beer extends PotentDrink {
	
	public static final String[] subItemNames = {"Young Cloudy Beer", "Aged Cloudy Beer", "Young Beer", "Aged Beer"};
	
	public Beer(int id) {
		super(id);
		this.setUnlocalizedName("fermentationBeer");
		this.setContainerItem(Fermentation.mug);
		this.setMaxDamage(0);
		this.setMaxStackSize(1);
		this.setHasSubtypes(true);
		potionEffects = new ArrayList<PotionEffect>(1);
		potionEffects.add(new PotionEffect(Potion.confusion.id, 60*20, 0));
		potionEffects.add(new PotionEffect(Potion.resistance.id, 60*20, 1));
	}
	
	@Override
    public void registerIcons(IconRegister iconRegister)
    {
             this.itemIcon = iconRegister.registerIcon("shiborui/fermentation:Beer");
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
		for (int subItem = 0; subItem < 64; subItem++) {
			subItems.add(new ItemStack(this, 1, subItem));
		}
	}
	
}
