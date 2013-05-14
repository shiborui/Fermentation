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
	public LiquidHoppedWort(int id) {
		super(id);
		setUnlocalizedName("fermentationLHoppedWort");
		setCreativeTab(CreativeTabs.tabMisc);
		setMaxStackSize(1);


        LiquidDictionary.getOrCreateLiquid("Hopped Wort", new LiquidStack(this.itemID, 1, 0));
        LiquidContainerData containerData = new LiquidContainerData(new LiquidStack(this.itemID, LiquidContainerRegistry.BUCKET_VOLUME, 0), 
        		new ItemStack(Fermentation.bucketHoppedWort), new ItemStack(Item.bucketEmpty));
        LiquidContainerRegistry.registerLiquid(containerData);
	}

	@Override
	public int stillLiquidId() {
		return this.itemID;
	}

	@Override
	public boolean isMetaSensitive() {
		return false;
	}

	@Override
	public int stillLiquidMeta() {
		return 0;
	}
	
	@Override
    public void registerIcons(IconRegister iconRegister)
    {
             this.itemIcon = iconRegister.registerIcon("shiborui/fermentation:TestLiquid");
    }
}
