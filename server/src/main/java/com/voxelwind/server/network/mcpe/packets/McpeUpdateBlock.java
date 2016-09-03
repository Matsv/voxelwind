package com.voxelwind.server.network.mcpe.packets;

import com.flowpowered.math.vector.Vector3i;
import com.voxelwind.server.network.mcpe.McpeUtil;
import com.voxelwind.server.network.NetworkPackage;
import io.netty.buffer.ByteBuf;
import lombok.Data;

@Data
public class McpeUpdateBlock implements NetworkPackage {
    private Vector3i position;
    private int blockId;
    private int flags;
    private int metadata;

    @Override
    public void decode(ByteBuf buffer) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void encode(ByteBuf buffer) {
        McpeUtil.writeVector3i(buffer, position);
        McpeUtil.writeSignedVarInt(buffer, blockId);
        McpeUtil.writeSignedVarInt(buffer, (flags << 4) | metadata);
    }
}
