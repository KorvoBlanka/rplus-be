package tests;

import com.mongodb.MongoClient;
import morphia.entity.User;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Key;
import org.mongodb.morphia.Morphia;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by owl on 3/25/16.
 */
public class MorphiaTest {
    public static void test() throws Exception {
        Logger logger = LoggerFactory.getLogger(MorphiaTest.class);

        MongoClient mongoClient = new MongoClient("localhost");

        Morphia morphia = new Morphia();
        morphia.map(User.class);

        Datastore ds = morphia.createDatastore(mongoClient, "rplus-dev");

        User user = new User(null, "some_name", "some_password");
        Key<User> result = ds.save(user);

        logger.info(result.toString());
    }
}
