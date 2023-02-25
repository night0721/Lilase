<h1> Auction House Flipper - Lilase</h1>
Forge mod will automatiaclly buy item according to your configuration, and send you a webhook when it has been bought.

# Features:
- Auto buy any item you want, with query customised by yourself
- Auto walk to the auction house
- Auto sell after buying with profit check, so profit is ensured
- Can customise fetch time (the faster, the higher chance to get the item you want)
- Webhook system to send you that an item has been bought
- Failsafe in Limbo
- Auto reconnect to server when disconnected
- Auto skip confirmation screen
- GUI showing coordinates, fps and clock and more error
- Auto ungrab mouse when sniping/flipping

# Changelog:
- v1.0.1-beta - Initial Release, with auto buy
- v1.0.2 - Added flipper, profit check, auto post
- v1.0.21 - 
     Auto reconnect to server when disconnected
     ungrab mouse when sniping/flipping
     auto skip confirmation screen, with GUI showing coord fps and time and more error catching
     however config still broken, keep using Lilase.cfg first. Will fix config and discord in next update
    

# Example Config:
```cfg
item1 {
    S:Name= 
    I:Price=1000
    S:Tier=ANY
    S:Type=ANY
}


item2 {
    S:Name=Livid Dagger
    I:Price=5000000
    S:Tier=LEGENDARY
    S:Type=WEAPON
}


item3 {
    S:Name=
    I:Price=0
    S:Tier=
    S:Type=
}

main {
    S:APIKey=Paste your API key here
    I:AuctionHouseDelay=15
    B:GUI=true
    I:GUI_COLOR=-7237653
    I:Multiplier=400
    I:ReconnectDelay=20
    S:Webhook=Paste your webhook here
    B:checkMultiplierBeforeBuy=true
}
```