package com.voxelwind.server.network.mcpe.packets;

import com.voxelwind.server.network.NetworkPackage;
import com.voxelwind.server.network.mcpe.McpeUtil;
import io.netty.buffer.ByteBuf;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString(exclude = {"data"})
@EqualsAndHashCode(exclude = {"data"})
public class McpeFullChunkData implements NetworkPackage {
    private int chunkX;
    private int chunkZ;
    private byte order;
    private byte[] data;

    @Override
    public void decode(ByteBuf buffer) {
        chunkX = McpeUtil.readVarInt(buffer);
        chunkZ = McpeUtil.readVarInt(buffer);
        order = buffer.readByte();
        int length = McpeUtil.readVarInt(buffer);
        data = new byte[length];
        buffer.readBytes(data);
    }

    @Override
    public void encode(ByteBuf buffer) {
        McpeUtil.writeVarInt(buffer, chunkX);
        McpeUtil.writeVarInt(buffer, chunkZ);
        buffer.writeByte(order);
        McpeUtil.writeVarInt(buffer, data.length);
        buffer.writeBytes(data);
    }
}
