/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uniandes.edu.util;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Rodrigo B
 */
public class DataGrapher {
    
    public static final String BARRAS = "BAR";
    public static final String TORTA = "PIE";
    
    private String graph_type;
    private List<GraphValue> graph_values;

    public DataGrapher(String graph_type) {
        this.graph_type = graph_type;
        graph_values = new ArrayList<>();
    }

    /**
     * Get the value of graph_values
     *
     * @return the value of graph_values
     */
    public List<GraphValue> getGraph_values() {
        return graph_values;
    }

    /**
     * Set the value of graph_values
     *
     * @param graph_values new value of graph_values
     */
    public void setGraph_values(List<GraphValue> graph_values) {
        this.graph_values = graph_values;
    }
    
    /**
     * Add graph_value to graph_values
     *
     * @param graph_value new value of graph_values
     */
    public void addGraph_value(GraphValue graph_value) {
        graph_values.add(graph_value);
    }

    /**
     * Get the value of graph_type
     *
     * @return the value of graph_type
     */
    public String getGraph_type() {
        return graph_type;
    }

    /**
     * Set the value of graph_type
     *
     * @param graph_type new value of graph_type
     */
    public void setGraph_type(String graph_type) {
        this.graph_type = graph_type;
    }

    
}
