package service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.GeoUtils;

import java.util.*;

/**
 * Created by Aleksandr on 11.01.17.
 */
public class GeoService {
    Logger logger = LoggerFactory.getLogger(OfferService.class);

    // return lat, lon
    public Double[] getLocation(String address) {

        return GeoUtils.getCoordsByAddr(address);
    }

    public List<String> getDistrict(Double lat, Double lon) {

        return GeoUtils.getLocationDistrict(lat, lon);
    }

    public void getPois(Double lat, Double lon) {

    }
}
