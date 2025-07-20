package com.imjustdoom.packet.out.party;

import com.imjustdoom.packet.handler.Packet;
import net.minestom.server.network.NetworkBuffer;
import net.minestom.server.network.NetworkBufferTemplate;

import java.util.UUID;

public record SetPartyReferencePacket(UUID storeId) implements Packet {
    public static final NetworkBuffer.Type<SetPartyReferencePacket> SERIALIZER = NetworkBufferTemplate.template(
            NetworkBuffer.UUID, SetPartyReferencePacket::storeId,
            SetPartyReferencePacket::new
    );
}
