package mods.shiborui.fermentation;

import mods.shiborui.fermentation.gui.GuiKettle;
import mods.shiborui.fermentation.gui.GuiTank;
import mods.shiborui.fermentation.gui.GuiWaterproofBarrel;
import mods.shiborui.fermentation.gui.GuiYeastBin;
import mods.shiborui.fermentation.inventory.ContainerKettle;
import mods.shiborui.fermentation.inventory.ContainerTank;
import mods.shiborui.fermentation.inventory.ContainerWaterproofBarrel;
import mods.shiborui.fermentation.inventory.ContainerYeastBin;
import mods.shiborui.fermentation.tileentity.TileEntityKettle;
import mods.shiborui.fermentation.tileentity.TileEntityTank;
import mods.shiborui.fermentation.tileentity.TileEntityWaterproofBarrel;
import mods.shiborui.fermentation.tileentity.TileEntityYeastBin;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.IGuiHandler;

public class GuiHandler implements IGuiHandler {
	
    @Override
    public Object getServerGuiElement(int id, EntityPlayer player, World world,
                    int x, int y, int z) {
            TileEntity tileEntity = world.getBlockTileEntity(x, y, z);
            if (tileEntity instanceof TileEntityTank) {
                return new ContainerTank(player.inventory, (TileEntityTank) tileEntity);
            } else if (tileEntity instanceof TileEntityYeastBin) {
            	return new ContainerYeastBin(player.inventory, (TileEntityYeastBin) tileEntity);
            } else if (tileEntity instanceof TileEntityKettle) {
            	return new ContainerKettle(player.inventory, (TileEntityKettle) tileEntity);
            } else if (tileEntity instanceof TileEntityWaterproofBarrel) {
            	return new ContainerWaterproofBarrel(player.inventory, (TileEntityWaterproofBarrel) tileEntity);
            }
            return null;
    }

    @Override
    public Object getClientGuiElement(int id, EntityPlayer player, World world,
                    int x, int y, int z) {
            TileEntity tileEntity = world.getBlockTileEntity(x, y, z);
            if(tileEntity instanceof TileEntityTank) {
                return new GuiTank(player.inventory, (TileEntityTank) tileEntity);
            } else if (tileEntity instanceof TileEntityYeastBin) {
            	return new GuiYeastBin(player.inventory, (TileEntityYeastBin) tileEntity);
            } else if (tileEntity instanceof TileEntityKettle) {
            	return new GuiKettle(player.inventory, (TileEntityKettle) tileEntity);
            } else if (tileEntity instanceof TileEntityWaterproofBarrel) {
            	return new GuiWaterproofBarrel(player.inventory, (TileEntityWaterproofBarrel) tileEntity);
            }
            return null;
    }
}
