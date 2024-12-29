package com.imjustdoom;

import net.minestom.server.entity.Player;

public class PlayerData {

    private final Player player;

    public boolean starterPrompted = false;

    public PlayerData(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return this.player;
    }
}
