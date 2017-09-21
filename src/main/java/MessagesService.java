import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Locale;
import java.util.ResourceBundle;

import static java.time.temporal.ChronoUnit.MINUTES;

/**
 * Created by mac on 02.09.17.
 */
public class MessagesService {

    private String cityName;

    public MessagesService(String cityName) {
        this.cityName = cityName;
    }

    public void cityMessage() throws URISyntaxException {
        URIBuilder builder = new URIBuilder();
        builder.setScheme("http").setHost("maps.googleapis.com").setPath("/maps/api/geocode/json")
                .setParameter("address", cityName)
                .setParameter("sensor", "false");
        String cityInfoJson = httpGetJsonByUrl(builder);

        JSONObject location = new JSONObject(cityInfoJson)
                .getJSONArray("results")
                .getJSONObject(0)
                .getJSONObject("geometry")
                .getJSONObject("location");
        Double latitude = location.getDouble("lat");
        Double longitude = location.getDouble("lng");

        //----------------------------------------------------------------------------------------------

        builder = new URIBuilder();
        builder.setScheme("https").setHost("maps.googleapis.com").setPath("/maps/api/timezone/json")
                .setParameter("location", latitude + "," + longitude)
                .setParameter("timestamp", "0");
        String timeZoneJson = httpGetJsonByUrl(builder);

        String cityTimeZoneId = new JSONObject(timeZoneJson)
                .getString("timeZoneId");

        //----------------------------------------------------------------------------------------------

        LocalTime currentCityTime = LocalTime.now(ZoneId.of(cityTimeZoneId));
        currentCityTime = currentCityTime.truncatedTo(MINUTES);

        System.out.println(currentCityTime.toString());

        double hourMinutes = currentCityTime.getHour() + currentCityTime.getMinute() / 60.0;

        String greeting = "";
        if (hourMinutes >= 6 && hourMinutes <= 9)
            greeting = "morning";
        else if (hourMinutes >= 9 && hourMinutes <= 19)
            greeting = "afternoon";
        else if (hourMinutes >= 19 && hourMinutes <= 23)
            greeting = "evening";
        else
            greeting = "night";

        Locale defaultLocale = Locale.getDefault();
        Locale USLocale = new Locale("en", "US");

        ResourceBundle bundle = ResourceBundle.getBundle("Greetings", USLocale);
        String messageBundle = bundle.getString(greeting);

        System.out.println(messageBundle + ", " + cityName + "!");
    }

    private String httpGetJsonByUrl(URIBuilder builder) throws URISyntaxException {
        HttpClient client = HttpClientBuilder.create().build();
        URI uri = builder.build();
        HttpGet request = new HttpGet(uri);

        StringBuilder result = new StringBuilder();
        try {
            HttpResponse response = client.execute(request);
            BufferedReader rd = new BufferedReader(
                    new InputStreamReader(response.getEntity().getContent()));

            result = new StringBuilder();
            String line = "";
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result.toString();
    }
}
