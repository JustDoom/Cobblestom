package com.imjustdoom.cobblemon;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PartyStore {
    private final UUID uuid;
    private List<String> mons = new ArrayList<>();

    public PartyStore(UUID uuid) {
        this.uuid = uuid;
    }

    public UUID getUuid() {
        return this.uuid;
    }

    public List<String> getMons() {
        return this.mons;
    }
}
