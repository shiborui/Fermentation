package mods.shiborui.fermentation.liquid;

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

public class LiquidHoppedWort extends Item implements ILiquid {

	private int metadata = 0;
	
	public LiquidHoppedWort(int id) {
		this(id, 0);
	}
	
	public LiquidHoppedWort(int id, int metadata) {
		super(id);
		setUnlocalizedName("fermentationLHoppedWort");
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
		return metadata;
	}
	
	@Override
    public void registerIcons(IconRegister iconRegister)
    {
             this.itemIcon = iconRegister.registerIcon("shiborui/fermentation:TestLiquid");
    }
	
	public static String getNameFromDamage(int damage) {
		return LiquidBeer.getPrefixFromDamage(damage) + LiquidBeer.getInfixFromDamage(damage) + "Hopped Wort";
	}
}
