import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * Created by mac on 01.09.17.
 */
public class Main {
    public static void main(String[] args) {
        String cityName;
        String zoneId;

        try {
            // Name of a city from the command line
            cityName = args[0];

            // Id of time zone from command line
            zoneId = args[1];
        }

        // Enter data after opening the program:
        catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Enter the name of the city (e - exit):");
            Scanner scCity = new Scanner(System.in);
            cityName = scCity.next();

            System.out.println("Enter id of the time zone (s - skip | e - exit):");
            Scanner scZoneId = new Scanner(System.in);
            zoneId = scZoneId.next();
        }


    }
}
