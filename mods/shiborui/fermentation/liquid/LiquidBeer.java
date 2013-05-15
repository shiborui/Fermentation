package mods.shiborui.fermentation.liquid;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import mods.shiborui.fermentation.Fermentation;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.liquids.ILiquid;
import net.minecraftforge.liquids.LiquidContainerData;
import net.minecraftforge.liquids.LiquidContainerRegistry;
import net.minecraftforge.liquids.LiquidDictionary;
import net.minecraftforge.liquids.LiquidStack;

public class LiquidBeer extends Item implements ILiquid {
	
	public static final String[] solidPrefixes = {"Watery ", "", "Strong ", "Thick "};
	public static final String[] hopsInfixes = {"White ", "", "Full ", "Dark "};
	public static final String[] ageSuffixes = {"Brew", "Beer"};
	public static final String[] purityFlags = {" (Cloudy)", ""};
	
	private int metadata = 0;
	
	public LiquidBeer(int id) {
		this(id, 0);
	}
	
	public LiquidBeer(int id, int metadata) {
		super(id);
		setUnlocalizedName("fermentationLBeer");
		setCreativeTab(CreativeTabs.tabMisc);
		setMaxStackSize(1);
		this.metadata = metadata;
		this.setHasSubtypes(true);
	}

	@Override
	public int stillLiquidId() {
		return this.itemID;
	}

	@Override
	public boolean isMetaSensitive() {
		return true;
	}

	@Override
	public int stillLiquidMeta() {
		return this.metadata;
	}
	
	@Override
    public void registerIcons(IconRegister iconRegister)
    {
             this.itemIcon = iconRegister.registerIcon("shiborui/fermentation:TestLiquid");
    }
	
	public static String getNameFromDamage(int damage) {
		return getPrefixFromDamage(damage) + getInfixFromDamage(damage) + getSuffixFromDamage(damage) + getFlagFromDamage(damage);
	}
	
	public static String getPrefixFromDamage(int damage) {
		int solid = damage & 3;
		return solidPrefixes[solid];
	}
	
	public static String getInfixFromDamage(int damage) {
		int hops = (damage & 12) >> 2;
		return hopsInfixes[hops];
	}
	
	public static String getSuffixFromDamage(int damage) { 
		int age = (damage & 16) >> 4;
		return ageSuffixes[age];
	}
	
	public static String getFlagFromDamage(int damage) {
		int purity = (damage & 32) >> 5;
		return purityFlags[purity];
	}

}
