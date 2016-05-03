package entity;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
