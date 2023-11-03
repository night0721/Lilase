<h1 style="text-align:center;"> Auction House Flipper - Lilase</h1>
<div style="display:flex;align-items: center;justify-content: center;">
<img src="https://raw.githubusercontent.com/night0721/Lilase/master/src/main/resources/assets/lilase.jpg" width="300" alt="icon"/>
</div>

# Introduction:
A simple QOL Forge mod runs on 1.8.9, automatiaclly buy item and sell items according to flips in COFL for profit, and send you a webhook when it has been bought. With the support of remote control allows you to control the mod through Discord.
If you have any questions please join [Discord Server](https://night0721.me/discord) for support
For how to use, see **[here](https://github.com/night0721/lilase#how-to-use)**

# Features:
- COFL macro
- Full skip confirmation screen
- Auto sell with COFL recommended pricing
- Auto claim auctions
- Auto relist expired auctions
- Remote control with screenshot, showing stats, and more
- Mod ID hider
- Supports both cookie or non cookie users
- Webhook system to send you that an item has been bought, sold, flipped, or any error occured with many information
- Configurations for every module with every modules toggleable
- Supports using and not using Cookie
- Auto walk to the auction house
- Failsafe in Limbo
- Auto reconnect to server when disconnected
- GUI showing coordinates, fps and clock and stats like auctions bought, sniped, flipped
- Auto ungrab mouse when sniping/flipping
- Anti-AFK movement during sniping
- Configurable bed spam delay
- Custom listing time for auctions
- GUI to tell you to update the mod
- Relist delay so you look less suspicious
- Scoreboard hider to hide server ID
- page flipper [WIP]
- Mod ID hider
- Short number to list auctions
- Detection of captcha

# Download:
1. Download the latest version of Lilase from [here](https://github.com/night0721/Lilase/releases/latest)
2. Download the latest version of COFL from [here](https://github.com/Coflnet/SkyblockMod/releases/latest)
3. Put them into %APPDATA%/.minecraft/mods
4. Start forge 1.8.9
5. Press N key to change any config you want including webhooks
6. Press M key to start cofl macro(or change it in control settings)
7. For example configuration, see [here](https://github.com/night0721/Lilase#example-config)

# Remote Control:
1. Go to [Discord Developer Portal](https://discord.com/developers/applications) and create a new application
2. Go to Bot tab and create a new bot
3. Copy the token and paste it into the config where it says "BotToken"
4. Go to OAuth2 tab and select bot and administrator, then copy the link and paste it into your browser
5. Select the server you want to add the bot to and click authorize
6. Copy the channel ID and paste it into the config where it says "LogChannel"
7. Restart the mod
8. Now you can use the remote control commands by typing `/`
9. For example, `/stats` will show you current session statistics

# How to get logs:
1. First go to the game folder, %APPDATA%/Roaming/.minecraft (default) or depends on whatever client you using
[![ExampleDir](https://cdn.discordapp.com/attachments/1085273638476992662/1109582318236078290/image.png)](https://cdn.discordapp.com/attachments/1085273638476992662/1109582318236078290/image.png)
2. Go to logs folder
3. Select latest.log and send it to Discord for developer to help you

# Changelog:
[ChangeLog](https://github.com/night0721/Lilase/blob/master/.github/CHANGELOG.md)


# Example Config:
```json
{
  "RemoteControl": true,
  "BotToken": "OTE1NDk3NDQ1OTMwODY2MjA=.QREQw3.ASDU238qw9adsfdWEfsgsdfg",
  "LogChannel": "91549744593086620",
  "Webhook": "https://discord.com/api/webhooks/81963527359311890/MIIDBjCCAe4CCQDqzJZzANBgkqhkiG9w0BAQsFADBFMQswCQYDVQQGEwJjbjELMAkGA1"
}
```
