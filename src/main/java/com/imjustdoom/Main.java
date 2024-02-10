package com.imjustdoom;

import com.imjustdoom.cobblemon.Cobblemon;
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
import net.minestom.server.event.EventNode;
import net.minestom.server.event.item.ItemDropEvent;
import net.minestom.server.event.player.PlayerLoginEvent;
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

import java.util.Random;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class Main {
    public static void main(String[] args) {
        // General minestom setup stuff
        // most code copied from the minestom ce example

        System.setProperty("minestom.use-new-chunk-sending", "true");
        System.setProperty("minestom.experiment.pose-updates", "true");

        MinecraftServer minecraftServer = MinecraftServer.init();

        CommandManager commandManager = MinecraftServer.getCommandManager();
        commandManager.register(new SaveCommand());
        commandManager.register(new SyncCommand());

        MinecraftServer.getGlobalEventHandler().addListener(ItemDropEvent.class, event -> {
            ItemStack item = event.getItemStack();

            ItemEntity itemEntity = new ItemEntity(item);
            itemEntity.setPickupDelay(40, TimeUnit.SERVER_TICK);
            itemEntity.setInstance(event.getPlayer().getInstance(), event.getPlayer().getPosition().add(0, 1.5, 0));
            itemEntity.setVelocity(event.getPlayer().getPosition().direction().mul(6));
        });

        MinecraftServer.getGlobalEventHandler().addListener(ServerListPingEvent.class, event -> {
            ResponseData responseData = event.getResponseData();
            responseData.addEntry(NamedAndIdentified.named("The first line is separated from the others"));
            responseData.addEntry(NamedAndIdentified.named("Could be a name, or a message"));

            // on modern versions, you can obtain the player connection directly from the event
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

            // components will be converted the legacy section sign format so they are displayed in the client
            responseData.addEntry(NamedAndIdentified.named(Component.text("You can use ").append(Component.text("styling too!", NamedTextColor.RED, TextDecoration.BOLD))));

            // the data will be automatically converted to the correct format on response, so you can do RGB and it'll be downsampled!
            // on legacy versions, colors will be converted to the section format so it'll work there too
            responseData.setDescription(Component.text("This is a Minestom Server", TextColor.color(0x66b3ff)));
            //responseData.setPlayersHidden(true);
        });

        InstanceManager instanceManager = MinecraftServer.getInstanceManager();

        InstanceContainer instanceContainer = instanceManager.createInstanceContainer(DimensionType.OVERWORLD);
        instanceContainer.setGenerator(unit -> unit.modifier().fillHeight(0, 40, Block.STONE));
//        instanceContainer.setChunkSupplier();
        instanceContainer.setChunkSupplier(LightingChunk::new);

        var eventHandler = MinecraftServer.getGlobalEventHandler();
        eventHandler.addChild(EventNode.all("node").addListener(PlayerLoginEvent.class, event -> {
            final Player player = event.getPlayer();

            var instances = MinecraftServer.getInstanceManager().getInstances();
            Instance instance = instances.stream().skip(new Random().nextInt(instances.size())).findFirst().orElse(null);
            event.setSpawningInstance(instance);
            int x = Math.abs(ThreadLocalRandom.current().nextInt()) % 500 - 250;
            int z = Math.abs(ThreadLocalRandom.current().nextInt()) % 500 - 250;
            player.setRespawnPoint(new Pos(0, 40f, 0));
        })
                .addListener(PlayerSpawnEvent.class, event -> {
            final Player player = event.getPlayer();
            player.setGameMode(GameMode.CREATIVE);
            player.setPermissionLevel(4);
            ItemStack itemStack = ItemStack.builder(Material.STONE)
                    .amount(64)
                    .meta(itemMetaBuilder ->
                            itemMetaBuilder.canPlaceOn(Set.of(Block.STONE))
                                    .canDestroy(Set.of(Block.DIAMOND_ORE)))
                    .build();
            player.getInventory().addItemStack(itemStack);

//            ItemStack bundle = ItemStack.builder(Material.BUNDLE)
//                    .meta(BundleMeta.class, bundleMetaBuilder -> {
//                        bundleMetaBuilder.addItem(ItemStack.of(Material.DIAMOND, 5));
//                        bundleMetaBuilder.addItem(ItemStack.of(Material.fromNamespaceId("minestomdatagen:villager_in_a_bucket"), 5));
//                    })
//                    .build();
//            player.getInventory().addItemStack(bundle);
//
//            player.getInventory().addItemStack(ItemStack.of(Material.fromNamespaceId("minestomdatagen:villager_in_a_bucket")));
        }));

        new Cobblemon().start(); // start cobblemon related stuff

        minecraftServer.start("0.0.0.0", 25565);
    }
}