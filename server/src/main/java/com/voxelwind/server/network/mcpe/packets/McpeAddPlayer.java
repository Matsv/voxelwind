package com.voxelwind.server.network.mcpe.packets;

import com.flowpowered.math.vector.Vector3f;
import com.voxelwind.api.game.item.ItemStack;
import com.voxelwind.api.game.level.block.BlockTypes;
import com.voxelwind.server.game.item.VoxelwindItemStack;
import com.voxelwind.server.network.mcpe.McpeUtil;
import com.voxelwind.server.network.mcpe.util.metadata.MetadataDictionary;
import com.voxelwind.server.network.NetworkPackage;
import io.netty.buffer.ByteBuf;
import lombok.Data;

import java.util.UUID;

@Data
public class McpeAddPlayer implements NetworkPackage {
    private UUID uuid;
    private String username;
    private long entityId;
    private Vector3f position;
    private Vector3f velocity;
    private float yaw;
    private float pitch;
    private ItemStack held = new VoxelwindItemStack(BlockTypes.AIR, 1, null);
    private final MetadataDictionary metadata = new MetadataDictionary();

    @Override
    public void decode(ByteBuf buffer) {
        uuid = McpeUtil.readUuid(buffer);
        username = McpeUtil.readVarIntString(buffer);
        entityId = McpeUtil.readUnsignedVarInt(buffer);
        position = McpeUtil.readVector3f(buffer);
        velocity = McpeUtil.readVector3f(buffer);
        yaw = buffer.readFloat();
        pitch = buffer.readFloat();
        held = McpeUtil.readItemStack(buffer);
        metadata.putAll(MetadataDictionary.deserialize(buffer));
    }

    @Override
    public void encode(ByteBuf buffer) {
        McpeUtil.writeUuid(buffer, uuid);
        McpeUtil.writeVarIntString(buffer, username);
        McpeUtil.writeUnsignedVarInt(buffer, (int) entityId);
        McpeUtil.writeVector3f(buffer, position);
        McpeUtil.writeVector3f(buffer, velocity);
        buffer.writeFloat(yaw);
        buffer.writeFloat(pitch);
        McpeUtil.writeItemStack(buffer, held);
        metadata.writeTo(buffer);
    }
}
