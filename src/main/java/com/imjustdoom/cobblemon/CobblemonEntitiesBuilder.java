package com.imjustdoom.cobblemon;

import net.minestom.server.network.NetworkBuffer;

import java.util.ArrayList;
import java.util.List;

public class CobblemonEntitiesBuilder {
    private final List<CobblemonEntity> entities = new ArrayList<>();

    public CobblemonEntitiesBuilder addEntity(String id, String name, String type) {
        this.entities.add(new CobblemonEntity(id, name, type));
        return this;
    }

    public List<CobblemonEntity> getEntities() {
        return this.entities;
    }

    public void build(NetworkBuffer buffer) {
        for (CobblemonEntitiesBuilder.CobblemonEntity entity : getEntities()) {
            addEntity(buffer, entity.id, entity.name, entity.type);
        }
    }

    /**
     * I think this adds the entity to the server registry so it can be used
     *
     * @param buffer
     * @param id
     * @param name
     * @param type
     */
    public static void addEntity(NetworkBuffer buffer, String id, String name, String type) {
        buffer.write(NetworkBuffer.STRING, "cobblemon:" + id); // species i think

        buffer.write(NetworkBuffer.BOOLEAN, true);
        buffer.write(NetworkBuffer.STRING, name);
        buffer.write(NetworkBuffer.INT, 5);

        // base stats map aaaa
        buffer.write(NetworkBuffer.VAR_INT, 0);

        buffer.write(NetworkBuffer.STRING, type);

        buffer.write(NetworkBuffer.BOOLEAN, false);

        buffer.write(NetworkBuffer.STRING, "medium_slow");
        buffer.write(NetworkBuffer.FLOAT, 6.0f);
        buffer.write(NetworkBuffer.FLOAT, 85.0f);
        buffer.write(NetworkBuffer.FLOAT, 0.7f);
        buffer.write(NetworkBuffer.FLOAT, 1.0f);

        // dimesnions
        buffer.write(NetworkBuffer.FLOAT, 0.7f);
        buffer.write(NetworkBuffer.FLOAT, 1.1f);
        buffer.write(NetworkBuffer.BOOLEAN, false);

        buffer.write(NetworkBuffer.BYTE, (byte) 0); //

        buffer.write(NetworkBuffer.VAR_INT, 1);
        buffer.write(NetworkBuffer.STRING, "cobblemon.species." + id + ".desc");

        buffer.write(NetworkBuffer.VAR_INT, 0);

        buffer.write(NetworkBuffer.STRING, "cobblemon:battle.pvw.default");

        buffer.write(NetworkBuffer.VAR_INT, 0);

        buffer.write(NetworkBuffer.BOOLEAN, false);

        buffer.write(NetworkBuffer.INT, 0);
        buffer.write(NetworkBuffer.VAR_INT, 0);
    }

    public class CobblemonEntity {
        private final String id;
        private final String name;
        private final String type;

        public CobblemonEntity(String id, String name, String type) {
            this.id = id;
            this.name = name;
            this.type = type;
        }
    }
}
