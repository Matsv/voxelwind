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
        McpeUtil.writeVarInt(buffer, (int) entityId); //EntityUniqueID
        McpeUtil.writeVarInt(buffer, (int) entityId); //EntityRuntimeID
        McpeUtil.writeVector3f(buffer, spawnLocation);
        McpeUtil.writeVector3i(buffer, levelSpawnLocation);
        McpeUtil.writeVarInt(buffer, seed);
        McpeUtil.writeVarInt(buffer, dimension);
        McpeUtil.writeVarInt(buffer, generator);
        McpeUtil.writeVarInt(buffer, gamemode);
        McpeUtil.writeVarInt(buffer, difficulty); //Difficulty
        buffer.writeBoolean(inCreative); //has been loaded in creative
        McpeUtil.writeVarInt(buffer, -1); //dayCycleStopTime
        buffer.writeBoolean(isEduMode); //edu mode
        buffer.writeFloat(rainLevel); //rain level
        buffer.writeFloat(lightningLevel); //lightning level
        buffer.writeBoolean(commandsEnabled); //commands enabled
        McpeUtil.writeVarIntString(buffer, "UNKNOWN");
    }
}
