package com.noandroid.familycontacts;

import com.fasterxml.jackson.databind.JsonNode;

class City {
    private String name;
    private String shortCode;
    private String longCode;
    private String pinyin;
    private static final int LONGCODEIdx = 0;
    private static final int NAMEIdx = 1;
    private static final int SHORTCODEIdx = 2;
    private static final int PINYINIdx = 3;

    public City(String name, String shortCode, String longCode, String pinyin) {
        this.name = name;
        this.shortCode = shortCode;
        this.longCode = longCode;
        this.pinyin = pinyin;
    }

    public String getName() {
        return name;
    }

    public String getLongCode() {
        return longCode;
    }

    public String getShortCode() {
        return shortCode;
    }

    public String getPinyin() {
        return pinyin;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        City city = (City) o;

        if (longCode.equals(city.longCode)) return false;
        if (shortCode.equals(city.shortCode)) return false;
        if (name != null ? !name.equals(city.name) : city.name != null) return false;
        return !(pinyin != null ? !pinyin.equals(city.pinyin) : city.pinyin != null);

    }

    @Override
    public String toString() {
        return "WeatherFactory.City{" +
                "name='" + name + '\'' +
                ", shortCode=" + shortCode +
                ", longCode=" + longCode +
                ", pinyin='" + pinyin + '\'' +
                '}';
    }

    public static City fromJsonNode(JsonNode jsonNode) {
        return new City(jsonNode.get(City.NAMEIdx).asText(),
                jsonNode.get(City.SHORTCODEIdx).asText(),
                jsonNode.get(City.LONGCODEIdx).asText(),
                jsonNode.get(City.PINYINIdx).asText());
    }
}
