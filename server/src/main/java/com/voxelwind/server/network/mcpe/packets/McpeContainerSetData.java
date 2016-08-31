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
        property = McpeUtil.readVarInt(buffer);
        value = McpeUtil.readVarInt(buffer);
    }

    @Override
    public void encode(ByteBuf buffer) {
        buffer.writeByte(windowId);
        McpeUtil.writeVarInt(buffer, property);
        McpeUtil.writeVarInt(buffer, value);
    }
}
