package morphia.entity;

import com.mongodb.BasicDBObject;


import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Property;

import com.mongodb.DBObject;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.Map;

/**
 * Created by owl on 3/25/16.
 */
@Entity("users")
public class User {
    @Id
    public ObjectId id;
    public String name;
    public String password;
    //private Date create_ts;

    private User() {
        this.id = null;
        this.name = null;
        this.password = null;
    }

    public User(Map<String, String> values) throws Exception {
        for (Field field : Offer.class.getFields()) {
            Object val = values.get(field.getName());
            if (field.getType() != String.class && field.getType() != ObjectId.class && val == null) {
                field.set(this, -1);
            } else {
                field.set(this, val);
            }
        }
    }
}
