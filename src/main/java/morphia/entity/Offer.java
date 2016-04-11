package morphia.entity;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;


/**
 * Created by owl on 3/27/16.
 */


@Entity("offers")
public class Offer {
    @Id
    public ObjectId id;

    public String state_code;
    public Long state_change_date;

    public String stage;
    public Long stage_change_date;

    public String type_code;
    public String offer_type_code;

    public String address;
    //house_num ap_num
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


    //latitude
    //longitude


    public String landmark;

    public Integer owner_id;
    public Integer agent_id;
    public Long assign_date;

    public Boolean multylisting;
    public String mls_price_type;
    public Float mls_price;

    public GeoLocation location;
    public String photo_thumbnail;
    //attachments

    //account_id
    //hidden_for

    //tags

    public Offer() {
        Logger logger = LoggerFactory.getLogger(Offer.class);

        for (Field field : Offer.class.getFields())
        {
            try{
            //field.set(this, null);
            } catch (Exception ex) {
                logger.info(ex.toString());
            }

        }
    }

}
