/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uniandes.edu.util;

/**
 *
 * @author Rodrigo B
 */
public class GraphValue {

    private String x_label;

    private String y_label;

    private float value;

    public GraphValue(String x_label, String y_label, float value) {
        this.x_label = x_label;
        this.y_label = y_label;
        this.value = value;
    }

    /**
     * Get the value of value
     *
     * @return the value of value
     */
    public float getValue() {
        return value;
    }

    /**
     * Set the value of value
     *
     * @param value new value of value
     */
    public void setValue(float value) {
        this.value = value;
    }

    /**
     * Get the value of y_label
     *
     * @return the value of y_label
     */
    public String getY_label() {
        return y_label;
    }

    /**
     * Set the value of y_label
     *
     * @param y_label new value of y_label
     */
    public void setY_label(String y_label) {
        this.y_label = y_label;
    }

    /**
     * Get the value of x_label
     *
     * @return the value of x_label
     */
    public String getX_label() {
        return x_label;
    }

    /**
     * Set the value of x_label
     *
     * @param x_label new value of x_label
     */
    public void setX_label(String x_label) {
        this.x_label = x_label;
    }

}
