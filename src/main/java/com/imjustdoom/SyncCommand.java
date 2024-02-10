package com.imjustdoom;

import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.entity.Player;
import net.minestom.server.network.NetworkBuffer;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class SyncCommand extends Command {

    public SyncCommand() {
        super("sync");
        addSyntax(this::execute);
    }

    private void execute(@NotNull CommandSender commandSender, @NotNull CommandContext commandContext) {
        NetworkBuffer buffer = new NetworkBuffer(1024);

        // some client sync stuff so it knows cobblemon is available
        buffer.write(NetworkBuffer.BOOLEAN, false);
        buffer.write(NetworkBuffer.BOOLEAN, false);
        buffer.write(NetworkBuffer.BOOLEAN, false);

        buffer.write(NetworkBuffer.OPT_UUID, UUID.randomUUID()); // think this can be null
        buffer.write(NetworkBuffer.BOOLEAN, false);

        ((Player) commandSender).sendPluginMessage("cobblemon:set_client_playerdata", buffer.readBytes(buffer.writeIndex()));

        buffer = new NetworkBuffer(1024);

        buffer.write(NetworkBuffer.VAR_INT, 5); // count of pokemon to add

        addEntity(buffer, "charmander", "Charmander", "fire");
        addEntity(buffer, "pikachu", "Pikachu", "electric");
        addEntity(buffer, "mew", "Mew", "psychic");
        addEntity(buffer, "seel", "Seel", "water");
        addEntity(buffer, "jynx", "Jynx", "dark");

        ((Player) commandSender).sendPluginMessage("cobblemon:species_sync", buffer.readBytes(buffer.writeIndex()));
    }

    private void addEntity(NetworkBuffer buffer, String id, String name, String type) {
        buffer.write(NetworkBuffer.STRING, "cobblemon:" + id); // species i think

        buffer.write(NetworkBuffer.BOOLEAN, true);
        buffer.write(NetworkBuffer.STRING, name);
        buffer.write(NetworkBuffer.INT, 5);

        // base stats map aaaa
        buffer.write(NetworkBuffer.VAR_INT, 0);
//        buffer.write(NetworkBuffer.INT, 6);

        buffer.write(NetworkBuffer.STRING, type);
        buffer.write(NetworkBuffer.STRING, "");
        buffer.write(NetworkBuffer.STRING, "medium_slow");
        buffer.write(NetworkBuffer.FLOAT, 6.0f);
        buffer.write(NetworkBuffer.FLOAT, 85.0f);
        buffer.write(NetworkBuffer.FLOAT, 0.7f);

        // dimesnions
        buffer.write(NetworkBuffer.FLOAT, 0.7f);
        buffer.write(NetworkBuffer.FLOAT, 1.1f);
        buffer.write(NetworkBuffer.BOOLEAN, false);

        buffer.write(NetworkBuffer.VAR_INT, 0);

        buffer.write(NetworkBuffer.VAR_INT, 1);
        buffer.write(NetworkBuffer.STRING, "cobblemon.species." + id + ".desc");

        buffer.write(NetworkBuffer.VAR_INT, 0);

        buffer.write(NetworkBuffer.STRING, "cobblemon:battle.pvw.default");
    }
}