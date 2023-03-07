## What's Limited Life?
### Basics
Limited Life is a hardcore-like gamemode, where you only have 24 hours to live. Every time you die, you lose 1 hour of time. Every time you kill someone, you gain 30 minutes of time. Players with more than 16 hours of time remaining will have a green coloured name, players with more than 8 hours of time remaining will have a yellow coloured name, and finally, players with less than 8 remaining will have a red coloured name. You're only allowed to kill people who have a "higher" colour than you. So green names aren't allowed to kill anyone, yellow names are only allowed to kill green names and red names are allowed to kill anyone. There's one exception to this rule; The Boogeyman!

### Boogeyman
Once every session, the Boogeyman should be rolled. When chosen as the Boogeyman, you must kill a non-red name before the end of the session. Failure to do so will result in your time to drop until it reaches the next colour. That means you could lose up to 8 hours of time if you fail! Red names can't be chosen as the boogeyman.

### Recommendations
Limited Life is intended to be played with around 14 players, on a 700x700 map.  
The server shouldn't be on 24/7. Instead, game sessions should be hosted, to make sure everyone's able to progress at the same time. I personally recommend keeping them around 3 hours long, just like in the [real series](https://youtu.be/gzaIHdjLIyM).

## Plugin Features
### Commands
**/lf help <boogeyman|modifytime|timer>** - Provides a detailed explanation of the specified command.  
**/lf reload** - Reloads the plugin and the its data files  
**/lf timer <start|pause|reset> [player]** - Starts/Resumes and pauses the timer for everyone, or the specified player. Also resets timer data.  
**/lf boogeyman <roll|cure|punish|clear>** - Rolls the boogeyman, cures the specified player, punishes all boogeymen for not securing a kill, and clears the Boogeyman list.  
**/lf modifytime <player> <+|->\<num><h|m|s>** - Adds/subtracts from the specified player's time in hours, minutes or seconds. (Last Argument Example: -3h)

### Enchantment Limitations
You may configure the plugin to limit the level on enchantments when combined in an anvil. This is set to level 1 by default.

### Disabling Certain Items
The Enchantment Table may be configured to be (un)craftable. You can also make it (un)breakable, and immortal when it drops as an item, meaning it won't despawn or take damage from anything.  
Helmets are unobtainable by default, but may be enabled.  
Bookshelves are unobtainable by default, but may be enabled.

### Custom Recipes
A few recipes have been altered/added in order to enhance your experience. They may be disabled if you see fit.  
<br>
**Paper TNT**  
![image](https://user-images.githubusercontent.com/75913945/223511629-3582983f-d9ce-424d-aa5d-bbfe73bd37a6.png)  
**Craftable Saddle**  
![image](https://user-images.githubusercontent.com/75913945/223511868-851c41c6-a552-4989-91a0-b0446ad6d0ba.png)  
**Craftable Name Tag**  
![image](https://user-images.githubusercontent.com/75913945/223511972-1d59cc36-dec0-41ea-815a-a51129d232f5.png)  



### Documented Configuration File
The plugin features a well documented and relatively detailed config file. The defaults of which may be found [here](https://github.com/RowwYourBoat/LimitedLife/blob/master/src/main/resources/config.yml).

## Additional Information
For support, please join my public [Limited Life Discord Server](https://discord.com/invite/PEsQSZVSwU) or contact me privately: Rowan#4321  
Credits to [Grian](https://youtu.be/gzaIHdjLIyM) for coming up with the idea for Limited Life!
