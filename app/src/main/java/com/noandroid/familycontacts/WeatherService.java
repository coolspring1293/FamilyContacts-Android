package com.noandroid.familycontacts;

import android.app.IntentService;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.noandroid.familycontacts.model.City;
import com.noandroid.familycontacts.model.CityDao;
import com.noandroid.familycontacts.model.DaoMaster;
import com.noandroid.familycontacts.model.DaoSession;
import com.noandroid.familycontacts.model.DatabaseHelper;
import com.noandroid.familycontacts.model.TelInitialDao;
import com.noandroid.familycontacts.model.Telephone;
import com.noandroid.familycontacts.model.TelephoneDao;

import de.greenrobot.dao.query.QueryBuilder;

/**
 * Created by Hsiaotsefeng on 2016/4/7.
 */
public class WeatherService extends IntentService {
    public static final String ACTION = "ACTION";
    public static final String CITYCODE = "CITYCODE";
    public static final int REFRESH_STATION_LIST = 0;
    public static final int REFRESH_REAL_WEATHER = 1;
    public static final int REFRESH_REAL_WEATHER_LIST = 2;
    private static String stationUrl;
    private static String realtimeWeatherUrl;
    private static String predictWeatherUrl;
    private static ObjectMapper jsonMapper = new ObjectMapper();
    private DaoSession daoSession;

    private final String appname = "Weather Service";
    private TelephoneDao telDao;

