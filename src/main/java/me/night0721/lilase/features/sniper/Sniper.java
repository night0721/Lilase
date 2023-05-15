package me.night0721.lilase.features.sniper;

public class Sniper {
    private int auctionsSniped = 0;
    private int auctionsPosted = 0;
    private int auctionsFlipped = 0;

    public int getAuctionsSniped() {
        return auctionsSniped;
    }

    public void incrementAuctionsSniped() {
        this.auctionsSniped += 1;
    }

    public int getAuctionsPosted() {
        return auctionsPosted;
    }

    public void incrementAuctionsPosted() {
        this.auctionsPosted += 1;
    }

    public int getAuctionsFlipped() {
        return auctionsFlipped;
    }

    public void incrementAuctionsFlipped() {
        this.auctionsFlipped += 1;
    }
}



