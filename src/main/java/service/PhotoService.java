package service;

import entity.Offer;
import entity.Photo;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.CommonUtils;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by owl on 4/10/16.
 */
public class PhotoService {

    Logger logger = LoggerFactory.getLogger(PhotoService.class);

    private final Datastore ds;

    public PhotoService(Datastore ds) {
        this.ds = ds;
    }

    public void put(String id, String photoUrl) throws Exception {
        String photoName = CommonUtils.downloadPhoto(photoUrl);

        Offer offer = ds.get(Offer.class, new ObjectId(id));
        ds.save(new Photo(id, photoName));
        ds.update(offer, ds.createUpdateOperations(Offer.class).set("photo_thumbnail", photoName));
    }

    public List<Photo> list(String id) {
        List<Photo> result = new LinkedList<Photo>();

        for (Photo p : ds.find(Photo.class, "entityId = ", id)) {
            result.add(p);
        }

        return result;
    }
}
