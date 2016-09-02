package com.voxelwind.server.network.mcpe.packets;

import com.voxelwind.server.game.level.util.Attribute;
import com.voxelwind.server.network.mcpe.McpeUtil;
import com.voxelwind.server.network.NetworkPackage;
import io.netty.buffer.ByteBuf;
import lombok.Data;

import java.util.ArrayList;
import java.util.Collection;

@Data
public class McpeUpdateAttributes implements NetworkPackage {
    private long entityId;
    private final Collection<Attribute> attributes = new ArrayList<>();

    @Override
    public void decode(ByteBuf buffer) {
        entityId = McpeUtil.readUnsignedVarInt(buffer);
        attributes.addAll(McpeUtil.readAttributes(buffer));
    }

    @Override
    public void encode(ByteBuf buffer) {
        McpeUtil.writeUnsignedVarInt(buffer, (int) entityId);
        McpeUtil.writeAttributes(buffer, attributes);
    }
}
