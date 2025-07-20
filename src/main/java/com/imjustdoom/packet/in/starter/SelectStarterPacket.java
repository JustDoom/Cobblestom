package com.imjustdoom.packet.in.starter;

import com.imjustdoom.PlayerData;
import com.imjustdoom.packet.handler.PacketHandler;
import com.imjustdoom.packet.out.party.InitialisePartyPacket;
import com.imjustdoom.packet.out.party.SetPartyCobblemonPacket;
import net.minestom.server.network.NetworkBuffer;
import net.minestom.server.network.NetworkBufferTemplate;

public record SelectStarterPacket(String category, int selected) implements PacketHandler {
    public static final NetworkBuffer.Type<SelectStarterPacket> SERIALIZER = NetworkBufferTemplate.template(
            NetworkBuffer.STRING, SelectStarterPacket::category,
            NetworkBuffer.INT, SelectStarterPacket::selected,
            SelectStarterPacket::new
    );

    @Override
    public void handle(PlayerData player) {
        player.getPlayer().sendMessage("[EA] Selecting a starter costs $9.99. Purchase?");

        player.write(new InitialisePartyPacket(true, player.getPlayer().getUuid(), (byte) 6));
// I think set the players party members
        player.write(new SetPartyCobblemonPacket(player.getPlayer().getUuid(), (short) 0));

        // Send custom cobblemon data to the client
        player.starterPrompted = true;
        player.startedSelected = true;
        player.write(player.createPacket("cobblemon:pokedex", true));

//                    Tag<Pokemon>e = new Tag<Pokemon>();
    }
}
