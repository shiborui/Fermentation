package mods.shiborui.fermentation.gui;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import mods.shiborui.fermentation.inventory.ContainerYeastBin;
import mods.shiborui.fermentation.tileentity.TileEntityYeastBin;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.StatCollector;

public class GuiYeastBin extends GuiContainer {

	private ContainerYeastBin containerYeastBin;
	private TileEntityYeastBin tileEntity;
	
	public GuiYeastBin (InventoryPlayer inventoryPlayer,
        TileEntityYeastBin tileEntity) {
		//the container is instanciated and passed to the superclass for handling
		super(new ContainerYeastBin(inventoryPlayer, tileEntity));
		this.tileEntity = tileEntity;
		Side side = FMLCommonHandler.instance().getEffectiveSide();
	}
	
	@Override
    protected void drawGuiContainerForegroundLayer(int param1, int param2) {
            //draw text and stuff here
            //the parameters for drawString are: string, x, y, color
			
            fontRenderer.drawString("Yeast Bin (Debug)", 8, 6, 4210752);
            //draws "Inventory" or your regional equivalent
            fontRenderer.drawString(StatCollector.translateToLocal("container.inventory"), 8, ySize - 96 + 2, 4210752);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float par1, int par2,
                    int par3) {
            //draw your Gui here, only thing you need to change is the path
            int texture = mc.renderEngine.getTexture("/mods/shiborui/fermentation/textures/gui/YeastBin.png");
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.mc.renderEngine.bindTexture("/mods/shiborui/fermentation/textures/gui/YeastBin.png");
            int x = (width - xSize) / 2;
            int y = (height - ySize) / 2;
            this.drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
    }

}
