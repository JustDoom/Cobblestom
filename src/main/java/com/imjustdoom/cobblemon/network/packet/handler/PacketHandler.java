package com.imjustdoom.cobblemon.network.packet.handler;

import com.imjustdoom.cobblemon.data.PlayerData;

public interface PacketHandler extends Packet {
    void handle(PlayerData player);
}
