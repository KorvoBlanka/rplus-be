package morphia.entity;

import com.mongodb.BasicDBObject;


import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.Map;

/**
 * Created by owl on 3/25/16.
 */
public class User {
    public String id;
    public String name;
    public String password;
    //private Date create_ts;

    private User() {
        Logger logger = LoggerFactory.getLogger(User.class);
    }

}
