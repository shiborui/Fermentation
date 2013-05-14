package mods.shiborui.fermentation;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

import mods.shiborui.fermentation.tileentity.ISynchronizedTileEntity;
import mods.shiborui.fermentation.tileentity.TileEntityKettle;
import mods.shiborui.fermentation.tileentity.TileEntityTank;
import mods.shiborui.fermentation.tileentity.TileEntityWaterproofBarrel;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.liquids.LiquidDictionary;
import net.minecraftforge.liquids.LiquidStack;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.relauncher.Side;

public class PacketHandler implements IPacketHandler{
	
	@Override
	public void onPacketData(INetworkManager manager,
        Packet250CustomPayload packet, Player playerEntity) {
		
		if (packet.channel.equals("FmtnTank") || 
				packet.channel.equals("FmtnKettle") || 
				packet.channel.equals("FmtnWPBarrel")) {
			handleSynchronizedTileEntity(packet, playerEntity);
		}
	}
	
	private void handleSynchronizedTileEntity(Packet250CustomPayload packet, Player playerEntity) {
		Side side = FMLCommonHandler.instance().getEffectiveSide();
		
		if (side == Side.CLIENT) {
			DataInputStream inputStream = new DataInputStream(new ByteArrayInputStream(packet.data));
			
			int x, y, z;
			
			try {
				x = inputStream.readInt();
				y = inputStream.readInt();
				z = inputStream.readInt();
				
				((ISynchronizedTileEntity) ((EntityPlayer) playerEntity).worldObj.getBlockTileEntity(x, y, z)).handleServerState(packet, playerEntity);
				
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
		}
	}
}
