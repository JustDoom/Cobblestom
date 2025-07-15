package com.imjustdoom.packet;

import com.imjustdoom.packet.handler.Packet;
import com.imjustdoom.packet.handler.PacketHandler;
import com.imjustdoom.packet.in.starter.RequestStarterScreenPacket;
import com.imjustdoom.packet.in.starter.SelectStarterPacket;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.event.player.PlayerPacketEvent;
import net.minestom.server.network.NetworkBuffer;
import net.minestom.server.network.packet.client.common.ClientPluginMessagePacket;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class CobblemonPacketListener {
    private final Map<String, Class<? extends Packet>> packets = new HashMap<>();
    private boolean initialised = false;

    public void init() {
        if (this.initialised) {
            System.err.println("Bro you already initialised it");
            return;
        }
        this.packets.put("request_starter_screen", RequestStarterScreenPacket.class);
        this.packets.put("select_starter", SelectStarterPacket.class);

        MinecraftServer.getGlobalEventHandler().addListener(PlayerPacketEvent.class, playerPacketEvent -> {
            if (!(playerPacketEvent.getPacket() instanceof ClientPluginMessagePacket(String channel, byte[] data))) {
                return;
            }

            String[] channels = channel.split(":");
            if (!channels[0].equalsIgnoreCase("cobblemon")) {
                return;
            }

            NetworkBuffer buffer = NetworkBuffer.builder(data.length).build();
            buffer.write(NetworkBuffer.RAW_BYTES, data);

            Class<? extends Packet> packetClass = this.packets.get(channels[1]);
            if (packetClass == null) {
                return;
            }

            try {
                Field serializerField = packetClass.getField("SERIALIZER");
                NetworkBuffer.Type<?> serializer = (NetworkBuffer.Type<?>) serializerField.get(null);
                ((PacketHandler) buffer.read(serializer)).handle(playerPacketEvent.getPlayer());
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
        });

        this.initialised = true;
    }

    // TODO: Maybe make a player wrapper and move this there
    public <T extends Packet> void write(Player player, String id, T packet) {
        NetworkBuffer buffer = NetworkBuffer.resizableBuffer(16);

        try {
            Field serializerField = packet.getClass().getField("SERIALIZER");
            NetworkBuffer.Type<T> serializer = (NetworkBuffer.Type<T>) serializerField.get(null);
            serializer.write(buffer, packet);

            player.sendPluginMessage(id, buffer.read(NetworkBuffer.RAW_BYTES));
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
