package com.lasat.model.proxy.bean;

public class SamplePoint {
    private final int id;
    private final double ds;
    private final double alpha;
    private final double flux;
    private final double l;
    private final double r;
    private final int kp;
    private final double tr;

    public SamplePoint(int id, double ds, double alpha, double flux, double l, double r, int kp, double tr) {
        this.id = id;
        this.ds = ds;
        this.alpha = alpha;
        this.flux = flux;
        this.l = l;
        this.r = r;
        this.kp = kp;
        this.tr = tr;
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
                '}';
    }
}
