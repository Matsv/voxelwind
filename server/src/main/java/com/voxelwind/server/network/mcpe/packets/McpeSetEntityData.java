package com.voxelwind.server.network.mcpe.packets;

import com.voxelwind.server.network.mcpe.McpeUtil;
import com.voxelwind.server.network.mcpe.util.metadata.MetadataDictionary;
import com.voxelwind.server.network.NetworkPackage;
import io.netty.buffer.ByteBuf;
import lombok.Data;

@Data
public class McpeSetEntityData implements NetworkPackage {
    private long entityId;
    private final MetadataDictionary metadata = new MetadataDictionary();

    @Override
    public void decode(ByteBuf buffer) {
        entityId = McpeUtil.readSignedVarInt(buffer);
        metadata.putAll(MetadataDictionary.deserialize(buffer));
    }

    @Override
    public void encode(ByteBuf buffer) {
        McpeUtil.writeSignedVarInt(buffer, (int) entityId);
        metadata.writeTo(buffer);
    }
}
