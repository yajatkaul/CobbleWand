![Replace this with a description](https://cdn.modrinth.com/data/cached_images/09c318bc5a537e82adbf856de690e2342acf10a3.png)
Adds a wand to the game to allow spawning of pokemons in the wild or their statues in Cobblemon. Inspired from pixelmon's wand. This development was funded via jusjohn55 to bring it to life for the community.  

## Permissions

CobbleWand supports permission nodes for menu access and critical actions.  
If no permission provider is available, CobbleWand falls back to OP level 2.

- `cobblewand.menu.open`: Allows opening the CobbleWand menu.
- `cobblewand.pokemon.set`: Allows using the Set action in the menu.
- `cobblewand.party.add`: Allows adding the configured Pokemon to the player's party.
- `cobblewand.statue.set`: Allows enabling/disabling statue behavior.

### LuckPerms examples

Admin full access:

```bash
/lp group admin permission set cobblewand.menu.open true
/lp group admin permission set cobblewand.pokemon.set true
/lp group admin permission set cobblewand.party.add true
/lp group admin permission set cobblewand.statue.set true
```

Moderator without statue permissions:

```bash
/lp group mod permission set cobblewand.menu.open true
/lp group mod permission set cobblewand.pokemon.set true
/lp group mod permission set cobblewand.party.add true
/lp group mod permission set cobblewand.statue.set false
```
