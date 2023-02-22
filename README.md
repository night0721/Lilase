<h1> Auction House Flipper - Lilase</h1>
Forge mod will automatiaclly buy item according to your configuration, and send you a webhook when it has been bought.

# Features:
- Autobuy any item you want, customised by yourself
- Can customise fetch time (the faster, the higher chance to get the item you want)
- Webhook system to send you that an item has been bought

# Changelog:
- v1.0.1-beta - Initial Release

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
    S:APIKey=PASTE KEY HERE
    I:AuctionHouseDelay=10
    S:Webhook=PASTE WEBHOOK HERE
}
```