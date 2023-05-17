<h1> Auction House Flipper - Lilase</h1>
<div style="text-align: center;">
<img align="center" src="https://cdn.discordapp.com/attachments/842014909264953354/1082373275038003210/lilase.png" width="300" alt="icon"/>
</div>

# Introduction:
A simple Forge mod will automatiaclly buy item according to your configuration, and send you a webhook when it has been bought.
If you have any questions please join [Discord Server](https://night0721.me/discord) for support
For how to use, see **[here](https://github.com/night0721/lilase#how-to-use)**

# Features:
- Auto walk to the auction house
- Auto sell after buying with profit check, so profit is ensured
- Webhook system to send you that an item has been bought, sold, flipped
- Failsafe in Limbo
- Auto reconnect to server when disconnected
- Auto skip confirmation screen
- GUI showing coordinates, fps and clock and stats like auctions bought, sniped, flipped
- Auto ungrab mouse when sniping/flipping
- Auto movement during sniping so hypixel won't send to afk
- Cookie Compatible
- Configurable bed spam delay
- Modules toggleable in game
- Check maximum profit percentage before buying so duped items won't be bought
- COFL macro
- auto sell to cofl macro
- Server ID on scoreboard hider
- page flipper [WIP]

# How to use:
1. Download the latest version of Lilase from [here](https://github.com/night0721/Lilase/releases)
2. Download the latest version of COFL from [here]([https://github.com/night0721/Lilase/releases](https://github.com/Coflnet/SkyblockMod/releases/latest))
3. Put them into %APPDATA%/.minecraft/mods
4. Start forge 1.8.9
5. Press * key to change any config you want including webhooks
6. Press END key to start cofl macro(or change it in control settings)
10. For example configuration, see [here](https://github.com/night0721/Lilase#example-config)

# Changelog:
[ChangeLog](https://github.com/night0721/Lilase/blob/master/.github/CHANGELOG.md)
     
# To Do Features:
- Console client??
- Page flipper for specific items(query)
- pre api?

# Example Config:
```json
{
  "SendMessageToWebhook": true,
  "Webhook": "",
  "ReconnectDelay": 20,
  "BedSpamDelay": 100,
  "OnlySniper": false,
  "BedSpam": true,
  "GUI": true,
  "GUI_COLOR": -1,
  "SniperMode": true
}
```
