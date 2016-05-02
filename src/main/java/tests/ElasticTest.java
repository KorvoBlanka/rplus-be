package tests;

import org.elasticsearch.action.fieldstats.FieldStats;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;

import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.node.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Date;
import java.util.HashMap;
import java.util.Map;

import static org.elasticsearch.node.NodeBuilder.nodeBuilder;

/**
 * Created by owl on 3/27/16.
 */
public class ElasticTest {
    static Logger logger = LoggerFactory.getLogger(ElasticTest.class);

    public static void test1() {

        Settings settings = Settings.settingsBuilder().put("path.home", "./elastic/").build();

        Node node = nodeBuilder().settings(settings).node();
        Client client = node.client();

        Map<String, Object> json = new HashMap<String, Object>();
        json.put("user","kimchy");
        json.put("message","trying out Elasticsearch");

        client.admin().cluster().prepareHealth().setWaitForYellowStatus().execute().actionGet(5000);

        IndexResponse idx_response = client.prepareIndex("rplus-dict", "city_dict")
                .setSource(json).execute().actionGet();

        logger.info(idx_response.getIndex());

        GetResponse get_response = client.prepareGet("rplus-index", "test", idx_response.getId()).execute().actionGet();

        if (get_response.isExists() == true) {
            logger.info("response exists");

            Map<String, Object> source = get_response.getSourceAsMap();
            for (Map.Entry<String, Object> entry : source.entrySet()) {
                logger.info(entry.getKey() + "/" + entry.getValue());
            }

        } else {
            logger.info("response not exists");
        }



    }
}