    public WeatherService() {
        super("WeatherService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (stationUrl == null) {
            stationUrl = getString(R.string.STATION_URL);
        }
        if (realtimeWeatherUrl == null) {
            realtimeWeatherUrl = getString(R.string.REAL_TIME_WEATHER_URL);
        }
        if (predictWeatherUrl == null) {
            predictWeatherUrl = getString(R.string.PREDICT_WEATHER_URL);
        }
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "contacts-db", null);
        SQLiteDatabase db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
        Log.d(appname, "Weather onCreate");
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        Log.d(appname, "Weather onStart");
    }


    @Override
    protected void onHandleIntent(Intent workIntent) {
        int action = workIntent.getIntExtra(ACTION, -1);
        switch (action) {
            case REFRESH_STATION_LIST:
                Log.d(appname, "REFRESH_STATION_LIST");
                HashMap<String, Hashtable<String, String>> cityList = getCityList();
//                StringBuilder strBuilder = new StringBuilder();
//                for (Map.Entry<String, Hashtable<String, String>> prov : cityList.entrySet()) {
//                    String provStr = prov.getKey();
//                    Hashtable<String, String> city2info = prov.getValue();
//                    for (Map.Entry<String, String> city : city2info.entrySet()) {
//                        strBuilder.append(provStr + "#" + city.getKey() + "#" + city.getValue());
//                        strBuilder.append('\n');
//                    }
//                }
//                Log.d(appname, strBuilder.toString());
                break;
            case REFRESH_REAL_WEATHER:
                Log.d(appname, "REFRESH_REAL_WEATHER");

                break;
            case REFRESH_REAL_WEATHER_LIST:
                telDao = daoSession.getTelephoneDao();
                // test
                // telDao.insert(new Telephone(null, "15626470723", 50L, null));
                List<Telephone> tels = telDao.queryBuilder().list();
                Set<Long> cityIds = new HashSet<>();
                for (Telephone tel : tels) {
                    Long cityId = tel.getTelCityId();
                    cityIds.add(cityId);
                }
                CityDao cityDao = daoSession.getCityDao();
                for (Long id : cityIds) {
                    City city = cityDao.load(id);
                    RealTimeWeather rtw = getRealTimeWeather(city.getWeatherCode());
                    if (rtw != null) {
                        city.setTemperature(String.valueOf(rtw.getTemperature()));
                        city.setWeatherInfo(String.valueOf(rtw.getWeatherInfo()));
                        cityDao.update(city);
                    }
                }

                Log.d(appname, "REFRESH_REAL_WEATHER_LIST");

                break;

        }
    }

    private static HashMap<String, Hashtable<String, String>> getCityList() {
        String jsCities = getUrlContent(stationUrl, 3);
        if (jsCities == null) {
            return null;
        }
        StringBuilder stationsRaw = new StringBuilder(jsCities);
        stationsRaw.delete(0, stationsRaw.indexOf("["));
        stationsRaw.delete(stationsRaw.indexOf(";"), stationsRaw.length());

        String stationsJSON = stationsRaw.toString().replaceAll("\'", "\"");

        JsonNode rootNode;
        try {
            rootNode = new ObjectMapper().readTree(stationsJSON);
        } catch (IOException e) {
            return null;
        }
        HashMap<String, Hashtable<String, String>> cityList = new HashMap<>();
        for (JsonNode provinceNode : rootNode) {
            ProvinceJsonHelper provinceJsonHelper = ProvinceJsonHelper.fromJsonNode(provinceNode);
            Hashtable<String, String> provCities = new Hashtable<>();
            for (CityJsonHelper cityJsonHelper : provinceJsonHelper.getCities()) {
                provCities.put(cityJsonHelper.getName(), cityJsonHelper.getLongCode());
            }
            cityList.put(provinceJsonHelper.getName(), provCities);
        }
        return cityList;
    }

    public static RealTimeWeather getRealTimeWeather(String citycode) {
        String weatherStr = getUrlContent(String.format(realtimeWeatherUrl, citycode), 3);
        if (weatherStr != null) {
//            Log.d("CITY_WEATHER", weatherStr);
            try {
                return jsonMapper.readValue(weatherStr, RealTimeWeather.class);
            } catch (IOException e) {
                return null;
            }
        }
        return null;
    }

    private static String getUrlContent(String URLStr, int trylimits) {
        int tryCount = 0;
        String result = null;
        HttpURLConnection connection = null;
        URL url;
        InputStreamReader unicodeIn = null;
        while (tryCount < trylimits) {
            try {
                url = new URL(URLStr);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:45.0) Gecko/20100101 Firefox/45.0");
                connection.connect();
                unicodeIn = new InputStreamReader(connection.getInputStream(), Charset.forName("UTF-8"));
                BufferedReader bufferedReader = new BufferedReader(unicodeIn);
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line);
                    stringBuilder.append("\n");
                }
                result = stringBuilder.toString();
            } catch (IOException e) {
                tryCount++;
                continue;
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                if (unicodeIn != null) {
                    try {
                        unicodeIn.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return result;
        }
        return null;
    }

}

class ProvinceJsonHelper {
    public static final int PCODEIdx = 0;
    public static final int NAMEIdx = 1;
    public static final int CITIESIdx = 2;
    private final String pCode;
    private final String name;
    private final List<CityJsonHelper> cities;


    public String getCode() {
        return pCode;
    }

    public String getName() {
        return name;
    }

    public List<CityJsonHelper> getCities() {
        return cities;
    }

    public ProvinceJsonHelper(String name, String pCode, List<CityJsonHelper> cities) {
        this.name = name;
        this.pCode = pCode;
        this.cities = cities;
    }

    @Override
    public String toString() {
        return "WeatherFactory.ProvinceJsonHelper{" +
                "pCode=" + pCode +
                ", name='" + name + '\'' +
                ", cities=" + cities +
                '}';
    }

    public static ProvinceJsonHelper fromJsonNode(JsonNode provinceNode) {
        LinkedList<CityJsonHelper> cities = new LinkedList<CityJsonHelper>();
        for (JsonNode city : provinceNode.get(ProvinceJsonHelper.CITIESIdx)) {
            cities.add(CityJsonHelper.fromJsonNode(city));
        }
        return new ProvinceJsonHelper(
                provinceNode.get(ProvinceJsonHelper.NAMEIdx).asText(),
                provinceNode.get(ProvinceJsonHelper.PCODEIdx).asText(),
                cities);
    }
}

