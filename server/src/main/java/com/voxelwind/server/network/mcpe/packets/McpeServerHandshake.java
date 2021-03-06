package com.voxelwind.server.network.mcpe.packets;

import com.voxelwind.server.network.mcpe.annotations.BatchDisallowed;
import com.voxelwind.server.network.mcpe.annotations.ForceClearText;
import com.voxelwind.server.network.NetworkPackage;
import com.voxelwind.server.network.raknet.RakNetUtil;
import io.netty.buffer.ByteBuf;
import lombok.Data;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@ForceClearText
@BatchDisallowed
@Data
public class McpeServerHandshake implements NetworkPackage {
    private PublicKey key;
    private byte[] token;

    @Override
    public void decode(ByteBuf buffer) {
        String keyBase64 = RakNetUtil.readString(buffer);
        byte[] keyArray = Base64.getDecoder().decode(keyBase64);
        try {
            key = KeyFactory.getInstance("EC", "BC").generatePublic(new X509EncodedKeySpec(keyArray));
        } catch (InvalidKeySpecException | NoSuchAlgorithmException | NoSuchProviderException e) {
            throw new AssertionError(e);
        }
        short tokenSz = buffer.readShort();
        token = new byte[tokenSz];
        buffer.readBytes(token);
    }

    @Override
    public void encode(ByteBuf buffer) {
        byte[] encoded = key.getEncoded();
        RakNetUtil.writeString(buffer, Base64.getEncoder().encodeToString(encoded));
        buffer.writeShort(token.length);
        buffer.writeBytes(token);
    }
}
