package service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mongodb.WriteResult;
import morphia.entity.GeoLocation;
import morphia.entity.Offer;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.UpdateResults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.CommonUtils;
import utils.ObjectIdTypeAdapter;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by owl on 3/27/16.
 */
public class OfferService {
    Logger logger = LoggerFactory.getLogger(OfferService.class);

    private final Datastore ds;
    Gson gson = new GsonBuilder().registerTypeAdapter(ObjectId.class, new ObjectIdTypeAdapter()).create();


    public OfferService(Datastore ds) {
        this.ds = ds;
    }

    public List<Offer> list(int page, int perPage, String filter) {
        List<Offer> result = new LinkedList<Offer>();

        for (Offer o : ds.find(Offer.class).limit(perPage).offset(page * perPage)) {
            result.add(o);
        }

        return result;
    }

    public Offer get(String id) {
        this.logger.info("get");

        Offer result = ds.get(Offer.class, new ObjectId(id));
        return result;
    }

    public Offer update(String id, String body) throws Exception {
        this.logger.info("update");

        this.logger.info(body);

        Offer t_offer = gson.fromJson(body, Offer.class);

        Offer offer = ds.get(Offer.class, new ObjectId(id));

        for (Field field : Offer.class.getFields()) {

            String f_name = field.getName();
            Object old_value = field.get(offer);
            Object new_value = field.get(t_offer);
            logger.info(f_name + ": " + old_value + " -> "  + new_value);
            if (f_name == "id") continue;
            if (old_value == new_value) continue;
            if (new_value == null) {
                UpdateResults result =
                        ds.update(offer, ds.createUpdateOperations(Offer.class).unset(f_name));
            } else {
                UpdateResults result =
                        ds.update(offer, ds.createUpdateOperations(Offer.class).set(f_name, new_value));
            }
        }

        offer = ds.get(Offer.class, new ObjectId(id));
        return offer;
    }

    public Offer create(String body) throws Exception {
        this.logger.info("create");

        Offer tOffer = gson.fromJson(body, Offer.class);

        ObjectId id = (ObjectId)ds.save(tOffer).getId();
        Offer result = ds.get(Offer.class, id);
        return result;
    }

    public Offer delete(String id) {
        Offer offer = ds.get(Offer.class, new ObjectId(id));
        WriteResult wr = ds.delete(Offer.class, new ObjectId(id));
        return offer;
    }
}
