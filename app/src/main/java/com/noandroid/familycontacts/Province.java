package com.noandroid.familycontacts;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Hsiaotsefeng on 2016/4/11.
 */
public class Province {
    public static final int PCODEIdx = 0;
    public static final int NAMEIdx = 1;
    public static final int CITIESIdx = 2;
    private final String pCode;
    private final String name;
    private final List<City> cities;


    public String getCode() {
        return pCode;
    }

    public String getName() {
        return name;
    }

    public List<City> getCities() {
        return cities;
    }

    public Province(String name, String pCode, List<City> cities) {
        this.name = name;
        this.pCode = pCode;
        this.cities = cities;
    }

    @Override
    public String toString() {
        return "WeatherFactory.Province{" +
                "pCode=" + pCode +
                ", name='" + name + '\'' +
                ", cities=" + cities +
                '}';
    }

    public Province fromJsonNode(JsonNode provinceNode) {
        LinkedList<City> cities = new LinkedList<City>();
        for (JsonNode city : provinceNode.get(Province.CITIESIdx)) {
            cities.add(City.fromJsonNode(city));
        }
        return new Province(
                provinceNode.get(Province.NAMEIdx).asText(),
                provinceNode.get(Province.PCODEIdx).asText(),
                cities);
    }
}
