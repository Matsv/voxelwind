package com.voxelwind.server.network.mcpe.packets;

import com.voxelwind.server.network.NetworkPackage;
import com.voxelwind.server.network.mcpe.McpeUtil;
import io.netty.buffer.ByteBuf;
import lombok.Data;

@Data
public class McpeEntityEvent implements NetworkPackage {
    private long entityId;
    private byte event;

    @Override
    public void decode(ByteBuf buffer) {
        entityId = McpeUtil.readSignedVarInt(buffer);
        event = buffer.readByte();
    }

    @Override
    public void encode(ByteBuf buffer) {
        McpeUtil.writeSignedVarInt(buffer, (int) entityId);
        buffer.writeByte(event);
    }
}
