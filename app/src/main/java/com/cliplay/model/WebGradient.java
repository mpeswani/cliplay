package com.cliplay.model;

import java.util.List;

/**
 * Created by Manohar Peswani on 02/07/18.
 * copyright (c) crafty endeavours
 * manoharpeswani@outlook.com
 */
public class WebGradient {
    private String name;
    private List<Colors> colors;
    private int gradientType;
    private String degree;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Colors> getColors() {
        return colors;
    }

    public void setColors(List<Colors> colors) {
        this.colors = colors;
    }

    public int getGradientType() {
        return gradientType;
    }

    public void setGradientType(int gradientType) {
        this.gradientType = gradientType;
    }

    public String getDegree() {
        return degree;
    }

    public void setDegree(String degree) {
        this.degree = degree;
    }

    public static class Colors {
        private String color;
        private String percent;

        public String getColor() {
            return color;
        }

        public void setColor(String color) {
            this.color = color;
        }

        public String getPercent() {
            return percent;
        }

        public void setPercent(String percent) {
            this.percent = percent;
        }
    }

    public static class GradientType {
        public static int TO_TOP = 0;
        public static int DEGREE = 1;
        public static int TO_RIGHT = 2;
    }
}
