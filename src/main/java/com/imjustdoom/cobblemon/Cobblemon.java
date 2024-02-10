package com.imjustdoom.cobblemon;

import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.event.player.PlayerLoginEvent;
import net.minestom.server.event.player.PlayerPacketEvent;
import net.minestom.server.network.NetworkBuffer;
import net.minestom.server.network.packet.client.play.*;

public class Cobblemon {

    public void start() {
        MinecraftServer.getGlobalEventHandler().addListener(PlayerPacketEvent.class, playerPacketEvent -> {

            Player player = playerPacketEvent.getPlayer();

            if (playerPacketEvent.getPacket() instanceof ClientPluginMessagePacket) {
                System.out.println("type - " + playerPacketEvent.getPacket());
                System.out.println("data - " + new String((((ClientPluginMessagePacket) playerPacketEvent.getPacket()).data())));

                if (((ClientPluginMessagePacket) playerPacketEvent.getPacket()).channel().equals("cobblemon:request_starter_screen")) {
                    NetworkBuffer buffer = new NetworkBuffer(1024);

                    buffer.write(NetworkBuffer.INT, 3); //category size

                    addRegion(buffer, "Kanto", 2); // pokemon count in category

                    addStarter(buffer, "charmander"); // must be registered in the SyncCommand class
                    addStarter(buffer, "pikachu");

                    addRegion(buffer, "Kalos", 2);

                    addStarter(buffer, "seel");
                    addStarter(buffer, "jynx");

                    addRegion(buffer, "test", 1);

                    addStarter(buffer, "mew");

                    player.sendPluginMessage("cobblemon:open_starter", buffer.readBytes(buffer.writeIndex()));
                } else if (((ClientPluginMessagePacket) playerPacketEvent.getPacket()).channel().equals("cobblemon:select_starter")) {
                    player.sendMessage("[EA] Selecting a starter costs $9.99. Purchase?");
                }
            }
        }).addListener(PlayerLoginEvent.class, playerLoginEvent -> {
            Player player = playerLoginEvent.getPlayer();

            player.sendMessage("To sync with cobblemon type \"/sync\". I need to make it work on login still");
        });
    }

    private void addStarter(NetworkBuffer buffer, String id) {
        buffer.write(NetworkBuffer.STRING, "cobblemon:" + id); //cobblemon.species.charmander
        buffer.write(NetworkBuffer.BYTE, (byte) 0); // aspects size?
    }

    private void addRegion(NetworkBuffer buffer, String name, int size) {
        buffer.write(NetworkBuffer.STRING, "Kanto"); // cat name
        buffer.write(NetworkBuffer.STRING, "cobblemon.starterselection.category." + name.toLowerCase()); // display name
        buffer.write(NetworkBuffer.INT, size); // size of list?
    }
}
