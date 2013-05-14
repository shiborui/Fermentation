package mods.shiborui.fermentation.gui;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;

import mods.shiborui.fermentation.inventory.ContainerTank;
import mods.shiborui.fermentation.tileentity.TileEntityTank;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.StatCollector;
import net.minecraftforge.liquids.LiquidDictionary;
import net.minecraftforge.liquids.LiquidStack;

public class GuiTank extends GuiContainer {
	
	private ContainerTank containerTank;
	private TileEntityTank tileEntity;
	
	public GuiTank (InventoryPlayer inventoryPlayer, TileEntityTank tileEntity) {
		//the container is instanciated and passed to the superclass for handling
		super(new ContainerTank(inventoryPlayer, tileEntity));
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
        fontRenderer.drawString("Tank (Debug)", 8, 6, 4210752);
        fontRenderer.drawString(liquidName + ": " + amount, 8, 16, 4210752);
        fontRenderer.drawString(tileEntity.getProgress() + "%", 8, 36, 4210752);
        fontRenderer.drawString((tileEntity.isActive() ? "Active" : "Inactive"), 8, 46, 4210752);
        //draws "Inventory" or your regional equivalent
        fontRenderer.drawString(StatCollector.translateToLocal("container.inventory"), 8, ySize - 96 + 2, 4210752);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float par1, int par2,
                    int par3) {
            //draw your Gui here, only thing you need to change is the path
            int texture = mc.renderEngine.getTexture("/mods/shiborui/fermentation/textures/gui/Tank.png");
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.mc.renderEngine.bindTexture("/mods/shiborui/fermentation/textures/gui/Tank.png");
            int x = (width - xSize) / 2;
            int y = (height - ySize) / 2;
            this.drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
    }
	
}
