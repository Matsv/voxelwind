package com.voxelwind.server.network.mcpe.packets;

import com.voxelwind.api.server.util.TranslatedMessage;
import com.voxelwind.server.network.mcpe.McpeUtil;
import com.voxelwind.server.network.NetworkPackage;
import com.voxelwind.server.network.raknet.RakNetUtil;
import io.netty.buffer.ByteBuf;
import lombok.Data;

@Data
public class McpeText implements NetworkPackage {
    private TextType type;
    private String source = "";
    private String message = "";
    private TranslatedMessage translatedMessage;

    @Override
    public void decode(ByteBuf buffer) {
        type = TextType.values()[buffer.readByte()];
        switch (type) {
            case RAW:
                message = McpeUtil.readVarIntString(buffer);
                break;
            case SOURCE:
                source = McpeUtil.readVarIntString(buffer);
                message = McpeUtil.readVarIntString(buffer);
                break;
            case TRANSLATE:
                translatedMessage = McpeUtil.readTranslatedMessage(buffer);
                break;
            case POPUP:
                source = McpeUtil.readVarIntString(buffer);
                message = McpeUtil.readVarIntString(buffer);
                break;
            case TIP:
                message = McpeUtil.readVarIntString(buffer);
                break;
            case SYSTEM:
                message = McpeUtil.readVarIntString(buffer);
                break;
        }
    }

    @Override
    public void encode(ByteBuf buffer) {
        buffer.writeByte(type.ordinal());
        switch (type) {
            case RAW:
                McpeUtil.writeVarIntString(buffer, message);
                break;
            case SOURCE:
                McpeUtil.writeVarIntString(buffer, source);
                McpeUtil.writeVarIntString(buffer, message);
                break;
            case TRANSLATE:
                McpeUtil.writeTranslatedMessage(buffer, translatedMessage);
                break;
            case POPUP:
                McpeUtil.writeVarIntString(buffer, source);
                McpeUtil.writeVarIntString(buffer, message);
                break;
            case TIP:
                message = McpeUtil.readVarIntString(buffer);
                break;
            case SYSTEM:
                message = McpeUtil.readVarIntString(buffer);
                break;
        }
    }

    @Override
    public String toString() {
        return "McpeText{" +
                "type=" + type +
                ", source='" + source + '\'' +
                ", message='" + message + '\'' +
                ", translatedMessage=" + translatedMessage +
                '}';
    }

    public enum TextType {
        RAW,
        SOURCE,
        TRANSLATE,
        POPUP,
        TIP,
        SYSTEM
    }
}
