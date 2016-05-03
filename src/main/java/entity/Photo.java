package entity;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

/**
 * Created by owl on 4/10/16.
 */

@Entity("photos")
public class Photo {
    @Id
    public ObjectId id;

    public String entityId;
    public String fileName;
    public String thumbFileName;

    private Photo() {

    }

    public Photo(String entityId, String fileName) {
        this.entityId = entityId;
        this.fileName = fileName;
    }
}
