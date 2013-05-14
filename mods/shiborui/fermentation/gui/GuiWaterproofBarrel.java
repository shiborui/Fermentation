package mods.shiborui.fermentation.gui;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import mods.shiborui.fermentation.inventory.ContainerTank;
import mods.shiborui.fermentation.inventory.ContainerWaterproofBarrel;
import mods.shiborui.fermentation.tileentity.TileEntityTank;
import mods.shiborui.fermentation.tileentity.TileEntityWaterproofBarrel;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.StatCollector;
import net.minecraftforge.liquids.LiquidDictionary;
import net.minecraftforge.liquids.LiquidStack;

public class GuiWaterproofBarrel extends GuiContainer {

	private ContainerWaterproofBarrel containerTank;
	private TileEntityWaterproofBarrel tileEntity;
	
	public GuiWaterproofBarrel (InventoryPlayer inventoryPlayer, TileEntityWaterproofBarrel tileEntity) {
		super(new ContainerWaterproofBarrel(inventoryPlayer, tileEntity));
		this.tileEntity = tileEntity;
		Side side = FMLCommonHandler.instance().getEffectiveSide();
	}
	
	@Override
    protected void drawGuiContainerForegroundLayer(int param1, int param2) {
            //draw text and stuff here
            //the parameters for drawString are: string, x, y, color
			LiquidStack liquid = tileEntity.getTank().getLiquid();
			String liquidName = LiquidDictionary.findLiquidName(liquid);
			if (liquidName == null) {
				liquidName = "Empty";
			}
			int amount = 0;
			if (liquid != null) {
				amount = liquid.amount;
			}
            fontRenderer.drawString("Waterproof Barrel (Debug)", 8, 6, 4210752);
            fontRenderer.drawString(liquidName + ": " + amount, 8, 16, 4210752);
            //draws "Inventory" or your regional equivalent
            fontRenderer.drawString(StatCollector.translateToLocal("container.inventory"), 8, ySize - 96 + 2, 4210752);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float par1, int par2,
                    int par3) {
            //draw your Gui here, only thing you need to change is the path
            int texture = mc.renderEngine.getTexture("/mods/shiborui/fermentation/textures/gui/WaterproofBarrel.png");
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.mc.renderEngine.bindTexture("/mods/shiborui/fermentation/textures/gui/WaterproofBarrel.png");
            int x = (width - xSize) / 2;
            int y = (height - ySize) / 2;
            this.drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
    }
}
