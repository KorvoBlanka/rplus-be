package morphia.entity;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Index;
import org.mongodb.morphia.converters.BooleanConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by owl on 3/27/16.
 */
@Entity("offers")
public class Offer {
    @Id
    public ObjectId id;

    public String state_code;
    public long state_change_date;

    public String stage;
    public long stage_change_date;

    public String type_code;
    public String offer_type_code;

    public String address;
    //house_num ap_num
    public String house_type;

    public String ap_scheme;         // планировка
    public int rooms_count;
    public int rooms_offer_count;
    public String room_scheme;

    public int floor;
    public int floors_count;
    public int levels_count;
    public String condition;
    public String balcony;
    public String bathroom;

    public float square_total;
    public float square_living;
    public float square_kitchen;
    public float square_land;

    public String description;
    public String source_media;
    public String source_url;
    public String source_media_text;

    public int creator_id;

    public long add_date;
    public long change_date;
    public long delete_date;
    public long last_seen_date;
    public long price_change_date;


    public float owner_price;
    public float agency_price;
    public float lease_deposite;
    public String work_info;


    //latitude
    //longitude


    public String landmark;

    public int owner_id;
    public int agent_id;
    public long assign_date;


    public boolean multylisting;
    public String mls_price_type;
    public float mls_price;

    //attachments

    //account_id
    //hidden_for

    //tags

    public Offer() {
        Logger logger = LoggerFactory.getLogger(Offer.class);

        for (Field field : Offer.class.getFields())
        {
            try {
                //field.set(this, null);
            } catch (Exception ex) {
                logger.info(ex.toString());
            }

        }
    }

    public Offer(Map<String, String> values) throws  Exception {
        Logger logger = LoggerFactory.getLogger(Offer.class);
        for (Field field : Offer.class.getFields()) {
            Object val = values.get(field.getName());
            logger.info(field.getType().toString());
            if (val == null) {
                if (field.getType() == float.class || field.getType() == long.class || field.getType() == int.class) {
                    field.set(this, -1);
                } else if (field.getType() == boolean.class) {
                    field.set(this, false);
                } else if (field.getType() == String.class) {
                    field.set(this, "");
                }
            } else {
                field.set(this, val);
            }
        }
    }

}
