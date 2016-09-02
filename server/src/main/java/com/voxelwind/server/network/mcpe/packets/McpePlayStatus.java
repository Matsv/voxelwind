package com.voxelwind.server.network.mcpe.packets;

import com.voxelwind.server.network.NetworkPackage;
import com.voxelwind.server.network.mcpe.McpeUtil;
import io.netty.buffer.ByteBuf;
import lombok.Data;

@Data
public class McpePlayStatus implements NetworkPackage {
    private Status status;

    @Override
    public void decode(ByteBuf buffer) {
        status = Status.values()[McpeUtil.readUnsignedVarInt(buffer)];
    }

    @Override
    public void encode(ByteBuf buffer) {
        McpeUtil.writeUnsignedVarInt(buffer, status.ordinal());
    }

    public enum Status {
        LOGIN_SUCCESS,
        LOGIN_FAILED_CLIENT,
        LOGIN_FAILED_SERVER,
        PLAYER_SPAWN
    }
}
