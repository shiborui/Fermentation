package mods.shiborui.fermentation;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.IGuiHandler;

public class GuiHandler implements IGuiHandler {
	
	//returns an instance of the Container you made earlier
    @Override
    public Object getServerGuiElement(int id, EntityPlayer player, World world,
                    int x, int y, int z) {
            TileEntity tileEntity = world.getBlockTileEntity(x, y, z);
            if (tileEntity instanceof TileEntityTank) {
                return new ContainerTank(player.inventory, (TileEntityTank) tileEntity);
            } else if (tileEntity instanceof TileEntityYeastBin) {
            	return new ContainerYeastBin(player.inventory, (TileEntityYeastBin) tileEntity);
            }
            return null;
    }

    //returns an instance of the Gui you made earlier
    @Override
    public Object getClientGuiElement(int id, EntityPlayer player, World world,
                    int x, int y, int z) {
            TileEntity tileEntity = world.getBlockTileEntity(x, y, z);
            if(tileEntity instanceof TileEntityTank) {
                return new GuiTank(player.inventory, (TileEntityTank) tileEntity);
            } else if (tileEntity instanceof TileEntityYeastBin) {
            	return new GuiYeastBin(player.inventory, (TileEntityYeastBin) tileEntity);
            }
            return null;

    }

}
