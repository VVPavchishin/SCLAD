package com.pavchishin.sclad;

public class Part {
    private String artiqulPart;
    private String namePart;
    private int quantityPart;
    private float pricePart;

    public Part(String artiqulPart, String namePart, int quantityPart, float pricePart) {
        this.artiqulPart = artiqulPart;
        this.namePart = namePart;
        this.quantityPart = quantityPart;
        this.pricePart = pricePart;
    }

    public String getArtiqulPart() {
        return artiqulPart;
    }

    public void setArtiqulPart(String artiqulPart) {
        this.artiqulPart = artiqulPart;
    }

    public String getNamePart() {
        return namePart;
    }

    public void setNamePart(String namePart) {
        this.namePart = namePart;
    }

    public int getQuantityPart() {
        return quantityPart;
    }

    public void setQuantityPart(int quantityPart) {
        this.quantityPart = quantityPart;
    }

    public float getPricePart() {
        return pricePart;
    }

    public void setPricePart(float pricePart) {
        this.pricePart = pricePart;
    }
}
