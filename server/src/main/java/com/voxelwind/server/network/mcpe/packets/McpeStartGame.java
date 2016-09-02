package com.voxelwind.server.network.mcpe.packets;

import com.flowpowered.math.vector.Vector3f;
import com.flowpowered.math.vector.Vector3i;
import com.voxelwind.server.network.mcpe.McpeUtil;
import com.voxelwind.server.network.NetworkPackage;
import io.netty.buffer.ByteBuf;
import lombok.Data;

@Data
public class McpeStartGame implements NetworkPackage {
    private int seed;
    private byte dimension;
    private int generator;
    private int gamemode;
    private long entityId;
    private Vector3i levelSpawnLocation;
    private Vector3f spawnLocation;
    private boolean inCreative;
    private boolean isEduMode;
    private float rainLevel = 0;
    private float lightningLevel = 0;
    private boolean commandsEnabled = false;
    private byte difficulty;

    @Override
    public void decode(ByteBuf buffer) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void encode(ByteBuf buffer) {
        McpeUtil.writeUnsignedVarInt(buffer, (int) entityId); //EntityUniqueID
        McpeUtil.writeUnsignedVarInt(buffer, (int) entityId); //EntityRuntimeID
        McpeUtil.writeVector3f(buffer, spawnLocation);
        McpeUtil.writeVector3i(buffer, levelSpawnLocation);
        McpeUtil.writeUnsignedVarInt(buffer, seed);
        McpeUtil.writeUnsignedVarInt(buffer, dimension);
        McpeUtil.writeUnsignedVarInt(buffer, generator);
        McpeUtil.writeUnsignedVarInt(buffer, gamemode);
        McpeUtil.writeUnsignedVarInt(buffer, difficulty); //Difficulty
        buffer.writeBoolean(inCreative); //has been loaded in creative
        McpeUtil.writeUnsignedVarInt(buffer, -1); //dayCycleStopTime
        buffer.writeBoolean(isEduMode); //edu mode
        buffer.writeFloat(rainLevel); //rain level
        buffer.writeFloat(lightningLevel); //lightning level
        buffer.writeBoolean(commandsEnabled); //commands enabled
        McpeUtil.writeVarIntString(buffer, "UNKNOWN");
    }
}
