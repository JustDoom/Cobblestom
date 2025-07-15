package com.imjustdoom.packet.out;

import com.imjustdoom.packet.handler.Packet;
import net.minestom.server.network.NetworkBuffer;
import net.minestom.server.network.NetworkBufferTemplate;
import net.minestom.server.utils.NamespaceID;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public record SetClientPlayerDataPacket(String id, boolean incremental, boolean promptStarter, boolean starterLocked,
                                        boolean starterSelected, boolean showChallengeLabel, @Nullable UUID starterUUID,
                                        @Nullable Boolean resetStarters, @Nullable NamespaceID battleTheme) implements Packet {
    public static final NetworkBuffer.Type<SetClientPlayerDataPacket> SERIALIZER = NetworkBufferTemplate.template(
            NetworkBuffer.STRING, SetClientPlayerDataPacket::id,
            NetworkBuffer.BOOLEAN, SetClientPlayerDataPacket::incremental,
            NetworkBuffer.BOOLEAN, SetClientPlayerDataPacket::promptStarter,
            NetworkBuffer.BOOLEAN, SetClientPlayerDataPacket::starterLocked,
            NetworkBuffer.BOOLEAN, SetClientPlayerDataPacket::starterSelected,
            NetworkBuffer.BOOLEAN, SetClientPlayerDataPacket::showChallengeLabel,
            NetworkBuffer.STRING.transform(UUID::fromString, UUID::toString).optional(), SetClientPlayerDataPacket::starterUUID,
            NetworkBuffer.BOOLEAN.optional(), SetClientPlayerDataPacket::resetStarters,
            NetworkBuffer.STRING.transform(NamespaceID::from, NamespaceID::asString).optional(), SetClientPlayerDataPacket::battleTheme,
            SetClientPlayerDataPacket::new
    );
}
