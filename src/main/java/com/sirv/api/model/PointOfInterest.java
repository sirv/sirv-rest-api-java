package com.sirv.api.model;

/**
 * Point of interest on a spin.
 */
public class PointOfInterest {
    private String name;
    private Double x;
    private Double y;
    private Integer frame;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getX() {
        return x;
    }

    public void setX(Double x) {
        this.x = x;
    }

    public Double getY() {
        return y;
    }

    public void setY(Double y) {
        this.y = y;
    }

    public Integer getFrame() {
        return frame;
    }

    public void setFrame(Integer frame) {
        this.frame = frame;
    }

    @Override
    public String toString() {
        return "PointOfInterest{" +
                "name='" + name + '\'' +
                ", x=" + x +
                ", y=" + y +
                ", frame=" + frame +
                '}';
    }
}
