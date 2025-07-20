package com.imjustdoom.packet;

import com.imjustdoom.cobblemon.Cobblemon;
import com.imjustdoom.packet.handler.Packet;
import com.imjustdoom.packet.handler.PacketHandler;
import com.imjustdoom.packet.in.starter.RequestStarterScreenPacket;
import com.imjustdoom.packet.in.starter.SelectStarterPacket;
import com.imjustdoom.packet.out.SetClientPlayerDataPacket;
import com.imjustdoom.packet.out.SpeciesSyncPacket;
import com.imjustdoom.packet.out.party.InitialisePartyPacket;
import com.imjustdoom.packet.out.party.SetPartyCobblemonPacket;
import com.imjustdoom.packet.out.party.SetPartyReferencePacket;
import net.minestom.server.MinecraftServer;
import net.minestom.server.event.player.PlayerPacketEvent;
import net.minestom.server.network.NetworkBuffer;
import net.minestom.server.network.packet.client.common.ClientPluginMessagePacket;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class CobblemonPacketListener {
    private final Map<String, Class<? extends Packet>> toServerPackets = new HashMap<>();
    private final Map<Class<? extends Packet>, String> toClientPackets = new HashMap<>();
    private boolean initialised = false;

    public void init() {
        if (this.initialised) {
            System.err.println("Bro you already initialised it");
            return;
        }
        this.toServerPackets.put("request_starter_screen", RequestStarterScreenPacket.class);
        this.toServerPackets.put("select_starter", SelectStarterPacket.class);

        this.toClientPackets.put(InitialisePartyPacket.class, "cobblemon:initialize_party");
        this.toClientPackets.put(SetPartyCobblemonPacket.class, "cobblemon:set_party_pokemon");
        this.toClientPackets.put(SetPartyReferencePacket.class, "cobblemon:set_party_reference");
        this.toClientPackets.put(SetClientPlayerDataPacket.class, "cobblemon:set_client_playerdata");
        this.toClientPackets.put(SpeciesSyncPacket.class, "cobblemon:species_sync");

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

            Class<? extends Packet> packetClass = this.toServerPackets.get(channels[1]);
            if (packetClass == null) {
                return;
            }

            try {
                Field serializerField = packetClass.getField("SERIALIZER");
                NetworkBuffer.Type<?> serializer = (NetworkBuffer.Type<?>) serializerField.get(null);
                ((PacketHandler) buffer.read(serializer)).handle(Cobblemon.get().getPlayerDataMap().get(playerPacketEvent.getPlayer().getUuid()));
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
        });

        this.initialised = true;
    }

    public <T extends Packet> String getClientPacket(T packet) {
        return this.toClientPackets.get(packet.getClass());
    }
}
