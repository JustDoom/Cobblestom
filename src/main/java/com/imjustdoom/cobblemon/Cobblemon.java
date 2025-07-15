package com.imjustdoom.cobblemon;

import com.imjustdoom.Main;
import com.imjustdoom.PlayerData;
import com.imjustdoom.packet.in.SelectStarterPacket;
import com.imjustdoom.packet.out.SyncPacket;
import com.imjustdoom.packet.out.party.SetPartyCobblemonPacket;
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

                    // TODO: Branches. Already selected, cannot choose or send the options. Store player data to know

                    if (false) {// Already selected a starter
                        player.sendMessage(Component.translatable("cobblemon.ui.starter.alreadyselected"));
                        return;
                    } else if (false) {// Cannot choose
                        player.sendMessage(Component.translatable("cobblemon.ui.starter.cannotchoose"));
                        return;
                    }

                    NetworkBuffer buffer = NetworkBuffer.resizableBuffer(0);

                    buffer.write(NetworkBuffer.INT, 3); //category size

                    addRegion(buffer, "Kanto", "charmander", "pikachu");
                    addRegion(buffer, "Kalos", "seel", "jynx");
                    addRegion(buffer, "test", "mew");

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
                    SetPartyCobblemonPacket.SERIALIZER.write(buffer, new SetPartyCobblemonPacket(player.getUuid(), (short) 0));
                    player.sendPluginMessage("cobblemon:set_party_pokemon", buffer.read(NetworkBuffer.RAW_BYTES));


//                    Tag<Pokemon>e = new Tag<Pokemon>();


                }
            }
        }).addListener(PlayerSpawnEvent.class, playerLoginEvent -> {
            Player player = playerLoginEvent.getPlayer();
            if (!playerLoginEvent.isFirstSpawn()) {
                return;
            }

            NetworkBuffer buffer = NetworkBuffer.resizableBuffer(0);

            SyncPacket.SERIALIZER.write(buffer,
                    new SyncPacket("cobblemon:general", false, false, false,
                            false, false, null, null, null));

            player.sendPluginMessage("cobblemon:set_client_playerdata", buffer.read(NetworkBuffer.RAW_BYTES));

            buffer = NetworkBuffer.resizableBuffer(1024);
            NetworkBuffer listBuffer = NetworkBuffer.resizableBuffer(512);

            CobblemonEntitiesBuilder builder = new CobblemonEntitiesBuilder()
                    .addEntity("charmander", "Charmander", "fire")
                    .addEntity("pikachu", "Pikachu", "electric")
                    .addEntity("mew", "Mew", "psychic")
                    .addEntity("seel", "Seel", "water")
                    .addEntity("jynx", "Jynx", "dark");

            listBuffer.write(NetworkBuffer.VAR_INT, builder.getEntities().size()); // count of pokemon to add
            builder.build(listBuffer);

            byte[] bytes = listBuffer.read(NetworkBuffer.RAW_BYTES);
            buffer.write(NetworkBuffer.INT, bytes.length);
            buffer.write(NetworkBuffer.RAW_BYTES, bytes);

            player.sendPluginMessage("cobblemon:species_sync", buffer.read(NetworkBuffer.RAW_BYTES));

            Main.dataMap.put(player.getUuid(), new PlayerData(player));

            player.sendMessage(Component.text("Successfully synced with Cobblemon!", NamedTextColor.GREEN));
        });
    }

    /**
     * Adds a new started to the last added region. For internal addRegion method use
     *
     * @param buffer
     * @param id     Cobblemon id
     */
    private void addStarter(NetworkBuffer buffer, String id) {
        buffer.write(NetworkBuffer.STRING, "cobblemon:" + id); //cobblemon.species.charmander
        buffer.write(NetworkBuffer.BYTE, (byte) 0); // aspects size?
    }

    /**
     * Adds a new region
     *
     * @param buffer
     * @param name      Name/id of the region
     * @param cobblemon String list of cobblemon id's to add to the region
     */
    private void addRegion(NetworkBuffer buffer, String name, String... cobblemon) {
        buffer.write(NetworkBuffer.STRING, name); // cat name
        buffer.write(NetworkBuffer.STRING, "cobblemon.starterselection.category." + name.toLowerCase()); // display name
        buffer.write(NetworkBuffer.INT, cobblemon.length); // size of list?

        for (String id : cobblemon) {
            addStarter(buffer, id);
        }
    }
}
