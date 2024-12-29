package com.imjustdoom.packet;

import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.event.player.PlayerPacketEvent;
import net.minestom.server.network.NetworkBuffer;
import net.minestom.server.network.packet.client.common.ClientPluginMessagePacket;

public class CobblemonPacketListener {

    public void init() {
        MinecraftServer.getGlobalEventHandler().addListener(PlayerPacketEvent.class, playerPacketEvent -> {
            if (!(playerPacketEvent.getPacket() instanceof ClientPluginMessagePacket)) {
                return;
            }

            Player player = playerPacketEvent.getPlayer();

            ClientPluginMessagePacket clientPluginMessagePacket = (ClientPluginMessagePacket) playerPacketEvent.getPacket();
            String channel = clientPluginMessagePacket.channel().split(":")[1];
            byte[] data = clientPluginMessagePacket.data();

            NetworkBuffer buffer = NetworkBuffer.builder(data.length).build();
            buffer.write(NetworkBuffer.RAW_BYTES, data);

            switch (channel.toLowerCase()) {
                case "request_starter_screen":
            }

            if (((ClientPluginMessagePacket) playerPacketEvent.getPacket()).channel().equals("cobblemon:request_starter_screen")) {

            }
        });
    }
}
