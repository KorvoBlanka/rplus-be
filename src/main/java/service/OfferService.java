package service;

import com.mongodb.WriteResult;
import morphia.entity.Offer;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.UpdateResults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.CommonUtils;

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

    public OfferService(Datastore ds) {
        this.ds = ds;
    }

    public List<Offer> list(String filter) {
        List<Offer> result = new LinkedList<Offer>();

        for (Offer o : ds.find(Offer.class)) {
            result.add(o);
        }

        return result;
    }

    public Offer get(String id) {
        this.logger.info("get");

        Offer result = ds.get(Offer.class, new ObjectId(id));
        return result;
    }

    public Offer update(String id, String body) {
        this.logger.info("update");

        this.logger.info(body);

        Map<String, String> values = CommonUtils.JsonToMap(body);

        this.logger.info(values.toString());

        Offer offer = ds.get(Offer.class, new ObjectId(id));

        for (Field field : Offer.class.getFields()) {
            String f_name = field.getName();
            String value = values.get(f_name);
            logger.info(f_name + " - " + value);
            if (f_name == "id") continue;;
            if (value == null) {
                if (field.getType() == String.class) {
                    value = "";
                } else
                if (field.getType() == float.class || field.getType() == long.class || field.getType() == int.class) {
                    value = "-1";
                } else {
                    value = "false";
                }
            }
            UpdateResults result =
                    ds.update(offer, ds.createUpdateOperations(Offer.class).set(f_name, value));
        }

        offer = ds.get(Offer.class, new ObjectId(id));
        return offer;
    }

    public Offer create(String body) throws Exception {
        this.logger.info("create");

        Map<String, String> map = CommonUtils.JsonToMap(body);

        Offer offer = new Offer(map);

        ObjectId id = (ObjectId)ds.save(offer).getId();
        Offer result = ds.get(Offer.class, id);
        return result;
    }

    public Offer delete(String id) {
        Offer offer = ds.get(Offer.class, new ObjectId(id));
        WriteResult wr = ds.delete(Offer.class, new ObjectId(id));
        return offer;
    }
}
