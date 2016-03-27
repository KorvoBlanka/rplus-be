package service; /**
 * Created by owl on 3/23/16.
 */
import com.mongodb.WriteResult;
import morphia.entity.User;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.UpdateResults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.CommonUtils;

import java.util.*;


public class UserService {
    Logger logger = LoggerFactory.getLogger(UserService.class);

    private final Datastore ds;

    public UserService(Datastore ds) {
        this.ds = ds;
    }

    public List<User> list(String filter) {
        List<User> result = new LinkedList<User>();

        for (User u : ds.find(User.class)) {
            result.add(u);
        }

        return result;
    }

    public User get(String id) {
        this.logger.info("get");

        User result = ds.get(User.class, new ObjectId(id));
        this.logger.info(result.name);
        return result;
    }

    public User getByName(String name) {
        this.logger.info("get by name");
        this.logger.info(name);

        User result = ds.find(User.class).field("name").equal(name).get();
        return result;
    }

    public User update(String id, String body) {
        this.logger.info("update");

        Map<String, String> map = CommonUtils.JsonToMap(body);

        User user = ds.get(User.class, new ObjectId(id));
        UpdateResults result = ds.update(user, ds.createUpdateOperations(User.class).set("name", map.get("name")));
        user = ds.get(User.class, new ObjectId(id));
        return user;
    }

    public User create(String body) {
        this.logger.info("create");

        Map<String, String> map = CommonUtils.JsonToMap(body);

        User user = new User(null, map.get("name"), map.get("password"));

        ObjectId id = (ObjectId)ds.save(user).getId();
        User result = ds.get(User.class, id);
        return result;
    }

    public User delete(String id) {
        User user = ds.get(User.class, new ObjectId(id));
        WriteResult wr = ds.delete(User.class, new ObjectId(id));
        return user;
    }
}
