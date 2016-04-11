package resource;

import Configuration.AppConfig;
import com.google.gson.Gson;
import morphia.entity.Photo;
import morphia.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import service.PhotoService;
import service.UserService;
import utils.JsonTransformer;

import java.util.List;

import static spark.Spark.post;
import static spark.Spark.get;

/**
 * Created by owl on 4/10/16.
 */
public class PhotoResource {

    Logger logger = LoggerFactory.getLogger(PhotoResource.class);

    private final PhotoService photoService;

    Gson gson = new Gson();

    public PhotoResource(PhotoService photoService) {
        this.photoService = photoService;
        setupEndpoints();
    }

    private void setupEndpoints() {
        get(AppConfig.API_CONTEXT + "/photo/list/:id", "application/json", (request, response) -> {
            List<Photo> result = photoService.list(request.params(":id"));
            for(Photo p: result) {
                p.fileName = "http://localhost:4567/photo_storage/" + p.fileName;
            }
            return result;
        }, new JsonTransformer());
    }
}
