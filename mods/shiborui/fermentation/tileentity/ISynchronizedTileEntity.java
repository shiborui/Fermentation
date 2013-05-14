package mods.shiborui.fermentation.tileentity;

import cpw.mods.fml.common.network.Player;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.packet.Packet250CustomPayload;

public interface ISynchronizedTileEntity {
	
	public void sendStateToClient(EntityPlayer player);
	
	public void handleServerState(Packet250CustomPayload packet, Player playerEntity);
}
