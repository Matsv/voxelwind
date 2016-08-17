package com.voxelwind.server.network.mcpe;

import com.flowpowered.math.vector.Vector3f;
import com.flowpowered.math.vector.Vector3i;
import com.google.common.base.Preconditions;
import com.voxelwind.server.level.util.Attribute;
import com.voxelwind.server.network.raknet.RakNetUtil;
import com.voxelwind.api.util.Rotation;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;

import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class McpeUtil {
    private McpeUtil() {

    }

    public static void writeLELengthString(ByteBuf buffer, String string) {
        Preconditions.checkNotNull(buffer, "buffer");
        Preconditions.checkNotNull(string, "string");
        buffer.order(ByteOrder.LITTLE_ENDIAN).writeInt(string.length());
        ByteBufUtil.writeUtf8(buffer, string);
    }

    public static String readLELengthString(ByteBuf buffer) {
        Preconditions.checkNotNull(buffer, "buffer");

        int length = (buffer.order(ByteOrder.LITTLE_ENDIAN).readInt());
        byte[] bytes = new byte[length];
        buffer.readBytes(bytes);
        return new String(bytes, StandardCharsets.UTF_8);
    }

    public static void writeVector3i(ByteBuf buf, Vector3i vector3i) {
        writeVector3i(buf, vector3i, true);
    }

    public static void writeVector3i(ByteBuf buf, Vector3i vector3i, boolean yIsByte) {
        buf.writeInt(vector3i.getX());
        if (yIsByte) {
            buf.writeInt(vector3i.getZ());
            buf.writeByte(vector3i.getY());
        } else {
            buf.writeInt(vector3i.getY());
            buf.writeInt(vector3i.getZ());
        }
    }

    public static Vector3i readVector3i(ByteBuf buf) {
        return readVector3i(buf, false);
    }

    public static Vector3i readVector3i(ByteBuf buf, boolean yIsByte) {
        int x = buf.readInt();
        int y;
        int z;
        if (yIsByte) {
            z = buf.readInt();
            y = buf.readByte();
        } else {
            y = buf.readInt();
            z = buf.readInt();
        }
        return new Vector3i(x, y, z);
    }

    public static void writeVector3f(ByteBuf buf, Vector3f vector3f) {
        buf.writeFloat(vector3f.getX());
        buf.writeFloat(vector3f.getY());
        buf.writeFloat(vector3f.getZ());
    }

    public static Vector3f readVector3f(ByteBuf buf) {
        double x = buf.readFloat();
        double y = buf.readFloat();
        double z = buf.readFloat();
        return new Vector3f(x, y, z);
    }

    public static Rotation readRotation(ByteBuf buf) {
        byte pitchByte = buf.readByte();
        byte yawByte = buf.readByte();
        byte headYawByte = buf.readByte();
        return new Rotation(rotationByteToAngle(pitchByte), rotationByteToAngle(yawByte), rotationByteToAngle(headYawByte));
    }

    public static void writeRotation(ByteBuf buf, Rotation rotation) {
        buf.writeByte(rotationAngleToByte(rotation.getPitch()));
        buf.writeByte(rotationAngleToByte(rotation.getYaw()));
        buf.writeByte(rotationAngleToByte(rotation.getHeadYaw()));
    }

    private static byte rotationAngleToByte(float angle) {
        return (byte) Math.ceil(angle / 360 * 255);
    }

    private static float rotationByteToAngle(byte angle) {
        return angle / 255f * 360f;
    }

    public static Collection<Attribute> readAttributes(ByteBuf buf) {
        List<Attribute> attributes = new ArrayList<>();
        short size = buf.readShort();

        for (int i = 0; i < size; i++) {
            float min = buf.readFloat();
            float max = buf.readFloat();
            float val = buf.readFloat();
            String name = RakNetUtil.readString(buf);

            attributes.add(new Attribute(name, min, max, val));
        }

        return attributes;
    }

    public static void writeAttributes(ByteBuf buf, Collection<Attribute> attributeList) {
        buf.writeShort(attributeList.size());
        for (Attribute attribute : attributeList) {
            buf.writeFloat(attribute.getMinimumValue());
            buf.writeFloat(attribute.getMaximumValue());
            buf.writeFloat(attribute.getValue());
            RakNetUtil.writeString(buf, attribute.getName());
        }
    }
}