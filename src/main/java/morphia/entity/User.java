package morphia.entity;

import com.mongodb.BasicDBObject;


import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Property;

import com.mongodb.DBObject;

import java.util.Date;

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

    public User(ObjectId id, String name, String password) {
        this.id = id;
        this.name = name;
        this.password = password;
    }
}
