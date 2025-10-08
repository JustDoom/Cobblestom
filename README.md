# Cobblestom

Cobblestom is an attempt to "port" the [Cobblemon](https://cobblemon.com) mod over to a [Minestom](https://minestom.net/)
library for servers to use. Started towards more custom Cobblemon servers than survival servers. It would allow Minestom
level customisability for server creation with support for Cobblemon. It could also be ported to a Plugin based server
such as PaperMC or PurpurMC in theory.

**Developed for Cobblemon 1.6.1 for Minecraft 1.21.1**

Very basic showcase on Minestom
https://www.youtube.com/watch?v=xqYqRYz0gPA

Discord: https://discord.gg/ydGK5jYV6t

## What is our goal

The goal of this is to implement a majority of what the Cobblemon project contains, which does include some Vanilla
functionality such as path finding, but it will NOT be identical to Vanilla.

Will this likely ever be done? Probably not, but this was a fun idea and have been very interesting so far!

## Using

Currently, this is just the whole project, not specifically a library. So figure it out however you want, fork this or
still add it as a dependency and use the relevant classes. This will be changed when it is in a better state.
I think it currently requires a [custom version](https://github.com/lowercasebtw/minestom_1-21-1) of Minestom because 
they used shorts to handle id's and adding all of Cobblemon's blocks and items went over the limit. It should be fixed
in future Minestom versions past 1.21.1 for when Cobblemon finally updates.

### Setting up with Minestom

Once a Minestom project is set up, you can integrate Cobblestom into it.

When a player connects to the server they need to be synced to it properly, you can do this with
`Cobblemon.get().syncPlayer(event.getPlayer());`. It should be able to be run at any time (Only do once) but it should
be done on join, otherwise it may cause unexpected issues.

Before the Minestom server has started, you can use `new Cobblemon().start();` to initialise the Cobblestom library.
It starts parts of the project such as the package listeners.

With that the project should be functional!

### Using other parts

Currently, the only special things you can do with the code is with items/blocks and sounds. The `CobblemonMaterials`
interface contains a list of every single "material" you can obtain. Functionality from there can be implemented
yourself.
To give an item you can do `player.getInventory().addItemStack(ItemStack.of(CobblemonMaterials.IRON));`.

There are also sounds you can play from the Cobblemon mod. Currently there is no proper class that contains a list of
all the sounds so you will need to look inside the sounds.json file inside the `resources` directory of the project.
To play a sound you will need to know the key of it yourself and play it using
`event.getInstance().playSound(Sound.sound(Key.key("cobblemon:poke_ball.send_out"), Sound.Source.NEUTRAL, 1, 1));`.

## Developing

Open in an editor such as IntelliJ and import the gradle project. Currently, this itself is just the whole server too,
eventually this will be turned into just a library for use. This will be fixed when the project progresses more.

Based on the 1.6.1 version of Cobblemon for Minecraft 1.21.1.

If using their branch to modify the client and test, you may need to change the `fabric-permissions-api` option in
`gradle/libs.versions.toml` to `0.3.1` from `0.2-SNAPSHOT`.

# Credit

- [Cobblemon](https://cobblemon.com) HUGE credit to the team working on Cobblemon, it is an incredible mod and it is 
super cool.
- [Minestom](https://minestom.net) I have been around the community for a long time now and the stuff the people make is
super cool as well. The idea for it is super cool and super useful for people who don't need most vanilla feature. Thank
you to everyone who has helped me there!
- [lowercasebtw](https://github.com/lowercasebtw) Thank you to lowercasebtw as well for helping me with issues and
creating a fixed version of the Minestom library. Since it originally broke because of the amount of new items and 
blocks lol