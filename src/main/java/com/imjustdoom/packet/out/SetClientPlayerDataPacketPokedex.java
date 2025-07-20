package com.imjustdoom.packet.out;

import com.imjustdoom.packet.handler.Packet;
import net.minestom.server.network.NetworkBuffer;
import net.minestom.server.network.NetworkBufferTemplate;
import net.minestom.server.utils.NamespaceID;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public record SetClientPlayerDataPacketPokedex(String id, boolean incremental, boolean promptStarter, boolean starterLocked,
                                               boolean starterSelected, boolean showChallengeLabel, @Nullable UUID starterUUID,
                                               @Nullable Boolean resetStarters, @Nullable NamespaceID battleTheme) implements Packet {
    public static final NetworkBuffer.Type<SetClientPlayerDataPacketPokedex> SERIALIZER = NetworkBufferTemplate.template(
            NetworkBuffer.STRING, SetClientPlayerDataPacketPokedex::id,
            NetworkBuffer.BOOLEAN, SetClientPlayerDataPacketPokedex::incremental,
            NetworkBuffer.BOOLEAN, SetClientPlayerDataPacketPokedex::promptStarter,
            NetworkBuffer.BOOLEAN, SetClientPlayerDataPacketPokedex::starterLocked,
            NetworkBuffer.BOOLEAN, SetClientPlayerDataPacketPokedex::starterSelected,
            NetworkBuffer.BOOLEAN, SetClientPlayerDataPacketPokedex::showChallengeLabel,
            NetworkBuffer.STRING.transform(UUID::fromString, UUID::toString).optional(), SetClientPlayerDataPacketPokedex::starterUUID,
            NetworkBuffer.BOOLEAN.optional(), SetClientPlayerDataPacketPokedex::resetStarters,
            NetworkBuffer.STRING.transform(NamespaceID::from, NamespaceID::asString).optional(), SetClientPlayerDataPacketPokedex::battleTheme,
            SetClientPlayerDataPacketPokedex::new
    );
}
