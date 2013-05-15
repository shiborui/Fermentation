package mods.shiborui.fermentation.item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import mods.shiborui.fermentation.Fermentation;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionHelper;
import net.minecraft.util.Icon;
import net.minecraft.world.World;

public class PotentDrink extends Item{
	
	protected ArrayList<PotionEffect> potionEffects;

	public PotentDrink(int id)
    {
        super(id);
        this.setMaxStackSize(1);
        this.setHasSubtypes(true);
        this.setMaxDamage(0);
        this.setUnlocalizedName("fermentationPotentDrink");
        this.setCreativeTab(CreativeTabs.tabBrewing);
        this.setContainerItem(Fermentation.mug);
    }
	
	/**
     * How long it takes to use or consume an item
     */
    public int getMaxItemUseDuration(ItemStack par1ItemStack)
    {
        return 32;
    }

    /**
     * returns the action that specifies what animation to play when the items is being used
     */
    public EnumAction getItemUseAction(ItemStack par1ItemStack)
    {
        return EnumAction.drink;
    }
    
    public ItemStack onEaten(ItemStack itemStack, World world, EntityPlayer player) {
    	if (!world.isRemote) {
    		System.out.println(player.getHealth());
			List list = this.getEffects(itemStack);

            if (list != null)
            {
                Iterator iterator = list.iterator();

                while (iterator.hasNext())
                {
                    PotionEffect potioneffect = (PotionEffect)iterator.next();
                    player.addPotionEffect(new PotionEffect(potioneffect));
                }
            }
            System.out.println(player.getHealth());
		}
    	return new ItemStack(this.getContainerItem());
    }
    
    public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer player) {
    	player.setItemInUse(itemStack, this.getMaxItemUseDuration(itemStack));
        return itemStack;
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
    
    public List getEffects(ItemStack itemStack) {
    	return potionEffects;
    }
	
}
