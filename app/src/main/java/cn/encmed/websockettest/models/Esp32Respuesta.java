package cn.encmed.websockettest.models;

import java.util.ArrayList;

public class Esp32Respuesta {

    private ArrayList<TupperIO> results;

    public ArrayList<TupperIO> getResults() {
        return results;
    }

    public void setResults(ArrayList<TupperIO> results) {
        this.results = results;
    }
}
