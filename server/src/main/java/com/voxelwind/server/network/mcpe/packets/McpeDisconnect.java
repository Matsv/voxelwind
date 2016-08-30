package com.voxelwind.server.network.mcpe.packets;

import com.voxelwind.server.network.NetworkPackage;
import com.voxelwind.server.network.mcpe.McpeUtil;
import com.voxelwind.server.network.raknet.RakNetUtil;
import io.netty.buffer.ByteBuf;
import lombok.Data;

@Data
public class McpeDisconnect implements NetworkPackage {
    private String message;

    @Override
    public void decode(ByteBuf buffer) {
        message = McpeUtil.readVarIntString(buffer);
    }

    @Override
    public void encode(ByteBuf buffer) {
        McpeUtil.writeVarIntString(buffer, message);
    }
}
