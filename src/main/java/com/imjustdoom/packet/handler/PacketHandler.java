package com.imjustdoom.packet.handler;

import net.minestom.server.network.NetworkBuffer;

public interface PacketHandler {

    void handle(NetworkBuffer buffer);
}