class CityJsonHelper {
    private String name;
    private String shortCode;
    private String longCode;
    private String pinyin;
    private static final int LONGCODEIdx = 0;
    private static final int NAMEIdx = 1;
    private static final int SHORTCODEIdx = 2;
    private static final int PINYINIdx = 3;

    public CityJsonHelper(String name, String shortCode, String longCode, String pinyin) {
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

        CityJsonHelper cityJsonHelper = (CityJsonHelper) o;

        if (longCode.equals(cityJsonHelper.longCode)) return false;
        if (shortCode.equals(cityJsonHelper.shortCode)) return false;
        if (name != null ? !name.equals(cityJsonHelper.name) : cityJsonHelper.name != null)
            return false;
        return !(pinyin != null ? !pinyin.equals(cityJsonHelper.pinyin) : cityJsonHelper.pinyin != null);

    }

    @Override
    public String toString() {
        return "WeatherFactory.CityJsonHelper{" +
                "name='" + name + '\'' +
                ", shortCode=" + shortCode +
                ", longCode=" + longCode +
                ", pinyin='" + pinyin + '\'' +
                '}';
    }

    public static CityJsonHelper fromJsonNode(JsonNode jsonNode) {
        return new CityJsonHelper(jsonNode.get(CityJsonHelper.NAMEIdx).asText(),
                jsonNode.get(CityJsonHelper.SHORTCODEIdx).asText(),
                jsonNode.get(CityJsonHelper.LONGCODEIdx).asText(),
                jsonNode.get(CityJsonHelper.PINYINIdx).asText());
    }
}

@JsonIgnoreProperties(ignoreUnknown = true)
class RealTimeWeather {

    private final String cityname;
    private final String date;
    private final double temperature;
    private final double airpressure;
    private final String weatherInfo;

    private final String windDirect;
    private final String windPower;
    private final String windSpeed;

    public RealTimeWeather(String cityname, String date, double temperature, double airpressure,
                           String weatherInfo, String windDirect, String windPower, String windSpeed) {
        this.cityname = cityname;
        this.date = date;
        this.temperature = temperature;
        this.airpressure = airpressure;
        this.weatherInfo = weatherInfo;
        this.windDirect = windDirect;
        this.windPower = windPower;
        this.windSpeed = windSpeed;
    }

    @JsonCreator
    public RealTimeWeather(@JsonProperty("city_name") String cityname,
                           @JsonProperty("data_time") String date,
                           @JsonProperty("weather") Map<String, Object> weather,
                           @JsonProperty("wind") Map<String, Object> wind) {
        this.cityname = cityname.trim();
        this.date = date;
        this.temperature = Double.valueOf((String) weather.get("temperature"));
        this.airpressure = Double.valueOf((String) weather.get("airpressure"));
        this.weatherInfo = (String) weather.get("info");
        this.windDirect = (String) wind.get("direct");
        this.windPower = (String) wind.get("power");
        this.windSpeed = (String) wind.get("speed");
    }

    public String getCityname() {
        return cityname;
    }

    public String getDate() {
        return date;
    }

    public double getTemperature() {
        return temperature;
    }

    public double getAirpressure() {
        return airpressure;
    }

    public String getWeatherInfo() {
        return weatherInfo;
    }

    public String getWindDirect() {
        return windDirect;
    }

    public String getWindPower() {
        return windPower;
    }

    public String getWindSpeed() {
        return windSpeed;
    }

    @Override
    public String toString() {
        return "RealTimeWeather{" +
                "cityname='" + cityname + '\'' +
                ", date='" + date + '\'' +
                ", temperature=" + temperature +
                ", airpressure=" + airpressure +
                ", weatherInfo='" + weatherInfo + '\'' +
                ", windDirect='" + windDirect + '\'' +
                ", windPower='" + windPower + '\'' +
                ", windSpeed='" + windSpeed + '\'' +
                '}';
    }

}