package com.voxelwind.server.network.mcpe.packets;

import com.voxelwind.server.network.NetworkPackage;
import com.voxelwind.server.network.mcpe.McpeUtil;
import io.netty.buffer.ByteBuf;
import lombok.Data;

@Data
public class McpeAnimate implements NetworkPackage {
    private byte action;
    private long entityId;
    private float unknown;

    @Override
    public void decode(ByteBuf buffer) {
        action = buffer.readByte();
        entityId = McpeUtil.readSignedVarInt(buffer);
        unknown = buffer.readFloat();
    }

    @Override
    public void encode(ByteBuf buffer) {
        buffer.writeByte(action);
        McpeUtil.writeSignedVarInt(buffer, (int) entityId);
        buffer.writeFloat(unknown);
    }
}
