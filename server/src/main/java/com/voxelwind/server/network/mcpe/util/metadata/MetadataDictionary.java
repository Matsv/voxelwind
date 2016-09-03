package com.voxelwind.server.network.mcpe.util.metadata;

import com.flowpowered.math.vector.Vector3i;
import com.google.common.base.Preconditions;
import com.voxelwind.api.game.item.ItemStack;
import com.voxelwind.api.game.item.ItemType;
import com.voxelwind.api.game.item.ItemTypes;
import com.voxelwind.api.game.item.data.ItemData;
import com.voxelwind.server.game.item.VoxelwindItemStack;
import com.voxelwind.server.network.mcpe.McpeUtil;
import com.voxelwind.server.network.raknet.RakNetUtil;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.procedure.TIntObjectProcedure;
import io.netty.buffer.ByteBuf;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class MetadataDictionary {
    private final TIntObjectMap<Object> typeMap = new TIntObjectHashMap<>();

    public void putAll(MetadataDictionary dictionary) {
        Preconditions.checkNotNull(dictionary, "dictionary");
        typeMap.putAll(dictionary.typeMap);
    }

    public Optional get(int index) {
        return Optional.ofNullable(typeMap.get(index));
    }

    public void put(int index, Object o) {
        Preconditions.checkNotNull(o, "o");
        Preconditions.checkArgument(isAcceptable(o), "object can not be serialized");

        typeMap.put(index, o);
    }

    public void writeTo(ByteBuf buf) {
        typeMap.forEachEntry((i, o) -> {
            serialize(buf, i, o);
            return true;
        });
        buf.writeByte(0x7F);
    }

    private static boolean isAcceptable(Object o) {
        return o instanceof Byte || o instanceof Short || o instanceof Integer || o instanceof Float || o
                instanceof String || o instanceof Vector3i;
    }

    public static MetadataDictionary deserialize(ByteBuf buf) {
        byte read;
        MetadataDictionary dictionary = new MetadataDictionary();
        while ((read = buf.readByte()) != 0x7F) {
            int idx = read & 0x1f;
            int type = read >> 5;

            switch (type) {
                case EntityMetadataConstants.DATA_TYPE_BYTE:
                    dictionary.put(idx, buf.readByte());
                    break;
                case EntityMetadataConstants.DATA_TYPE_SHORT:
                    dictionary.put(idx, buf.readShort());
                    break;
                case EntityMetadataConstants.DATA_TYPE_INT:
                    dictionary.put(idx, buf.readInt());
                    break;
                case EntityMetadataConstants.DATA_TYPE_FLOAT:
                    dictionary.put(idx, buf.readFloat());
                    break;
                case EntityMetadataConstants.DATA_TYPE_STRING:
                    dictionary.put(idx, RakNetUtil.readString(buf));
                    break;
                case EntityMetadataConstants.DATA_TYPE_POS:
                    dictionary.put(idx, McpeUtil.readVector3i(buf));
                    break;
                case EntityMetadataConstants.DATA_TYPE_SLOT:
                    short id = buf.readShort();
                    byte amount = buf.readByte();
                    short data = buf.readShort();

                    ItemType type1 = ItemTypes.forId(id);
                    Optional<ItemData> data1 = type1.createDataFor(data);
                    dictionary.put(idx, new VoxelwindItemStack(type1, amount, data1.orElse(null)));
                    break;
                default:
                    throw new IllegalArgumentException("Type " + type + " is not recognized.");
            }
        }
        return dictionary;
    }

    private static void serialize(ByteBuf buf, int idx, Object o) {
        Preconditions.checkNotNull(buf, "buf");
        Preconditions.checkNotNull(o, "o");

        if (o instanceof Byte) {
            Byte aByte = (Byte) o;
            buf.writeByte(EntityMetadataConstants.idify(EntityMetadataConstants.DATA_TYPE_BYTE, idx));
            buf.writeByte(aByte);
        } else if (o instanceof Short) {
            Short aShort = (Short) o;
            buf.writeByte(EntityMetadataConstants.idify(EntityMetadataConstants.DATA_TYPE_SHORT, idx));
            buf.writeShort(aShort);
        } else if (o instanceof Integer) {
            Integer integer = (Integer) o;
            buf.writeByte(EntityMetadataConstants.idify(EntityMetadataConstants.DATA_TYPE_INT, idx));
            buf.writeInt(integer);
        } else if (o instanceof Float) {
            Float aFloat = (Float) o;
            buf.writeByte(EntityMetadataConstants.idify(EntityMetadataConstants.DATA_TYPE_FLOAT, idx));
            buf.writeFloat(aFloat);
        } else if (o instanceof String) {
            String s = (String) o;
            buf.writeByte(EntityMetadataConstants.idify(EntityMetadataConstants.DATA_TYPE_STRING, idx));
            RakNetUtil.writeString(buf, s);
        } else if (o instanceof ItemStack) {
            ItemStack stack = (ItemStack) o;
            buf.writeByte(EntityMetadataConstants.idify(EntityMetadataConstants.DATA_TYPE_SLOT, idx));
            buf.writeShort(stack.getItemType().getId());
            buf.writeByte(stack.getAmount());
            Optional<ItemData> data = stack.getItemData();
            if (data.isPresent()) {
                buf.writeShort(data.get().toMetadata());
            } else {
                buf.writeShort(0);
            }
        } else if (o instanceof Vector3i) {
            Vector3i vector3i = (Vector3i) o;
            buf.writeByte(EntityMetadataConstants.idify(EntityMetadataConstants.DATA_TYPE_POS, idx));
            McpeUtil.writeVector3i(buf, vector3i);
        } else {
            throw new IllegalArgumentException("Unsupported type " + o.getClass().getName());
        }
    }

    @Override
    public String toString() {
        return typeMap.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MetadataDictionary that = (MetadataDictionary) o;
        return java.util.Objects.equals(typeMap, that.typeMap);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(typeMap);
    }
}
