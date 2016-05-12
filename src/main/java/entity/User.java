package entity;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by owl on 3/25/16.
 */
public class User {
    public String id;
    public String manager_id;
    public String name;
    public String role;
    public String password;
    //private Date create_ts;

    public Long add_date;
    public Long change_date;

    private User() {
        Logger logger = LoggerFactory.getLogger(User.class);
    }

}
