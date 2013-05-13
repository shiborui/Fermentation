package mods.shiborui.fermentation.item;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;
import mods.shiborui.fermentation.Fermentation;

public class Beer extends PotentDrink {
	
	public Beer(int id) {
		super(id);
		this.setUnlocalizedName("fermentationBeer");
		this.setContainerItem(Fermentation.mug);
		potionEffects = new ArrayList<PotionEffect>(1);
		potionEffects.add(new PotionEffect(Potion.confusion.id, 60*20, 0));
		potionEffects.add(new PotionEffect(Potion.resistance.id, 60*20, 1));
	}
	
}
