package com.imjustdoom.packet.handler;

import com.imjustdoom.PlayerData;

public interface PacketHandler extends Packet {
    void handle(PlayerData player);
}
