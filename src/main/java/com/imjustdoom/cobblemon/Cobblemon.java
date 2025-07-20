package com.imjustdoom.cobblemon;

import com.imjustdoom.PlayerData;
import com.imjustdoom.packet.CobblemonPacketListener;
import com.imjustdoom.packet.out.SpeciesSyncPacket;
import com.imjustdoom.packet.out.party.InitialisePartyPacket;
import com.imjustdoom.packet.out.party.SetPartyReferencePacket;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.entity.Player;
import net.minestom.server.network.NetworkBuffer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Cobblemon {
    private final CobblemonPacketListener packetListener = new CobblemonPacketListener();
    private final Map<UUID, PartyStore> playerParties = new HashMap<>();
    private final Map<UUID, PlayerData> playerDataMap = new HashMap<>();

    public Cobblemon() {
        INSTANCE = this;
    }

    public void start() {
        this.packetListener.init();
    }

    /**
     * Syncs a new players into the system. Without it they can not do any cobblemon related things
     * @param player
     */
    public void syncPlayer(Player player) {
        PlayerData playerData = new PlayerData(player);
        PartyStore store = new PartyStore(UUID.randomUUID());
        store.getMons().add("charmander");

        playerData.write(new InitialisePartyPacket(false, store.getUuid(), (byte) 6));
        playerData.write(new SetPartyReferencePacket(store.getUuid()));

        playerData.write(playerData.createPacket("cobblemon:general", false));
//        playerData.write(playerData.createPacket("cobblemon:pokedex", false)); // TODO Aaaaaaaaa

        // TODO: Write proper packet serializers for these
        NetworkBuffer listBuffer = NetworkBuffer.resizableBuffer(512);

        CobblemonEntitiesBuilder builder = new CobblemonEntitiesBuilder().addEntity("charmander", "Charmander", "fire").addEntity("pikachu", "Pikachu", "electric").addEntity("mew", "Mew", "psychic").addEntity("seel", "Seel", "water").addEntity("jynx", "Jynx", "dark");

        listBuffer.write(NetworkBuffer.VAR_INT, builder.getEntities().size()); // length of pokemon to add
        builder.build(listBuffer);

        byte[] bytes = listBuffer.read(NetworkBuffer.RAW_BYTES);
        playerData.write(new SpeciesSyncPacket(bytes.length, bytes));

        this.playerDataMap.put(player.getUuid(), playerData);

        player.sendMessage(Component.text("Successfully synced with Cobblemon!", NamedTextColor.GREEN));
    }

    public CobblemonPacketListener getPacketListener() {
        return this.packetListener;
    }

    public Map<UUID, PlayerData> getPlayerDataMap() {
        return this.playerDataMap;
    }

    private static Cobblemon INSTANCE;

    public static Cobblemon get() {
        return INSTANCE;
    }
}
