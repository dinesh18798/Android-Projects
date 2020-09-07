package com.example.arrows_m.arrow;

public class ArrowObject {

    private String arrowType;
    private int arrowImage;

    private boolean arrowRight = false;
    private boolean highLight = false;

    public ArrowObject(String type, int imageResource) {
        this.arrowType = type;
        this.arrowImage = imageResource;
    }

    public ArrowObject(ArrowObject object) {
        this.arrowType = object.arrowType;
        this.arrowImage = object.getArrowImage();
    }

    public String getArrowType() {
        return arrowType;
    }

    public void setArrowType(String type) {
        this.arrowType = type;
    }

    public int getArrowImage() {
        return arrowImage;
    }

    public void setArrowImage(int image) {
        this.arrowImage = image;
    }

    public boolean isHighLight() {
        return highLight;
    }

    public boolean isArrowRight() {
        return arrowRight;
    }

    public void setArrowRight(boolean arrowRight) {
        this.arrowRight = arrowRight;
    }

    public void setHighLight(boolean highLight) {
        this.highLight = highLight;
    }
}
