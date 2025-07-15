package com.imjustdoom.cobblemon;

import com.imjustdoom.Main;
import com.imjustdoom.PlayerData;
import com.imjustdoom.packet.CobblemonPacketListener;
import com.imjustdoom.packet.out.SetClientPlayerDataPacket;
import com.imjustdoom.packet.out.SpeciesSyncPacket;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.event.player.PlayerSpawnEvent;
import net.minestom.server.network.NetworkBuffer;

public class Cobblemon {
    private final CobblemonPacketListener packetListener = new CobblemonPacketListener();

    public Cobblemon() {
        INSTANCE = this;
    }

    public void start() {
        this.packetListener.init();

        MinecraftServer.getGlobalEventHandler().addListener(PlayerSpawnEvent.class, playerLoginEvent -> {
            Player player = playerLoginEvent.getPlayer();
            if (!playerLoginEvent.isFirstSpawn()) {
                return;
            }

            Cobblemon.get().getPacketListener().write(player, "cobblemon:set_client_playerdata", new SetClientPlayerDataPacket("cobblemon:general", false, false, false, false, false, null, null, null));

            // TODO: Write proper packet serializers for these
            NetworkBuffer buffer = NetworkBuffer.resizableBuffer(1024);
            NetworkBuffer listBuffer = NetworkBuffer.resizableBuffer(512);

            CobblemonEntitiesBuilder builder = new CobblemonEntitiesBuilder().addEntity("charmander", "Charmander", "fire").addEntity("pikachu", "Pikachu", "electric").addEntity("mew", "Mew", "psychic").addEntity("seel", "Seel", "water").addEntity("jynx", "Jynx", "dark");

            listBuffer.write(NetworkBuffer.VAR_INT, builder.getEntities().size()); // length of pokemon to add
            builder.build(listBuffer);

            byte[] bytes = listBuffer.read(NetworkBuffer.RAW_BYTES);
            Cobblemon.get().getPacketListener().write(player, "cobblemon:species_sync", new SpeciesSyncPacket(bytes.length, bytes));

            Main.dataMap.put(player.getUuid(), new PlayerData(player));

            player.sendMessage(Component.text("Successfully synced with Cobblemon!", NamedTextColor.GREEN));
        });
    }

    public CobblemonPacketListener getPacketListener() {
        return this.packetListener;
    }

    private static Cobblemon INSTANCE;

    public static Cobblemon get() {
        return INSTANCE;
    }
}
