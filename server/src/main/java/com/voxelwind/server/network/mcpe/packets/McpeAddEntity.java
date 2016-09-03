package com.voxelwind.server.network.mcpe.packets;

import com.flowpowered.math.vector.Vector3f;
import com.voxelwind.server.game.level.util.Attribute;
import com.voxelwind.server.network.mcpe.McpeUtil;
import com.voxelwind.server.network.mcpe.util.metadata.MetadataDictionary;
import com.voxelwind.server.network.NetworkPackage;
import io.netty.buffer.ByteBuf;
import lombok.Data;

import java.util.ArrayList;
import java.util.Collection;

@Data
public class McpeAddEntity implements NetworkPackage {
    private long entityId;
    private long runtimeId;
    private int entityType;
    private Vector3f position;
    private Vector3f velocity;
    private float yaw;
    private float pitch;
    private final MetadataDictionary metadata = new MetadataDictionary();
    private final Collection<Attribute> attributes = new ArrayList<>();

    @Override
    public void decode(ByteBuf buffer) {
        entityId = McpeUtil.readSignedVarInt(buffer);
        runtimeId = McpeUtil.readSignedVarInt(buffer);
        entityType = McpeUtil.readUnsignedVarInt(buffer);
        position = McpeUtil.readVector3f(buffer);
        velocity = McpeUtil.readVector3f(buffer);
        yaw = buffer.readFloat();
        pitch = buffer.readFloat();
        attributes.addAll(McpeUtil.readAttributes(buffer));
        metadata.putAll(MetadataDictionary.deserialize(buffer));
        // TODO: Entity Links
    }

    @Override
    public void encode(ByteBuf buffer) {
        McpeUtil.writeSignedVarInt(buffer, (int) entityId);
        McpeUtil.writeSignedVarInt(buffer, (int) runtimeId);
        McpeUtil.writeUnsignedVarInt(buffer, entityType);
        McpeUtil.writeVector3f(buffer, position);
        McpeUtil.writeVector3f(buffer, velocity);
        buffer.writeFloat(yaw);
        buffer.writeFloat(pitch);
        McpeUtil.writeAttributes(buffer, attributes);
        metadata.writeTo(buffer);
        // TODO: Entity Links
        McpeUtil.writeSignedVarInt(buffer, 0);
    }
}
