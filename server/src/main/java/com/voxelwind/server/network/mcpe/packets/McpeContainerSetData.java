package com.voxelwind.server.network.mcpe.packets;

import com.voxelwind.server.network.NetworkPackage;
import com.voxelwind.server.network.mcpe.McpeUtil;
import io.netty.buffer.ByteBuf;
import lombok.Data;

@Data
public class McpeContainerSetData implements NetworkPackage {
    public byte windowId;
    public int property;
    public int value;

    @Override
    public void decode(ByteBuf buffer) {
        windowId = buffer.readByte();
        property = McpeUtil.readSignedVarInt(buffer);
        value = McpeUtil.readSignedVarInt(buffer);
    }

    @Override
    public void encode(ByteBuf buffer) {
        buffer.writeByte(windowId);
        McpeUtil.writeSignedVarInt(buffer, property);
        McpeUtil.writeSignedVarInt(buffer, value);
    }
}
