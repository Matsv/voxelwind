package com.voxelwind.server.network.mcpe.packets;

import com.voxelwind.server.network.mcpe.McpeUtil;
import com.voxelwind.server.network.NetworkPackage;
import com.voxelwind.server.network.util.CompressionUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import lombok.Data;

import java.util.zip.DataFormatException;

@Data
public class McpeLogin implements NetworkPackage {
    private int protocolVersion; // = 90
    private String chainData;
    private String skinData;

    @Override
    public void decode(ByteBuf buffer) {
        protocolVersion = buffer.readInt();
        buffer.skipBytes(protocolVersion >= 90 ? 2 : 4);
        ByteBuf body = buffer.slice();

        // Decompress the body
        ByteBuf result = null;
        try {
            result = CompressionUtil.inflate(body);
            chainData = McpeUtil.readLELengthString(result);
            skinData = McpeUtil.readLELengthString(result);
        } catch (DataFormatException e) {
            throw new RuntimeException("Unable to inflate login data body", e);
        } finally {
            if (result != null) {
                result.release();
            }
        }
    }

    @Override
    public void encode(ByteBuf buffer) {
        buffer.writeInt(protocolVersion);
        buffer.writeShort(0); // TODO

        ByteBuf body = PooledByteBufAllocator.DEFAULT.directBuffer();
        try {
            McpeUtil.writeLELengthString(body, chainData);
            McpeUtil.writeLELengthString(body, skinData);

            ByteBuf compressed = CompressionUtil.deflate(body);

            McpeUtil.writeUnsignedVarInt(buffer, (compressed.readableBytes() & 0xFF));
            buffer.writeBytes(compressed);
            compressed.release();
        } catch (DataFormatException e) {
            throw new RuntimeException("Unable to compress login data body", e);
        } finally {
            body.release();
        }
    }
}
