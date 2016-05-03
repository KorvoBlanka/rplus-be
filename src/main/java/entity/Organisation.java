package entity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by owl on 3/27/16.
 */
public class Organisation {
    public String id;
    public String name;
    public String address;
    public String description;

    public Long add_date;
    public Long change_date;

    public Organisation() {
        Logger logger = LoggerFactory.getLogger(Organisation.class);
    }
}
