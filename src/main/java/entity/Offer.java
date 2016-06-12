package entity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Created by owl on 3/27/16.
 */


public class Offer {

    public String id;

    public String state_code;
    public Long state_change_date;

    public String stage;
    public Long stage_change_date;

    public String type_code;
    public String offer_type_code;


    public String locality;
    public String address;
    public String house_num;
    public String ap_num;

    public String district;



    public String house_type;

    public String ap_scheme;         // планировка
    public Integer rooms_count;
    public Integer rooms_offer_count;
    public String room_scheme;

    public Integer floor;
    public Integer floors_count;
    public Integer levels_count;
    public String condition;
    public String balcony;
    public String bathroom;

    public Float square_total;
    public Float square_living;
    public Float square_kitchen;
    public Float square_land;

    public String description;
    public String source_media;
    public String source_url;
    public String source_media_text;

    public Integer creator_id;

    public Long add_date;
    public Long change_date;
    public Long delete_date;
    public Long last_seen_date;
    public Long price_change_date;

    public Float owner_price;
    public Float agency_price;
    public Float lease_deposite;
    public String work_info;


    //public String[] pois;
    //public String poi;


    public String owner_id;
    public String agent_id;
    public Long assign_date;

    public Boolean multylisting;
    public String mls_price_type;
    public Float mls_price;

    public GeoLocation location;
    public String photo_thumbnail;
    //attachments

    public String titleTags;
    public String addressTags;
    public String descriptionTags;

    public String allTags;
    //account_id
    //hidden_for

    //tags

    public Offer() {
        Logger logger = LoggerFactory.getLogger(Offer.class);
    }

    String strNN(String s) {
        if (s != null) return s;
        return "";
    }

    public void GenerateTags() {

        titleTags = "";
        addressTags = "";
        descriptionTags = "";
        allTags = "";

        titleTags = type_code;
        addressTags = strNN(locality) + ' ' + strNN(address) + ' ' + strNN(house_num);
        descriptionTags = strNN(description) + ' ' + strNN(source_media_text);

        allTags = strNN(titleTags) + ' ' + strNN(addressTags) + ' ' + strNN(descriptionTags);
    }

}
