package mods.shiborui.fermentation;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.StatCollector;

public class GuiTank extends GuiContainer {
	
	private ContainerTank containerTank;
	private TileEntityTank tileEntity;
	
	public GuiTank (InventoryPlayer inventoryPlayer,
        TileEntityTank tileEntity) {
		//the container is instanciated and passed to the superclass for handling
		super(new ContainerTank(inventoryPlayer, tileEntity));
		this.tileEntity = tileEntity;
		Side side = FMLCommonHandler.instance().getEffectiveSide();
	}
	
	@Override
    protected void drawGuiContainerForegroundLayer(int param1, int param2) {
            //draw text and stuff here
            //the parameters for drawString are: string, x, y, color
			String liquidType;
			switch(tileEntity.getLiquidType()) {
				case 0: liquidType = "Liquid";
						break;
				case 1: liquidType = "Water";
						break;
				case 2: liquidType = "Sweet Wort";
						break;
				default: liquidType = "Invalid";
			}
			String solidType;
			switch(tileEntity.getSolidType()) {
				case 0: solidType = "Solid";
						break;
				case 1: solidType = "Dried Grain";
						break;
				case 2: solidType = "Hydrated Grain";
						break;
				default: solidType = "Invalid";
			}
            fontRenderer.drawString("Tank", 8, 6, 4210752);
            fontRenderer.drawString(liquidType + ": " + tileEntity.getLiquidVolume(), 8, 16, 4210752);
            fontRenderer.drawString(solidType + ": " + tileEntity.getSolidCount(), 8, 26, 4210752);
            fontRenderer.drawString(tileEntity.getProgress() + "%", 8, 36, 4210752);
            fontRenderer.drawString((tileEntity.isActive() ? "Active" : "Inactive"), 8, 46, 4210752);
            //draws "Inventory" or your regional equivalent
            fontRenderer.drawString(StatCollector.translateToLocal("container.inventory"), 8, ySize - 96 + 2, 4210752);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float par1, int par2,
                    int par3) {
            //draw your Gui here, only thing you need to change is the path
            int texture = mc.renderEngine.getTexture("/gui/trap.png");
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.mc.renderEngine.bindTexture("/gui/trap.png");
            int x = (width - xSize) / 2;
            int y = (height - ySize) / 2;
            this.drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
    }
	
}
