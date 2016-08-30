package com.voxelwind.server.network.mcpe.packets;

import com.voxelwind.server.network.NetworkPackage;
import com.voxelwind.server.network.mcpe.McpeUtil;
import io.netty.buffer.ByteBuf;
import lombok.Data;

@Data
public class McpeRemoveEntity implements NetworkPackage {
    private long entityId;

    @Override
    public void decode(ByteBuf buffer) {
        entityId = McpeUtil.readVarInt(buffer);
    }

    @Override
    public void encode(ByteBuf buffer) {
        McpeUtil.writeVarInt(buffer, (int) entityId);
    }
}
