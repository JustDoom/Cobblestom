package com.imjustdoom;

import com.imjustdoom.cobblemon.Cobblemon;
import com.imjustdoom.material.CobblemonMaterials;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.CommandManager;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.ItemEntity;
import net.minestom.server.entity.Player;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.item.ItemDropEvent;
import net.minestom.server.event.item.PickupItemEvent;
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent;
import net.minestom.server.event.player.PlayerSpawnEvent;
import net.minestom.server.event.server.ServerListPingEvent;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.InstanceManager;
import net.minestom.server.instance.LightingChunk;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.ping.ResponseData;
import net.minestom.server.utils.identity.NamedAndIdentified;
import net.minestom.server.utils.time.TimeUnit;
import net.minestom.server.world.DimensionType;

import java.util.*;

public class Main {
    public static Map<UUID, PlayerData> dataMap = new HashMap<>();

    public static void main(String[] args) {
        System.setProperty("minestom.registry.unsafe-ops", "true");
        System.setProperty("minestom.use-new-chunk-sending", "true");
        System.setProperty("minestom.experiment.pose-updates", "true");

        MinecraftServer minecraftServer = MinecraftServer.init();

        CommandManager commandManager = MinecraftServer.getCommandManager();
        commandManager.register(new SaveCommand());

        // Item dropping
        MinecraftServer.getGlobalEventHandler().addListener(ItemDropEvent.class, event -> {
            ItemStack item = event.getItemStack();

            ItemEntity itemEntity = new ItemEntity(item);
            itemEntity.setPickupDelay(40, TimeUnit.SERVER_TICK);
            itemEntity.setInstance(event.getPlayer().getInstance(), event.getPlayer().getPosition().add(0, 1.5, 0));
            itemEntity.setVelocity(event.getPlayer().getPosition().direction().mul(6));
        });

        // Server list ping
        MinecraftServer.getGlobalEventHandler().addListener(ServerListPingEvent.class, event -> {
            ResponseData responseData = event.getResponseData();
            responseData.addEntry(NamedAndIdentified.named("The first line is separated from the others"));
            responseData.addEntry(NamedAndIdentified.named("Could be a name, or a message"));

            if (event.getConnection() != null) {
                responseData.addEntry(NamedAndIdentified.named("IP test: " + event.getConnection().getRemoteAddress().toString()));

                responseData.addEntry(NamedAndIdentified.named("Connection Info:"));
                String ip = event.getConnection().getServerAddress();
                responseData.addEntry(NamedAndIdentified.named(Component.text('-', NamedTextColor.DARK_GRAY)
                        .append(Component.text(" IP: ", NamedTextColor.GRAY))
                        .append(Component.text(ip != null ? ip : "???", NamedTextColor.YELLOW))));
                responseData.addEntry(NamedAndIdentified.named(Component.text('-', NamedTextColor.DARK_GRAY)
                        .append(Component.text(" PORT: ", NamedTextColor.GRAY))
                        .append(Component.text(event.getConnection().getServerPort()))));
                responseData.addEntry(NamedAndIdentified.named(Component.text('-', NamedTextColor.DARK_GRAY)
                        .append(Component.text(" VERSION: ", NamedTextColor.GRAY))
                        .append(Component.text(event.getConnection().getProtocolVersion()))));
            }
            responseData.addEntry(NamedAndIdentified.named(Component.text("Time", NamedTextColor.YELLOW)
                    .append(Component.text(": ", NamedTextColor.GRAY))
                    .append(Component.text(System.currentTimeMillis(), Style.style(TextDecoration.ITALIC)))));

            responseData.setDescription(Component.text("This is a Minestom Server", TextColor.color(0x66b3ff)));
        });

        InstanceManager instanceManager = MinecraftServer.getInstanceManager();

        InstanceContainer instanceContainer = instanceManager.createInstanceContainer(DimensionType.OVERWORLD);
        instanceContainer.setGenerator(unit -> unit.modifier().fillHeight(0, 40, Block.STONE));
        instanceContainer.setChunkSupplier(LightingChunk::new);

        GlobalEventHandler eventHandler = MinecraftServer.getGlobalEventHandler();
        eventHandler.addListener(AsyncPlayerConfigurationEvent.class, event -> {
            final Player player = event.getPlayer();

            Set<Instance> instances = MinecraftServer.getInstanceManager().getInstances();
            Instance instance = instances.stream().skip(new Random().nextInt(instances.size())).findFirst().orElse(null);
            event.setSpawningInstance(instance);
            player.setRespawnPoint(new Pos(0, 40f, 0));
        }).addListener(PlayerSpawnEvent.class, event -> {
            final Player player = event.getPlayer();
            player.setGameMode(GameMode.CREATIVE);
            player.setPermissionLevel(4);
            player.getInventory().addItemStack(ItemStack.of(Material.STONE));
            player.getInventory().addItemStack(ItemStack.of(CobblemonMaterials.IRON));
        }).addListener(PickupItemEvent.class, event -> {
            if (event.getLivingEntity() instanceof Player player) {
                player.getInventory().addItemStack(event.getItemStack());
            }

            // Test custom sounds
            event.getInstance().playSound(Sound.sound(Key.key("cobblemon:poke_ball.send_out"), Sound.Source.NEUTRAL, 1, 1));
        });

        new Cobblemon().start(); // start cobblemon related stuff

        minecraftServer.start("0.0.0.0", 25565);
    }
}