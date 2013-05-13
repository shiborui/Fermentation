package mods.shiborui.fermentation;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

import mods.shiborui.fermentation.tileentity.TileEntityKettle;
import mods.shiborui.fermentation.tileentity.TileEntityTank;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.relauncher.Side;

public class PacketHandler implements IPacketHandler{
	
	@Override
	public void onPacketData(INetworkManager manager,
        Packet250CustomPayload packet, Player playerEntity) {
		
		if (packet.channel.equals("FmtnTank")) {
			handleTank(packet, playerEntity);
		} else if (packet.channel.equals("FmtnKettle")) {
			handleKettle(packet, playerEntity);
		}
	}
	
	private void handleTank(Packet250CustomPayload packet, Player playerEntity) {
		Side side = FMLCommonHandler.instance().getEffectiveSide();
		
		DataInputStream inputStream = new DataInputStream(new ByteArrayInputStream(packet.data));
		
		int x, y, z;
		byte liquidType, liquidVolume, solidType, solidCount, progress;
		
		try {
			x = inputStream.readInt();
			y = inputStream.readInt();
			z = inputStream.readInt();
			liquidType = inputStream.readByte();
			liquidVolume = inputStream.readByte();
			solidType = inputStream.readByte();
			solidCount = inputStream.readByte();
			progress = inputStream.readByte();
			
			if (side == Side.CLIENT) {
				EntityPlayer entityPlayer = (EntityPlayer) playerEntity;
				TileEntityTank tankTileEntity = (TileEntityTank) entityPlayer.worldObj.getBlockTileEntity(x, y, z);
				tankTileEntity.setLiquidType(liquidType);
				tankTileEntity.setLiquidVolume(liquidVolume);
				tankTileEntity.setSolidType(solidType);
				tankTileEntity.setSolidCount(solidCount);
				tankTileEntity.setProgress(progress);
			}
			
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
	}
	
	private void handleKettle(Packet250CustomPayload packet, Player playerEntity) {
		Side side = FMLCommonHandler.instance().getEffectiveSide();
		
		DataInputStream inputStream = new DataInputStream(new ByteArrayInputStream(packet.data));
		
		int x, y, z;
		byte liquidType, liquidVolume, progress;
		int kettleBurnTime, storedHeat;
		
		try {
			x = inputStream.readInt();
			y = inputStream.readInt();
			z = inputStream.readInt();
			liquidType = inputStream.readByte();
			liquidVolume = inputStream.readByte();
			kettleBurnTime = inputStream.readInt();
			storedHeat = inputStream.readInt();
			progress = inputStream.readByte();
			
			if (side == Side.CLIENT) {
				EntityPlayer entityPlayer = (EntityPlayer) playerEntity;
				TileEntityKettle kettleTileEntity = (TileEntityKettle) entityPlayer.worldObj.getBlockTileEntity(x, y, z);
				kettleTileEntity.setLiquidType(liquidType);
				kettleTileEntity.setLiquidVolume(liquidVolume);
				kettleTileEntity.setKettleBurnTime(kettleBurnTime);
				kettleTileEntity.setStoredHeat(storedHeat);
				kettleTileEntity.setProgress(progress);
			}
			
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
	}
}
