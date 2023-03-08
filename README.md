<h1> Auction House Flipper - Lilase</h1>
<div style="text-align: center;">
<img align="center" src="https://cdn.discordapp.com/attachments/842014909264953354/1082373275038003210/lilase.png" width="300" />
</div>

# Introduction:
A simple Forge mod will automatiaclly buy item according to your configuration, and send you a webhook when it has been bought.
If you have any questions please join [Discord Server](https://night0721.me/discord) for support
For how to use, see **[here](https://github.com/night0721/lilase#how-to-use)**

# Features:
- Auto buy any item you want, with query customised by yourself
- Auto walk to the auction house
- Auto sell after buying with profit check, so profit is ensured
- Can customise fetch time (the faster, the higher chance to get the item you want)
- Webhook system to send you that an item has been bought
- Failsafe in Limbo
- Auto reconnect to server when disconnected
- Auto skip confirmation screen
- GUI showing coordinates, fps and clock and stats like auctions bought, sniped, flipped
- Auto ungrab mouse when sniping/flipping
- Auto movement during sniping so hypixel won't send to afk

# How to use:
1. Download the latest version of Lilase from [here](https://github.com/night0721/Lilase/releases)
2. Put it into %APPDATA%/.minecraft/mods
3. Start forge 1.8.9
4. Go to %APPDATA%/.minecraft/config
5. Open Notepad/Notepad++/Visual Studio Code/or any IDE you like to edit Lilase.cfg
6. Fill item(s) you want to buy, only fill if you need to, don't randomly put something at only one field
7. Note: You can create as many item as you want, just copy and paste the item section and change the number(the highest number will have the last priority on checking auctions, vice versa)
8. save the file and press END key to start auction house sniper(or change it in control settings)
9. For example configuration, see [here](https://github.com/night0721/Lilase#example-config)

# **IMPORTANT INFO**

List of things you can put in Tier section
```
ANY, COMMON, UNCOMMON, RARE, EPIC, LEGENDARY, MYTHIC, DIVINE, SPECIAL, VERY_SPECIAL
```
List of things you can put in Type section
```
ANY, WEAPON, ARMOR, ACCESSORIES, CONSUMABLES, BLOCKS, MISC
```
# Changelog:
[ChangeLog](https://github.com/night0721/Lilase/blob/master/.github/CHANGELOG.md)
     
# To Do Features:
- Console client??
- Page flipper for specific items(query)
- Wither Impact in lore check
- Blue omelette in lore check
- pre api?
- upper bound and lower bound for sniping as upper bound is specified right now

# Example Config:
```cfg
item1 {
    S:Name=Livid Dagger
    I:Price=5000000
    S:Tier=LEGENDARY
    S:Type=WEAPON
}

blacklist1 {
    S:Name=Rune
    I:Price=1
    S:Tier=ANY
    S:Type=ANY
}
main {
    S:APIKey=Paste your API key here
    I:AuctionHouseDelay=9
    B:GUI=true
    I:GUI_COLOR=-65536
    I:MinimumProfitPercentage=400
    I:ReconnectDelay=20
    B:SendMessageToWebhook=true
    S:Webhook=Paste your webhook here
    B:checkProfitPercentageBeforeBuy=false
}
```