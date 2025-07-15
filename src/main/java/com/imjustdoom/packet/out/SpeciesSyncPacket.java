package com.imjustdoom.packet.out;

import net.minestom.server.network.NetworkBuffer;
import net.minestom.server.network.NetworkBufferTemplate;

public record SpeciesSyncPacket(int length, byte[] bytes) {
    public static final NetworkBuffer.Type<SpeciesSyncPacket> SERIALIZER = NetworkBufferTemplate.template(
            NetworkBuffer.INT, SpeciesSyncPacket::length,
            NetworkBuffer.RAW_BYTES, SpeciesSyncPacket::bytes,
            SpeciesSyncPacket::new
    );
}
