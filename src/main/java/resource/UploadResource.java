package resource;

import com.google.gson.Gson;
import configuration.AppConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import service.UploadService;

import static spark.Spark.post;

/**
 * Created by owl on 2/20/17.
 */
public class UploadResource {
    Logger logger = LoggerFactory.getLogger(UploadResource.class);
    Gson gson = new Gson();

    private final UploadService uploadService;


    public UploadResource(UploadService uploadService) {
        this.uploadService = uploadService;
        setupEndpoints();
    }

    private void setupEndpoints() {

        post(AppConfig.API_CONTEXT + "/upload/photo", "application/json", (request, response) -> {

            return null;
        }, gson::toJson);
    }
}
