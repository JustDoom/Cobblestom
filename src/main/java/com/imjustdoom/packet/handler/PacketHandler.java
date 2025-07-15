package com.imjustdoom.packet.handler;

import net.minestom.server.entity.Player;

public interface PacketHandler extends Packet {
    void handle(Player player);
}
