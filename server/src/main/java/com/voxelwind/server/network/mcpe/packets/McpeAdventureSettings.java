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
        flags = McpeUtil.readUnsignedVarInt(buffer);
        playerPermissions = McpeUtil.readUnsignedVarInt(buffer);
        globalPermissions = McpeUtil.readUnsignedVarInt(buffer);
    }

    @Override
    public void encode(ByteBuf buffer) {
        McpeUtil.writeUnsignedVarInt(buffer, flags);
        McpeUtil.writeUnsignedVarInt(buffer, playerPermissions);
        McpeUtil.writeUnsignedVarInt(buffer, globalPermissions);
    }
}
