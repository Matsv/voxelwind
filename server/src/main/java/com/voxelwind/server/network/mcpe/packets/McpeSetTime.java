package com.voxelwind.server.network.mcpe.packets;

import com.voxelwind.server.network.NetworkPackage;
import com.voxelwind.server.network.mcpe.McpeUtil;
import io.netty.buffer.ByteBuf;
import lombok.Data;

@Data
public class McpeSetTime implements NetworkPackage {
    private long time;
    private boolean running;

    @Override
    public void decode(ByteBuf buffer) {
        time = McpeUtil.readSignedVarInt(buffer);
        running = buffer.readBoolean();
    }

    @Override
    public void encode(ByteBuf buffer) {
        McpeUtil.writeSignedVarInt(buffer, (int) time);
        buffer.writeBoolean(running);
    }
}
