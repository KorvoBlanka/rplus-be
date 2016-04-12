package service;

import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by owl on 4/12/16.
 */
public class ElasticService {
    static Logger logger = LoggerFactory.getLogger(ElasticService.class);
    Client elasticClient;

    public ElasticService(Client client) {
        this.elasticClient = client;
    }

    public void index(String indexName, Object object) {
        IndexResponse idx_response = elasticClient.prepareIndex("rplus-index", indexName).setSource(object).execute().actionGet();

        logger.info(idx_response.getIndex());
    }
}
