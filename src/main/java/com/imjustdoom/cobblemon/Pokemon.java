package com.imjustdoom.cobblemon;

import net.minestom.server.network.NetworkBuffer;
import net.minestom.server.network.NetworkBufferTemplate;

public record Pokemon(String test) {

    public static final NetworkBuffer.Type<Pokemon> SERIALIZER = NetworkBufferTemplate.template(
            NetworkBuffer.STRING, Pokemon::test,
            Pokemon::new
    );
}
