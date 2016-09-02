package com.voxelwind.server.network.mcpe.packets;

import com.voxelwind.api.game.item.ItemStack;
import com.voxelwind.server.network.mcpe.McpeUtil;
import com.voxelwind.server.network.NetworkPackage;
import io.netty.buffer.ByteBuf;
import lombok.Data;

@Data
public class McpeMobEquipment implements NetworkPackage {
    private long entityId;
    private ItemStack stack;
    private byte inventorySlot;
    private byte hotbarSlot;
    private byte unknown;

    @Override
    public void decode(ByteBuf buffer) {
        entityId = McpeUtil.readUnsignedVarInt(buffer);
        stack = McpeUtil.readItemStack(buffer);
        inventorySlot = buffer.readByte();
        hotbarSlot = buffer.readByte();
        unknown = buffer.readByte(); // TODO: Unknown byte
    }

    @Override
    public void encode(ByteBuf buffer) {
        buffer.writeLong(entityId);
        McpeUtil.writeItemStack(buffer, stack);
        buffer.writeByte(inventorySlot);
        buffer.writeByte(hotbarSlot);
        buffer.writeByte(unknown); // TODO: Unknown byte
    }
}
