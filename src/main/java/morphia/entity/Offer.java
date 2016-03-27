package morphia.entity;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

/**
 * Created by owl on 3/27/16.
 */
@Entity("users")
public class Offer {
    @Id
    public ObjectId id;


}
