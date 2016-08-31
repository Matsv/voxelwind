package com.voxelwind.server.network.mcpe;

import com.flowpowered.math.vector.Vector3f;
import com.flowpowered.math.vector.Vector3i;
import com.flowpowered.nbt.stream.NBTInputStream;
import com.flowpowered.nbt.stream.NBTOutputStream;
import com.google.common.base.Preconditions;
import com.voxelwind.api.game.item.ItemStack;
import com.voxelwind.api.game.item.ItemType;
import com.voxelwind.api.game.item.ItemTypes;
import com.voxelwind.api.game.item.data.ItemData;
import com.voxelwind.api.game.level.block.BlockTypes;
import com.voxelwind.api.server.Skin;
import com.voxelwind.api.server.util.TranslatedMessage;
import com.voxelwind.server.game.item.VoxelwindItemStack;
import com.voxelwind.server.game.level.util.Attribute;
import com.voxelwind.server.network.raknet.RakNetUtil;
import com.voxelwind.api.util.Rotation;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.ByteBufUtil;
import org.apache.commons.io.output.ByteArrayOutputStream;

import java.io.IOException;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.*;

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
        writeVarInt(buf, vector3i.getX());
        writeVarInt(buf, vector3i.getY());
        writeVarInt(buf, vector3i.getZ());
    }

    public static Vector3i readVector3i(ByteBuf buf) {
        int x = readVarInt(buf);
        int y = readVarInt(buf);
        int z = readVarInt(buf);
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
        int size = readVarInt(buf);

        for (int i = 0; i < size; i++) {
            float min = buf.readFloat();
            float max = buf.readFloat();
            float val = buf.readFloat();
            String name = readVarIntString(buf);

            attributes.add(new Attribute(name, min, max, val));
        }

        return attributes;
    }

    public static void writeAttributes(ByteBuf buf, Collection<Attribute> attributeList) {
        McpeUtil.writeVarInt(buf, attributeList.size());
        for (Attribute attribute : attributeList) {
            buf.writeFloat(attribute.getMinimumValue());
            buf.writeFloat(attribute.getMaximumValue());
            buf.writeFloat(attribute.getValue());
            writeVarIntString(buf, attribute.getName());
        }
    }

    public static Skin readSkin(ByteBuf buf) {
        String type = RakNetUtil.readString(buf);
        short length = buf.readShort();
        if (length == 64*32*4 || length == 64*64*4) {
            byte[] in = new byte[length];
            buf.readBytes(in);

            return new Skin(type, in);
        }

        return new Skin("Standard_Custom", new byte[0]);
    }

    public static void writeSkin(ByteBuf buf, Skin skin) {
        byte[] texture = skin.getTexture();
        RakNetUtil.writeString(buf, skin.getType());
        buf.writeShort(texture.length);
        buf.writeBytes(texture);
    }

    public static TranslatedMessage readTranslatedMessage(ByteBuf buf) {
        String message = readVarIntString(buf);
        int ln = readVarInt(buf);
        List<String> replacements = new ArrayList<>();
        for (int i = 0; i < ln; i++) {
            replacements.add(readVarIntString(buf));
        }
        return new TranslatedMessage(message, replacements);
    }

    public static void writeTranslatedMessage(ByteBuf buf, TranslatedMessage message) {
        writeVarIntString(buf, message.getName());
        writeVarInt(buf, message.getReplacements().size());
        for (String s : message.getReplacements()) {
            writeVarIntString(buf, s);
        }
    }

    public static ItemStack readItemStack(ByteBuf buf) {
        short id = buf.readShort();
        if (id == 0) {
            return new VoxelwindItemStack(BlockTypes.AIR, 1, null);
        }

        int count = buf.readByte();
        short damage = buf.readShort();

        int nbtSize = readVarInt(buf);

        ItemType type = ItemTypes.forId(id);
        VoxelwindItemStack stack = new VoxelwindItemStack(type, count, type.createDataFor(damage).orElse(null));

        if (nbtSize > 0) {
            try (NBTInputStream stream = new NBTInputStream(new ByteBufInputStream(buf.readSlice(nbtSize)), false, ByteOrder.LITTLE_ENDIAN)) {
                stack.readNbt(stream);
            } catch (IOException e) {
                throw new IllegalStateException("Unable to load NBT data", e);
            }
        }
        return stack;
    }

    public static void writeItemStack(ByteBuf buf, ItemStack stack) {
        buf.writeShort(stack.getItemType().getId());
        if (stack.getItemType() == BlockTypes.AIR) {
            return;
        }

        buf.writeByte(stack.getAmount());
        Optional<ItemData> dataOptional = stack.getItemData();
        if (dataOptional.isPresent()) {
            buf.writeShort(dataOptional.get().toMetadata());
        } else {
            buf.writeShort(0);
        }

        // Write a stack here
        if (stack instanceof VoxelwindItemStack) {
            ByteArrayOutputStream nbtBuffer = new ByteArrayOutputStream();
            try (NBTOutputStream stream = new NBTOutputStream(nbtBuffer, false, ByteOrder.LITTLE_ENDIAN)) {
                ((VoxelwindItemStack) stack).writeNbt(stream);
            } catch (IOException e) {
                // This shouldn't happen (as this is backed by a Netty ByteBuf), but okay...
                throw new IllegalStateException("Unable to save NBT data", e);
            }

            // Write NBT size.
            writeVarInt(buf, nbtBuffer.size());
            try {
                nbtBuffer.writeTo(new ByteBufOutputStream(buf));
            } catch (IOException e) {
                throw new AssertionError(e); // Can't ever happen
            }
        } else {
            writeVarInt(buf, 0);
        }
    }

    public static UUID readUuid(ByteBuf buf) {
        return new UUID(buf.readLong(), buf.readLong());
    }

    public static void writeUuid(ByteBuf buf, UUID uuid) {
        buf.writeLong(uuid.getMostSignificantBits());
        buf.writeLong(uuid.getLeastSignificantBits());
    }

    public static int readVarInt(ByteBuf in) {
        int i = 0;
        int j = 0;
        while (true) {
            int k = in.readByte();
            i |= (k & 0x7F) << j++ * 7;
            if (j > 5) throw new RuntimeException("VarInt too big");
            if ((k & 0x80) != 128) break;
        }
        return i;
    }

    public static void writeVarInt(ByteBuf out, int paramInt) {
        while (true) {
            if ((paramInt & 0xFFFFFF80) == 0) {
                out.writeByte(paramInt);
                return;
            }

            out.writeByte(paramInt & 0x7F | 0x80);
            paramInt >>>= 7;
        }
    }

    public static String readVarIntString(ByteBuf buf) {
        int length = readVarInt(buf);
        return buf.readSlice(length).toString(StandardCharsets.UTF_8);
    }

    public static void writeVarIntString(ByteBuf buf, String string) {
        writeVarInt(buf, string.length());
        ByteBufUtil.writeUtf8(buf, string);
    }
}
