## What's Limited Life?
### Basics
Limited Life is a hardcore-like gamemode, where you only have 24 hours to live. Every time you die, you lose 1 hour of time. Every time you kill someone, you gain 30 minutes of time. Players with more than 16 hours of time remaining will have a green coloured name, players with more than 8 hours of time remaining will have a yellow coloured name, and finally, players with less than 8 remaining will have a red coloured name. You're only allowed to kill people who have a "higher" colour than you (except for red names). So green names aren't allowed to kill anyone, yellow names are only allowed to kill green names and red names are allowed to kill anyone. There's one exception to this rule; The Boogeyman!

### Boogeyman
Once every session, the Boogeyman should be rolled. When chosen as the Boogeyman, you're required to kill someone before the end of the session. Failure to do so will result in your time to drop until it reaches the next colour. That means you could lose up to 8 hours of time if you fail! You can choose whether red names may be chosen as the boogeyman.

### Recommendations
Limited Life is intended to be played with around 14 players, on a 700x700 map.  
The server shouldn't be on 24/7. Instead, game sessions should be hosted, to make sure everyone's able to progress at the same time. I personally recommend keeping them around 3 hours long, just like in the [real series](https://youtu.be/gzaIHdjLIyM).

## Plugin Features
### Commands
**/lf help <boogeyman|modifytime|timer>** - Provides a detailed explanation of the specified command.  
**/lf reload** - Reloads the plugin and its data files  
**/lf timer <start|pause|reset> [player]** - Starts/Resumes and pauses the timer for everyone, or the specified player. Also resets timer data.  
**/lf modifytime <player> <+|->\<num><h|m|s>** - Adds/subtracts from the specified player's time in hours, minutes or seconds. (Last Argument Example: -3h)  
**/lf gettime <player>** - Returns the amount of time the specified player has remaining.  
**/lf boogeyman <roll|cure|punish|clear> [skiprolldelay]** - Rolls the boogeyman, cures the specified player, punishes all boogeymen for not securing a kill, and clears the Boogeyman list. The last argument only applies to the "roll" command. It will cause the configurable timer to be skipped.  

### Enchantment Limitations
You may configure the plugin to limit the level on enchantments when combined in an anvil. This is set to level 2 by default.  

### Potion Whitelist
Because some potions may be considered overpowered for a series like this, I've implemented a way to toggle every existing potion individually.  

### Disabling Certain Items
Enchantment Tables are very configurable. You may decide whether they're craftable, breakable and you may also make them immortal when they're on the ground as an item.  
Golden Apples, as well as Enchanted Golden Apples, may be toggled individually.  
Helmets are unobtainable by default, but may be enabled.  
Bookshelves are unobtainable by default, but may be enabled.  

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
The plugin features a well documented and relatively detailed config file, making it very easy to customize to your specific needs. The defaults values may be found [here](https://github.com/RowwYourBoat/LimitedLife/blob/master/src/main/resources/config.yml).

## Additional Information
Resource Pack for custom sound effects during countdown/boogeyman selection (recommended):  
https://www.dropbox.com/s/xqlpwrfgnaekctr/Limited%20Life%20Resources.zip?dl=1  
Paste that download link in the server.properties file in the empty resource-pack field to prompt it for all players. To force it, set require-resource-pack to true.  
<br>
To enable coloured names in chat while using (a fork of) PaperMC, navigate to the paper-world-defaults.yml file and change the value "use-vanilla-world-scoreboard-name-coloring" to true. You may find this file here: main folder > config > paper-world-defaults.yml  
<br>
When the global timer's active, offline players will still lose time.  
<br>
For support, please join my [Discord Support Server](https://discord.com/invite/phJHjvrdE5)!
<br>
Credits to [Grian](https://youtu.be/gzaIHdjLIyM) for coming up with the idea for Limited Life!
