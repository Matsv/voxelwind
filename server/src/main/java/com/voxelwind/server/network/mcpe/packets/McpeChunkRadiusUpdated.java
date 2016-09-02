package com.voxelwind.server.network.mcpe.packets;

import com.voxelwind.server.network.NetworkPackage;
import com.voxelwind.server.network.mcpe.McpeUtil;
import io.netty.buffer.ByteBuf;
import lombok.Data;

@Data
public class McpeChunkRadiusUpdated implements NetworkPackage {
    private int radius;

    @Override
    public void decode(ByteBuf buffer) {
        radius = McpeUtil.readSignedVarInt(buffer);
    }

    @Override
    public void encode(ByteBuf buffer) {
        McpeUtil.writeSignedVarInt(buffer, radius);
    }
}
