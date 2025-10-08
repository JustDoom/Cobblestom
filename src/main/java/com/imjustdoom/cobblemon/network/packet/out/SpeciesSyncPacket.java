package com.imjustdoom.cobblemon.network.packet.out;

import com.imjustdoom.cobblemon.network.packet.handler.Packet;
import net.minestom.server.network.NetworkBuffer;
import net.minestom.server.network.NetworkBufferTemplate;

public record SpeciesSyncPacket(int length, byte[] bytes) implements Packet {
    public static final NetworkBuffer.Type<SpeciesSyncPacket> SERIALIZER = NetworkBufferTemplate.template(
            NetworkBuffer.INT, SpeciesSyncPacket::length,
            NetworkBuffer.RAW_BYTES, SpeciesSyncPacket::bytes,
            SpeciesSyncPacket::new
    );
}
