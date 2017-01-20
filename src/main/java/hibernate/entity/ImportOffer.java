package hibernate.entity;
/**
 * Created by Aleksandr on 09.11.16.
 */

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

import java.util.Map;

import static utils.CommonUtils.getUnixTimestamp;


public class ImportOffer {

    public Long id;

    public String type_code;
    public String offer_type_code;

    public String locality;
    public String address;
    public String house_num;
    public String ap_num;


    public String district;
    public String poi;

    public Integer house_type_id;
    public Integer ap_scheme_id;
    public Integer room_scheme_id;
    public Integer condition_id;
    public Integer balcony_id;
    public Integer bathroom_id;

    public Integer rooms_count;
    public Integer rooms_offer_count;

    public Integer floor;
    public Integer floors_count;
    public Integer levels_count;

    public Float square_total;
    public Float square_living;
    public Float square_kitchen;
    public Float square_land;

    public Float owner_price;

    public String source_media;
    public String source_url;
    public String source_media_text;

    public String add_date;
    public String last_seen_date;

    public Double location_lat;
    public Double location_lon;

    public String photo_url[];

    public String owner_phones[];
    public String owner_name;
    public String mediator_company;

}
