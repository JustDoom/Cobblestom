package com.imjustdoom.packet.in.starter;

import com.imjustdoom.PlayerData;
import com.imjustdoom.packet.handler.PacketHandler;
import net.kyori.adventure.text.Component;
import net.minestom.server.network.NetworkBuffer;
import net.minestom.server.network.NetworkBufferTemplate;

public record RequestStarterScreenPacket() implements PacketHandler {
    public static final NetworkBuffer.Type<RequestStarterScreenPacket> SERIALIZER = NetworkBufferTemplate.template(RequestStarterScreenPacket::new);

    @Override
    public void handle(PlayerData player) {
        // TODO: Branches. Already selected, cannot choose or send the options. Store player data to know

        if (false) {// Already selected a starter
            player.getPlayer().sendMessage(Component.translatable("cobblemon.ui.starter.alreadyselected"));
            return;
        } else if (false) {// Cannot choose
            player.getPlayer().sendMessage(Component.translatable("cobblemon.ui.starter.cannotchoose"));
            return;
        }

        NetworkBuffer buffer = NetworkBuffer.resizableBuffer(0);

        buffer.write(NetworkBuffer.INT, 3); //category size

        addRegion(buffer, "Kanto", "charmander", "pikachu");
        addRegion(buffer, "Kalos", "seel", "jynx");
        addRegion(buffer, "test", "mew");

        player.getPlayer().sendPluginMessage("cobblemon:open_starter", buffer.read(NetworkBuffer.RAW_BYTES));
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
