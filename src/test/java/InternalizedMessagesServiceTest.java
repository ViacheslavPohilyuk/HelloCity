import org.junit.Test;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by mac on 07.10.17.
 */
public class InternalizedMessagesServiceTest {

    @Test
    public void checkConnectionToGeocodeApi () {
        String geocodeUrl = "https://maps.googleapis.com/maps/api/geocode/json?address=Kiev&sensor=false";
        String geocodeErrorMessage = "Error creating HTTP connection to the api Geocode!";
        assert(assertHttpConn (geocodeUrl, geocodeErrorMessage));
    }

    @Test
    public void checkConnectionToTimezoneApi() {

        String timezoneUrl = "https://maps.googleapis.com/maps/api/timezone/json?location=0,0&timestamp=0";
        String timezoneErrorMessage = "Error creating HTTP connection to the api Timezone!";
        assertHttpConn (timezoneUrl, timezoneErrorMessage);
    }

    private boolean assertHttpConn(String strUrl, String errorMessage) {
        boolean isOkConn = false;
        try {
            URL url = new URL(strUrl);
            HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
            urlConn.connect();

            isOkConn = HttpURLConnection.HTTP_OK == urlConn.getResponseCode();
        } catch (IOException e) {
            System.err.println(errorMessage);
            e.printStackTrace();
        }
        return isOkConn;
    }
}
