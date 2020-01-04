package com.pavchishin.sclad;

public class PartItem {

String artPart;
String namePart;
String locationPart;
int dockQuantityPart;
int realQuantityPart;

    public PartItem(String artPart, String namePart, String locationPart, int dockQuantityPart, int realQuantityPart) {
        this.artPart = artPart;
        this.namePart = namePart;
        this.locationPart = locationPart;
        this.dockQuantityPart = dockQuantityPart;
        this.realQuantityPart = realQuantityPart;
    }

    public String getArtPart() {
        return artPart;
    }

    public void setArtPart(String artPart) {
        this.artPart = artPart;
    }

    public String getNamePart() {
        return namePart;
    }

    public void setNamePart(String namePart) {
        this.namePart = namePart;
    }

    public String getLocationPart() {
        return locationPart;
    }

    public void setLocationPart(String locationPart) {
        this.locationPart = locationPart;
    }

    public int getDockQuantityPart() {
        return dockQuantityPart;
    }

    public void setDockQuantityPart(int dockQuantityPart) {
        this.dockQuantityPart = dockQuantityPart;
    }

    public int getRealQuantityPart() {
        return realQuantityPart;
    }

    public void setRealQuantityPart(int realQuantityPart) {
        this.realQuantityPart = realQuantityPart;
    }
}
