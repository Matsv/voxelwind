package com.voxelwind.server.network.mcpe.packets;

import com.voxelwind.api.game.item.ItemStack;
import com.voxelwind.server.network.mcpe.McpeUtil;
import com.voxelwind.server.network.NetworkPackage;
import io.netty.buffer.ByteBuf;
import lombok.Data;

@Data
public class McpeContainerSetContents implements NetworkPackage {
    private byte windowId;
    private ItemStack[] stacks;
    private int[] hotbarData = new int[0];

    @Override
    public void decode(ByteBuf buffer) {
        windowId = buffer.readByte();
        // TODO: Unsigned varint
        int stacksToRead = buffer.readShort();
        stacks = new ItemStack[stacksToRead];
        for (int i = 0; i < stacksToRead; i++) {
            stacks[i] = McpeUtil.readItemStack(buffer);
        }
        // TODO: Unsigned varint
        int hotbarEntriesToRead = buffer.readShort();
        hotbarData = new int[hotbarEntriesToRead];
        for (int i = 0; i < hotbarEntriesToRead; i++) {
            hotbarData[i] = McpeUtil.readUnsignedVarInt(buffer);
        }
    }

    @Override
    public void encode(ByteBuf buffer) {
        buffer.writeByte(windowId);
        // TODO: Unsigned varint
        buffer.writeShort(stacks.length);
        for (ItemStack stack : stacks) {
            McpeUtil.writeItemStack(buffer, stack);
        }
        // TODO: Unsigned varint
        buffer.writeShort(hotbarData.length);
        for (int i : hotbarData) {
            McpeUtil.writeUnsignedVarInt(buffer, i);
        }
    }
}
