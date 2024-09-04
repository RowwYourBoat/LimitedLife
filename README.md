## What's Limited Life?

<br>

### Basics
Limited Life is a hardcore-like gamemode, in which players only have 24 hours to live. Each death is penalized with one hour of time lost. Killing someone rewards the player with 30 minutes. The color of a player's name is determined by the amount of hours they have left (green for >16, yellow for >8, red for >0). Players are only allowed to kill those who are on a higher color stage than them. There's one exception to this rule; The Boogeyman!

<br>

### Boogeyman
The Boogeyman is recommended to be rolled at least once per session. When a player is chosen as the Boogeyman, they're required to kill another player before the end of the session. Failure to do so will result in their time to drop until it reaches the next color stage. That means players could lose up to 8 hours of time if they fail!

<br>

### Recommendations
Limited Life is intended to be played with around 14 players on a 700x700 map.  
The server shouldn't be on 24/7. 3 hour long "sessions" should be hosted instead, ensuring all players are able to progress at the same time. This is also how it works in the [real series](https://youtu.be/gzaIHdjLIyM).

<br>
<br>

## Plugin Features

<br>

### Commands
- **/lf reload** - Reloads the plugin and its data files.  

<br>

- **/lf modifytime <player> <+|-><num><h|m|s>** - Manage the amount of time a player has. Example for removing 3 hours from someone: "/lf modifytime Rowa_n -3h".  

<br>

- **/lf gettime <player>** - Returns the amount of time the specified player has remaining.  

<br>

- **/lf boogeyman <cancel|clear|cure|punish|roll> [skiprolldelay]**
    - **cancel** - Cancels the pending boogeyman roll.
    - **clear** - Clears the current boogeyman list without penalties or notifications.
    - **cure** - Cures all boogeymen with a notification.
    - **punish** - Inflict a time penalty for all boogeymen.
    - **roll** - Initialize a pending boogeyman roll.
         - **skiprolldelay** - Will cause the countdown to be skipped.  

<br>

- **/lf timer <start|pause|reset> [player]**
    - **start** - Starts/Resumes the global timer or that of the specified player.
    - **pause** - Pauses the global timer or that of the specified player.
    - **reset** - Resets the global timer or that of the specified player.

<br>

- **/lf help <boogeyman|modifytime|timer>** - Provides an explanation about the specified command.  

<br>

### Enchantment Limitations
You may configure the plugin to limit the level on enchantments when combined in an anvil. This is set to level 2 by default.  

<br>

### Potion Whitelist
Because some potions may be considered overpowered for a series like this, I've implemented a way to toggle every existing potion individually.  

<br>

### Disabling Certain Items
Enchantment Tables are very configurable. You may decide whether they're craftable, breakable, and you may also make them immortal when they're on the ground as an item.  
Golden Apples, as well as Enchanted Golden Apples, may be toggled individually.  
Helmets are unobtainable by default, but may be enabled.  
Bookshelves are unobtainable by default, but may be enabled.  

<br>

### Custom Recipes
A few recipes have been altered/added in order to enhance your experience. All of them may be toggled individually.  

**Paper TNT**  
![Paper TNT Recipe](https://user-images.githubusercontent.com/75913945/234309242-83ae0653-676e-42b1-83a8-0616b5f855ac.png)  

**Craftable Saddle**  
![Saddle Recipe](https://user-images.githubusercontent.com/75913945/234309266-198b2ea0-4e93-4d2c-941e-61c507c7d06d.png)  

**Craftable Name Tag**  
![Name Tag Recipe](https://user-images.githubusercontent.com/75913945/234309304-ab1f3d50-8eb1-4746-9b67-4ae3dfe1c044.png)  

**Craftable Slimeball**  
![Slimeball Recipe](https://user-images.githubusercontent.com/75913945/234308028-8ef70336-254f-4a8f-9e81-840d4eb488f2.png)  

<br>  

### Extensive Configuration / Customizability
The plugin features a well documented and relatively detailed config file, making it very easy to customize to your specific needs. The default values may be found [here](https://github.com/RowwYourBoat/LimitedLife/blob/master/src/main/resources/config.yml).
<br>
<br>
<br>
<br>
<br>
## [Discord Integration Guide](https://github.com/RowwYourBoat/LimitedLifeIntegration)
<br>
<br>
<br>

## Additional Information
To enable coloured names in chat while using (a fork of) PaperMC, navigate to the paper-world-defaults.yml file and change the value "use-vanilla-world-scoreboard-name-coloring" to true. You may find this file here: Server Directory\config\paper-world-defaults.yml
<br>
Whilst the global timer's active, players whom are offline will still lose time unless disabled in the configuration file.  
<br>
For support, please join my [Discord Support Server](https://discord.com/invite/phJHjvrdE5)!
<br>
Credits to [Grian](https://youtu.be/gzaIHdjLIyM) and his team for coming up with the idea for Limited Life!
