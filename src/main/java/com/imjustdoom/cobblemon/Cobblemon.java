package com.imjustdoom.cobblemon;

import com.imjustdoom.Main;
import com.imjustdoom.PlayerData;
import com.imjustdoom.packet.in.SelectStarterPacket;
import com.imjustdoom.packet.out.SyncPacket;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.event.player.PlayerPacketEvent;
import net.minestom.server.event.player.PlayerSpawnEvent;
import net.minestom.server.network.NetworkBuffer;
import net.minestom.server.network.packet.client.common.ClientPluginMessagePacket;

import java.util.UUID;

public class Cobblemon {

    public void start() {
        MinecraftServer.getGlobalEventHandler().addListener(PlayerPacketEvent.class, playerPacketEvent -> {

            Player player = playerPacketEvent.getPlayer();

            if (playerPacketEvent.getPacket() instanceof ClientPluginMessagePacket) {
                System.out.println("type - " + playerPacketEvent.getPacket());
                System.out.println("data - " + new String((((ClientPluginMessagePacket) playerPacketEvent.getPacket()).data())));

                if (((ClientPluginMessagePacket) playerPacketEvent.getPacket()).channel().equals("cobblemon:request_starter_screen")) {

                    NetworkBuffer buffer = NetworkBuffer.resizableBuffer(0);

                    buffer.write(NetworkBuffer.INT, 3); //category size

                    addRegion(buffer, "Kanto", 2); // pokemon count in category

                    addStarter(buffer, "charmander"); // must be registered in the SyncCommand class
                    addStarter(buffer, "pikachu");

                    addRegion(buffer, "Kalos", 2);

                    addStarter(buffer, "seel");
                    addStarter(buffer, "jynx");

                    addRegion(buffer, "test", 1);

                    addStarter(buffer, "mew");

                    player.sendPluginMessage("cobblemon:open_starter", buffer.read(NetworkBuffer.RAW_BYTES));
                } else if (((ClientPluginMessagePacket) playerPacketEvent.getPacket()).channel().equals("cobblemon:select_starter")) {
                    player.sendMessage("[EA] Selecting a starter costs $9.99. Purchase?");

                    byte[] data = ((ClientPluginMessagePacket) playerPacketEvent.getPacket()).data();
                    NetworkBuffer buffer = NetworkBuffer.builder(data.length).build();
                    buffer.write(NetworkBuffer.RAW_BYTES, data);
                    SelectStarterPacket packet = SelectStarterPacket.SERIALIZER.read(buffer);

                    System.out.println(packet.category() + " - " + packet.selected());

                    buffer = NetworkBuffer.resizableBuffer(16);
                    SyncPacket.SERIALIZER.write(buffer,
                            new SyncPacket("cobblemon:general", false, true, false,
                                    true, false, UUID.randomUUID(), null, null));

                    player.sendPluginMessage("cobblemon:set_client_playerdata", buffer.read(NetworkBuffer.RAW_BYTES));

                    buffer = NetworkBuffer.resizableBuffer(16);
                    buffer.write(NetworkBuffer.UUID, player.getUuid()); // player uuid?
                    buffer.write(NetworkBuffer.SHORT, (short) 0);
                    player.sendPluginMessage("cobblemon:set_party_pokemon", buffer.read(NetworkBuffer.RAW_BYTES));


//                    Tag<Pokemon>e = new Tag<Pokemon>();


                }
            }
        }).addListener(PlayerSpawnEvent.class, playerLoginEvent -> {
            Player player = playerLoginEvent.getPlayer();
            if (!playerLoginEvent.isFirstSpawn()) return;

            NetworkBuffer buffer = NetworkBuffer.resizableBuffer(0);

            SyncPacket.SERIALIZER.write(buffer,
                    new SyncPacket("cobblemon:general", false, false, false,
                            false, false, null, null, null));

            player.sendPluginMessage("cobblemon:set_client_playerdata", buffer.read(NetworkBuffer.RAW_BYTES));

            buffer = NetworkBuffer.resizableBuffer(1024);
            NetworkBuffer listBuffer = NetworkBuffer.resizableBuffer(512);
            listBuffer.write(NetworkBuffer.VAR_INT, 5); // count of pokemon to add

            addEntity(listBuffer, "charmander", "Charmander", "fire");
            addEntity(listBuffer, "pikachu", "Pikachu", "electric");
            addEntity(listBuffer, "mew", "Mew", "psychic");
            addEntity(listBuffer, "seel", "Seel", "water");
            addEntity(listBuffer, "jynx", "Jynx", "dark");

            byte[] bytes = listBuffer.read(NetworkBuffer.RAW_BYTES);
            buffer.write(NetworkBuffer.INT, bytes.length);
            buffer.write(NetworkBuffer.RAW_BYTES, bytes);

            player.sendPluginMessage("cobblemon:species_sync", buffer.read(NetworkBuffer.RAW_BYTES));

            Main.dataMap.put(player.getUuid(), new PlayerData(player));

            player.sendMessage(Component.text("Successfully synced with Cobblemon!", NamedTextColor.GREEN));
        });
    }

    private void addStarter(NetworkBuffer buffer, String id) {
        buffer.write(NetworkBuffer.STRING, "cobblemon:" + id); //cobblemon.species.charmander
        buffer.write(NetworkBuffer.BYTE, (byte) 0); // aspects size?
    }

    private void addRegion(NetworkBuffer buffer, String name, int size) {
        buffer.write(NetworkBuffer.STRING, name); // cat name
        buffer.write(NetworkBuffer.STRING, "cobblemon.starterselection.category." + name.toLowerCase()); // display name
        buffer.write(NetworkBuffer.INT, size); // size of list?
    }

    public static void addEntity(NetworkBuffer buffer, String id, String name, String type) {
        buffer.write(NetworkBuffer.STRING, "cobblemon:" + id); // species i think

        buffer.write(NetworkBuffer.BOOLEAN, true);
        buffer.write(NetworkBuffer.STRING, name);
        buffer.write(NetworkBuffer.INT, 5);

        // base stats map aaaa
        buffer.write(NetworkBuffer.VAR_INT, 0);

        buffer.write(NetworkBuffer.STRING, type);

        buffer.write(NetworkBuffer.BOOLEAN, false);

        buffer.write(NetworkBuffer.STRING, "medium_slow");
        buffer.write(NetworkBuffer.FLOAT, 6.0f);
        buffer.write(NetworkBuffer.FLOAT, 85.0f);
        buffer.write(NetworkBuffer.FLOAT, 0.7f);
        buffer.write(NetworkBuffer.FLOAT, 1.0f);

        // dimesnions
        buffer.write(NetworkBuffer.FLOAT, 0.7f);
        buffer.write(NetworkBuffer.FLOAT, 1.1f);
        buffer.write(NetworkBuffer.BOOLEAN, false);

        buffer.write(NetworkBuffer.BYTE, (byte) 0); //

        buffer.write(NetworkBuffer.VAR_INT, 1);
        buffer.write(NetworkBuffer.STRING, "cobblemon.species." + id + ".desc");

        buffer.write(NetworkBuffer.VAR_INT, 0);

        buffer.write(NetworkBuffer.STRING, "cobblemon:battle.pvw.default");

        buffer.write(NetworkBuffer.VAR_INT, 0);

        buffer.write(NetworkBuffer.BOOLEAN, false);

        buffer.write(NetworkBuffer.INT, 0);
        buffer.write(NetworkBuffer.VAR_INT, 0);
    }
}
