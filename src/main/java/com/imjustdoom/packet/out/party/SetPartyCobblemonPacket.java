package com.imjustdoom.packet.out.party;

import net.minestom.server.network.NetworkBuffer;
import net.minestom.server.network.NetworkBufferTemplate;

import java.util.UUID;

public record SetPartyCobblemonPacket(UUID storeId, short slot) {
    public static final NetworkBuffer.Type<SetPartyCobblemonPacket> SERIALIZER = NetworkBufferTemplate.template(
            NetworkBuffer.STRING.transform(UUID::fromString, UUID::toString).optional(), SetPartyCobblemonPacket::storeId,
            NetworkBuffer.SHORT, SetPartyCobblemonPacket::slot,
            SetPartyCobblemonPacket::new
    );
}
