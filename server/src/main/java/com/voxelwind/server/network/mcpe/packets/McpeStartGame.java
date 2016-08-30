package com.voxelwind.server.network.mcpe.packets;

import com.flowpowered.math.vector.Vector3f;
import com.flowpowered.math.vector.Vector3i;
import com.voxelwind.server.network.mcpe.McpeUtil;
import com.voxelwind.server.network.NetworkPackage;
import io.netty.buffer.ByteBuf;
import lombok.Data;

import javax.xml.bind.DatatypeConverter;

@Data
public class McpeStartGame implements NetworkPackage {
    private static final byte[] UNKNOWN = DatatypeConverter.parseHexBinary("01010000000000000000000000");
    private int seed;
    private byte dimension;
    private int generator;
    private int gamemode;
    private long entityId;
    private Vector3f spawnLocation;

    @Override
    public void decode(ByteBuf buffer) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void encode(ByteBuf buffer) {
        McpeUtil.writeVarInt(buffer, 0); //EntityUniqueID
        McpeUtil.writeVarInt(buffer, (int) entityId); //EntityRuntimeID (basically just the normal entityID)
        McpeUtil.writeVector3f(buffer, spawnLocation);
        McpeUtil.writeVarInt(buffer, seed);
        buffer.writeByte(dimension);
        buffer.writeByte(generator);
        buffer.writeByte(gamemode);
        buffer.writeByte(0); //Difficulty (TODO)
        buffer.writeByte(0); //has been loaded in creative
        buffer.writeByte(0); //edu mode
        buffer.writeByte(0); //rain level
        buffer.writeByte(0); //lightning level
        buffer.writeByte(0); //commands enabled
        buffer.writeBytes(UNKNOWN);
    }
}
