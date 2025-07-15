package com.imjustdoom;

import com.imjustdoom.cobblemon.Cobblemon;
import com.imjustdoom.packet.handler.Packet;
import net.minestom.server.entity.Player;
import net.minestom.server.network.NetworkBuffer;

import java.lang.reflect.Field;

public class PlayerData {
    private final Player player;

    public boolean starterPrompted = false;

    public PlayerData(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return this.player;
    }

    // TODO: Maybe make a player wrapper and move this there
    public <T extends Packet> void write(T packet) {
        NetworkBuffer buffer = NetworkBuffer.resizableBuffer(16);

        try {
            Field serializerField = packet.getClass().getField("SERIALIZER");
            NetworkBuffer.Type<T> serializer = (NetworkBuffer.Type<T>) serializerField.get(null);
            serializer.write(buffer, packet);

            this.player.sendPluginMessage(Cobblemon.get().getPacketListener().getClientPacket(packet), buffer.read(NetworkBuffer.RAW_BYTES));
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
