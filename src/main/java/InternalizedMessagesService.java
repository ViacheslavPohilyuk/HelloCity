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
public class InternalizedMessagesService {

    private String cityName;

    public InternalizedMessagesService(String cityName) {
        this.cityName = cityName;
    }

    /**
     * Appointment of this method is displaying a greeting message
     * depending on the time of day in a city that user inputs.
     *
     * Time of day periods:
     * Morning   6:00 - 9:00
     * Afternoon 9:00 - 19:00
     * Evening   19:00 - 23:00
     * Night     23:00 - 6:00
     */
    public void cityMessage() throws URISyntaxException {

        /* Building the URI for Google api Geocode and
         * send a Get request with this URI to recognize the
         * location (latitude/longitude) of a city by its name */
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

        /* Building the URI for Google api Timezone and
         * send a Get request with this URI to recognize the
         * time zone of a city by its location */
        builder = new URIBuilder();
        builder.setScheme("https").setHost("maps.googleapis.com").setPath("/maps/api/timezone/json")
                .setParameter("location", latitude + "," + longitude)
                .setParameter("timestamp", "0");
        String timeZoneJson = httpGetJsonByUrl(builder);

        String cityTimeZoneId = new JSONObject(timeZoneJson)
                .getString("timeZoneId");

        /* Now we find out the current time in the above city by its time zone.
         * And get time of the city in hours and minutes */
        LocalTime currentCityTime = LocalTime.now(ZoneId.of(cityTimeZoneId));
        currentCityTime = currentCityTime.truncatedTo(MINUTES);

        System.out.println(currentCityTime.toString());

        double hourMinutes = currentCityTime.getHour() + currentCityTime.getMinute() / 60.0;

        /* At this moment we know the current time in the city that's why
         * we can choose appropriate greeting message */
        String greeting = "";
        if (hourMinutes >= 6 && hourMinutes <= 9)
            greeting = "morning";
        else if (hourMinutes >= 9 && hourMinutes <= 19)
            greeting = "afternoon";
        else if (hourMinutes >= 19 && hourMinutes <= 23)
            greeting = "evening";
        else
            greeting = "night";

        /* Now we check a locale of the user and we can
         * get a right message from the resource bundle */
        Locale defaultLocale = Locale.getDefault();
        Locale USLocale = new Locale("en", "US");

        ResourceBundle bundle = ResourceBundle.getBundle("Greetings", USLocale);
        String messageBundle = bundle.getString(greeting);

        System.out.println(messageBundle + ", " + cityName + "!");
    }

    /**
     * This method need to send GET http requests using library Apache HttpClient
     *
     * @param builder - this class represents the URI with separate parts like: scheme, host, path, parameters, etc.
     * @return body of a response
     */
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
