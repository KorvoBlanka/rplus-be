package hibernate.entity;

import java.io.Serializable;

/**
 * Created by Aleksandr on 13.01.17.
 */
public class GeoPoint implements Serializable {

    Double lat;
    Double lon;

    public GeoPoint() {

        this.lat = null;
        this.lon = null;

    }

    public GeoPoint(double lat, double lon) {

        this.lat = lat;
        this.lon = lon;

    }

    @Override
    public String toString() {

        return "{" +
                "\"lat\":" + lat +
                ", \"lon\":" + lon +
                '}';

    }
}
