<h1> Auction House Flipper - Lilase</h1>
Forge mod will automatiaclly buy item according to your configuration, and send you a webhook when it has been bought.
If you have any questions please join https://night0721.me/discord for support
For how to use https://github.com/night0721/lilase#how-to-use

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
- v1.0.1-beta - Initial Release, with auto buy
- v1.0.2 - Added flipper, profit check, auto post
- v1.0.21 - 
     Auto reconnect to server when disconnected
     Ungrab mouse when sniping/flipping
     Auto skip confirmation screen, with GUI showing coord fps and time and more error catching
     However config still broken, keep using Lilase.cfg first. Will fix config and discord in next update
     Some movement during sniping so hypixel won't send to afk
- v1.0.22 - Fatal bug fixed (before it will crash when open controls gui), also added title for process
- v1.0.23 - More lines in GUI, flipped, posted, sniped
- v1.0.24-beta - 
     Remove the use of org.json dependency 
     Fixing flipper could not be started when item is sniped
     Making send auction to webhook to be toggleable
     Added blacklist for sniping 
     Changing API URL to a more stable one
     Have at most 99 items to snipe at the same time, with 99 items to blacklist too
     Mod will now only generate first three items config in the file, but you can add yourself
     Fetching price of item earlier so it won't be bugged when posting on auctio house
     Fix the weird lag when doing random motion when sniping
     Changed names in config so it is more readable and understanable
     Added days in GUI
     
# To Do Features:
- Console client??
- Page flipper for specific items(query)
- Wither Impact in lore check
- Blue omelette in lore check
- Cookie compatible
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
    I:ProfitPercentage=400
    I:ReconnectDelay=20
    B:SendMessageToWebhook=true
    S:Webhook=Paste your webhook here
    B:checkProfitPercentageBeforeBuy=false
}
```