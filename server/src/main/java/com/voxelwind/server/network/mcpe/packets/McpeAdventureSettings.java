package com.voxelwind.server.network.mcpe.packets;

import com.voxelwind.server.network.NetworkPackage;
import com.voxelwind.server.network.mcpe.McpeUtil;
import io.netty.buffer.ByteBuf;
import lombok.Data;

@Data
public class McpeAdventureSettings implements NetworkPackage {
    private int flags;
    private int playerPermissions;
    private int globalPermissions;

    @Override
    public void decode(ByteBuf buffer) {
        flags = McpeUtil.readVarInt(buffer);
        playerPermissions = McpeUtil.readVarInt(buffer);
        globalPermissions = McpeUtil.readVarInt(buffer);
    }

    @Override
    public void encode(ByteBuf buffer) {
        McpeUtil.writeVarInt(buffer, flags);
        McpeUtil.writeVarInt(buffer, playerPermissions);
        McpeUtil.writeVarInt(buffer, globalPermissions);
    }
}
