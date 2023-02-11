package me.night0721.lilase.utils.ah;

public enum ItemType {
    WEAPON("weapon"),
    ARMOR("armor"),
    ACCESSORIES("accessories"),
    CONSUMABLES("consumables"),
    BLOCKS("blocks"),
    MISC("misc"),;

    public final String lowercase;

    public String getLowercase() {
        return lowercase;
    }

    ItemType(String lowercase) {
        this.lowercase = lowercase;
    }
}
