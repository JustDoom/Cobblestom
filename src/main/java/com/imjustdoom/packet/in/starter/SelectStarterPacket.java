package com.imjustdoom.packet.in.starter;

import com.imjustdoom.PlayerData;
import com.imjustdoom.packet.handler.PacketHandler;
import com.imjustdoom.packet.out.SetClientPlayerDataPacket;
import com.imjustdoom.packet.out.party.SetPartyCobblemonPacket;
import net.minestom.server.network.NetworkBuffer;
import net.minestom.server.network.NetworkBufferTemplate;

import java.util.UUID;

public record SelectStarterPacket(String category, int selected) implements PacketHandler {
    public static final NetworkBuffer.Type<SelectStarterPacket> SERIALIZER = NetworkBufferTemplate.template(
            NetworkBuffer.STRING, SelectStarterPacket::category,
            NetworkBuffer.INT, SelectStarterPacket::selected,
            SelectStarterPacket::new
    );

    @Override
    public void handle(PlayerData player) {
        player.getPlayer().sendMessage("[EA] Selecting a starter costs $9.99. Purchase?");

        // Send custom cobblemon data to the client
        player.write(
                new SetClientPlayerDataPacket("cobblemon:general", false, true, false,
                        true, false, UUID.randomUUID(), null, null));

        // I think set the players party members
        player.write(new SetPartyCobblemonPacket(player.getPlayer().getUuid(), (short) 0));

//                    Tag<Pokemon>e = new Tag<Pokemon>();
    }
}
