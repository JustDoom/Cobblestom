package com.imjustdoom;

import com.imjustdoom.cobblemon.Cobblemon;
import com.imjustdoom.packet.handler.Packet;
import com.imjustdoom.packet.out.SetClientPlayerDataPacket;
import net.minestom.server.entity.Player;
import net.minestom.server.network.NetworkBuffer;
import net.minestom.server.utils.NamespaceID;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.UUID;

public class PlayerData {
    private final Player player;

    public boolean starterPrompted = false;
    public boolean starterLocked = false;
    public boolean startedSelected = false;
    public boolean showChallengeLabel = false;
    public UUID starterUuid;
    public boolean resetStarters = false;
    public @Nullable NamespaceID battleTheme;

    public PlayerData(Player player) {
        this.player = player;
    }

    public SetClientPlayerDataPacket createPacket(String id, boolean incremental) {
        return new SetClientPlayerDataPacket(id, incremental, this.starterPrompted, this.starterLocked, this.startedSelected, this.showChallengeLabel, this.starterUuid, this.resetStarters, this.battleTheme);
    }

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

    public Player getPlayer() {
        return this.player;
    }
}
