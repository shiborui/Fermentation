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
	
	public Beer(int id) {
		super(id);
		this.setUnlocalizedName("fermentationBeer");
		this.setContainerItem(Fermentation.mug);
		this.setMaxDamage(0);
		this.setMaxStackSize(1);
		this.setHasSubtypes(true);
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
	
	@Override
	public List getEffects(ItemStack itemStack) {
		potionEffects = new ArrayList<PotionEffect>(1);
		int metadata = itemStack.getItemDamage();
		int solid = metadata & 3;
		int hops = (metadata & 12) >> 2;
		int age = (metadata & 16) >> 4;
		int purity = (metadata & 32) >> 5;
		potionEffects.add(new PotionEffect(Potion.resistance.id, (solid+1)*60*20, age));
		potionEffects.add(new PotionEffect(Potion.heal.id, 1, 1 + (hops + age)/2));
		potionEffects.add(new PotionEffect(Potion.confusion.id, (solid+1)*10*20, 0));
		if (purity == 0) {
			potionEffects.add(new PotionEffect(Potion.blindness.id, 60*20, 0));
		}
    	return potionEffects;
    }
}
