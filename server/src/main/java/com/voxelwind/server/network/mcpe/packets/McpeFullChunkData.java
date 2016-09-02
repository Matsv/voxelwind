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
        chunkX = McpeUtil.readUnsignedVarInt(buffer);
        chunkZ = McpeUtil.readUnsignedVarInt(buffer);
        order = buffer.readByte();
        int length = McpeUtil.readUnsignedVarInt(buffer);
        data = new byte[length];
        buffer.readBytes(data);
    }

    @Override
    public void encode(ByteBuf buffer) {
        McpeUtil.writeUnsignedVarInt(buffer, chunkX);
        McpeUtil.writeUnsignedVarInt(buffer, chunkZ);
        buffer.writeByte(order);
        McpeUtil.writeUnsignedVarInt(buffer, data.length);
        buffer.writeBytes(data);
    }
}
