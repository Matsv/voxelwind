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
    private int hotbarSlot;
    private ItemStack stack;

    @Override
    public void decode(ByteBuf buffer) {
        windowId = buffer.readByte();
        slot = McpeUtil.readSignedVarInt(buffer);
        hotbarSlot = McpeUtil.readSignedVarInt(buffer);
        stack = McpeUtil.readItemStack(buffer);
    }

    @Override
    public void encode(ByteBuf buffer) {
        buffer.writeByte(windowId);
        McpeUtil.writeSignedVarInt(buffer, slot);
        McpeUtil.writeSignedVarInt(buffer, hotbarSlot);
        McpeUtil.writeItemStack(buffer, stack);
    }
}
