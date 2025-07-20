package com.imjustdoom.packet.out.party;

import com.imjustdoom.packet.handler.Packet;
import net.minestom.server.network.NetworkBuffer;
import net.minestom.server.network.NetworkBufferTemplate;

import java.util.UUID;

public record InitialisePartyPacket(boolean isPlayerParty, UUID uuid, byte slots) implements Packet {
    public static final NetworkBuffer.Type<InitialisePartyPacket> SERIALIZER = NetworkBufferTemplate.template(
            NetworkBuffer.BOOLEAN, InitialisePartyPacket::isPlayerParty,
            NetworkBuffer.UUID, InitialisePartyPacket::uuid,
            NetworkBuffer.BYTE, InitialisePartyPacket::slots,
            InitialisePartyPacket::new
    );
}
