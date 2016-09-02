package com.voxelwind.server.network.mcpe.packets;

import com.voxelwind.api.game.item.ItemStack;
import com.voxelwind.server.network.mcpe.McpeUtil;
import com.voxelwind.server.network.NetworkPackage;
import io.netty.buffer.ByteBuf;
import lombok.Data;

@Data
public class McpeContainerSetSlot implements NetworkPackage {
    private byte windowId;
    private int slot;
    private int unknown;
    private ItemStack stack;

    @Override
    public void decode(ByteBuf buffer) {
        windowId = buffer.readByte();
        slot = McpeUtil.readUnsignedVarInt(buffer);
        unknown = McpeUtil.readUnsignedVarInt(buffer);
        stack = McpeUtil.readItemStack(buffer);
    }

    @Override
    public void encode(ByteBuf buffer) {
        buffer.writeByte(windowId);
        McpeUtil.writeUnsignedVarInt(buffer, slot);
        McpeUtil.writeUnsignedVarInt(buffer, unknown);
        McpeUtil.writeItemStack(buffer, stack);
    }
}
