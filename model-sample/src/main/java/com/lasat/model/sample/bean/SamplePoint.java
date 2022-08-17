package com.lasat.model.sample.bean;

public class SamplePoint {
    private int id;
    private double ds;
    private double alpha;
    private double flux;
    private double l;
    private double r;
    private int kp;
    private double tr;
    private double cost;

    public SamplePoint(int id, double ds, double alpha, double flux, double l, double r, int kp, double tr, double powerCost) {
        this.id = id;
        this.ds = ds;
        this.alpha = alpha;
        this.flux = flux;
        this.l = l;
        this.r = r;
        this.kp = kp;
        this.tr = tr;
        this.cost = powerCost;
    }

    public int getId() {
        return id;
    }

    public double getDs() {
        return ds;
    }

    public double getAlpha() {
        return alpha;
    }

    public double getFlux() {
        return flux;
    }

    public double getL() {
        return l;
    }

    public double getR() {
        return r;
    }

    public int getKp() {
        return kp;
    }

    public double getTr() {
        return tr;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setDs(double ds) {
        this.ds = ds;
    }

    public void setAlpha(double alpha) {
        this.alpha = alpha;
    }

    public void setFlux(double flux) {
        this.flux = flux;
    }

    public void setL(double l) {
        this.l = l;
    }

    public void setR(double r) {
        this.r = r;
    }

    public void setKp(int kp) {
        this.kp = kp;
    }

    public void setTr(double tr) {
        this.tr = tr;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double powerCost) {
        this.cost = powerCost;
    }

    @Override
    public String toString() {
        return "SamplePoint{" +
                "id=" + id +
                ", ds=" + ds +
                ", alpha=" + alpha +
                ", flux=" + flux +
                ", l=" + l +
                ", r=" + r +
                ", kp=" + kp +
                ", tr=" + tr +
                ", powerCost=" + cost +
                '}';
    }
}
