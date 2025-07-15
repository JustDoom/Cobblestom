package com.imjustdoom.packet.in;

import net.minestom.server.network.NetworkBuffer;
import net.minestom.server.network.NetworkBufferTemplate;

public record SelectStarterPacket(String category, int selected) {
    public static final NetworkBuffer.Type<SelectStarterPacket> SERIALIZER = NetworkBufferTemplate.template(
            NetworkBuffer.STRING, SelectStarterPacket::category,
            NetworkBuffer.INT, SelectStarterPacket::selected,
            SelectStarterPacket::new
    );
}
