package com.imjustdoom.packet.out;

import net.minestom.server.network.NetworkBuffer;
import net.minestom.server.network.NetworkBufferTemplate;
import net.minestom.server.utils.NamespaceID;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public record SyncPacket(String id, boolean incremental, boolean promptStarter, boolean starterLocked, boolean starterSelected,
                         boolean showChallengeLabel, @Nullable UUID starterUUID, @Nullable Boolean resetStarters,
                         @Nullable NamespaceID battleTheme) {

    public static final NetworkBuffer.Type<SyncPacket> SERIALIZER = NetworkBufferTemplate.template(
            NetworkBuffer.STRING, SyncPacket::id,
            NetworkBuffer.BOOLEAN, SyncPacket::incremental,
            NetworkBuffer.BOOLEAN, SyncPacket::promptStarter,
            NetworkBuffer.BOOLEAN, SyncPacket::starterLocked,
            NetworkBuffer.BOOLEAN, SyncPacket::starterSelected,
            NetworkBuffer.BOOLEAN, SyncPacket::showChallengeLabel,
            NetworkBuffer.STRING.transform(UUID::fromString, UUID::toString).optional(), SyncPacket::starterUUID,
            NetworkBuffer.BOOLEAN.optional(), SyncPacket::resetStarters,
            NetworkBuffer.STRING.transform(NamespaceID::from, NamespaceID::asString).optional(), SyncPacket::battleTheme,
            SyncPacket::new
    );
}
