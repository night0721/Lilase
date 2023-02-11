package me.night0721.lilase.utils.ah;

public class Item {
    public String query;
    public ItemType type;
    public Integer price;
    public ItemTier tier;

    public Item(String query, ItemType type, Integer price, ItemTier tier) {
        this.query = query;
        this.type = type;
        this.price = price;
        this.tier = tier;
    }
}
