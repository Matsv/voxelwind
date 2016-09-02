package com.voxelwind.server.network.mcpe.packets;

import com.voxelwind.server.network.PacketRegistry;
import com.voxelwind.server.network.PacketType;
import com.voxelwind.server.network.mcpe.McpeUtil;
import com.voxelwind.server.network.mcpe.annotations.BatchDisallowed;
import com.voxelwind.server.network.NetworkPackage;
import com.voxelwind.server.network.util.CompressionUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.DataFormatException;

@BatchDisallowed // You don't batch a batch packet, it makes no sense.
public class McpeBatch implements NetworkPackage {
    private byte[] precompressed;
    private final List<NetworkPackage> packages = new ArrayList<>();

    @Override
    public void decode(ByteBuf buffer) {
        ByteBuf decompressed = null;
        try {
            int compressedSize = McpeUtil.readUnsignedVarInt(buffer);
            decompressed = CompressionUtil.inflate(buffer.readSlice(compressedSize));

            // Now process the decompressed result.
            while (decompressed.isReadable()) {
                int length = Math.toIntExact(decompressed.readUnsignedInt()); // WTF
                ByteBuf data = decompressed.readSlice(length);

                if (data.readableBytes() == 0) {
                    throw new DataFormatException("Contained batch packet is empty.");
                }

                NetworkPackage pkg = PacketRegistry.tryDecode(data, PacketType.MCPE, true);
                if (pkg != null) {
                    packages.add(pkg);
                } else {
                    data.readerIndex(0);
                    McpeUnknown unknown = new McpeUnknown();
                    unknown.decode(data);
                    packages.add(unknown);
                }
            }
        } catch (DataFormatException e) {
            throw new RuntimeException("Unable to inflate batch data", e);
        } finally {
            if (decompressed != null) {
                decompressed.release();
            }
        }
    }

    @Override
    public void encode(ByteBuf buffer) {
        if (this.precompressed == null) {
            compress(buffer);
        } else {
            buffer.writeBytes(precompressed);
        }
    }

    public List<NetworkPackage> getPackages() {
        return packages;
    }

    private void compress(ByteBuf buffer) {
        ByteBuf source = PooledByteBufAllocator.DEFAULT.directBuffer();

        try {
            for (NetworkPackage netPackage : packages) {
                if (netPackage.getClass().isAnnotationPresent(BatchDisallowed.class)) {
                    throw new DataFormatException("Packet " + netPackage + " does not permit batching.");
                }
                ByteBuf encodedPackage = PacketRegistry.tryEncode(netPackage);
                source.writeInt(encodedPackage.readableBytes());
                source.writeBytes(encodedPackage);
                encodedPackage.release();
            }

            // Use temporary buffer to compress everything (needed for varints)
            ByteBuf out = CompressionUtil.deflate(source);
            McpeUtil.writeUnsignedVarInt(buffer, out.readableBytes());
            buffer.writeBytes(out);
            out.release();
        } catch (DataFormatException e) {
            throw new RuntimeException("Unable to deflate batch data", e);
        } finally {
            source.release();
        }
    }

    public void precompress() {
        ByteBuf out = PooledByteBufAllocator.DEFAULT.directBuffer();
        try {
            compress(out);
            precompressed = new byte[out.readableBytes()];
            out.readBytes(precompressed);
        } finally {
            out.release();
        }
    }

    public void releasePrecompressed() {
        if (precompressed == null) {
            throw new IllegalStateException("Can't release precompressed packet if none exists.");
        }
        precompressed = null;
    }

    @Override
    public String toString() {
        return "McpeBatch{" +
                "packages=" + packages +
                '}';
    }
}
