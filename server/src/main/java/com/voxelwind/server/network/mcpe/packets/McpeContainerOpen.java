package com.voxelwind.server.network.mcpe.packets;

import com.flowpowered.math.vector.Vector3i;
import com.voxelwind.server.network.mcpe.McpeUtil;
import com.voxelwind.server.network.NetworkPackage;
import io.netty.buffer.ByteBuf;
import lombok.Data;

@Data
public class McpeContainerOpen implements NetworkPackage {
    private byte windowId;
    private byte type;
    private int slotCount;
    private Vector3i position;

    @Override
    public void decode(ByteBuf buffer) {
        windowId = buffer.readByte();
        type = buffer.readByte();
        slotCount = McpeUtil.readVarInt(buffer);
        position = McpeUtil.readVector3i(buffer);
    }

    @Override
    public void encode(ByteBuf buffer) {
        buffer.writeByte(windowId);
        buffer.writeByte(type);
        McpeUtil.writeVarInt(buffer, slotCount);
        McpeUtil.writeVector3i(buffer, position);
    }
}
