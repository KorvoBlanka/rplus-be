package morphia.entity;

import org.mongodb.morphia.annotations.Embedded;

/**
 * Created by owl on 4/1/16.
 */

@Embedded
public class GeoLocation {
    Float lat;
    Float lon;

    public GeoLocation() {
        this.lat = null;
        this.lon = null;
    }
    public GeoLocation(float lat, float lon) {
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
