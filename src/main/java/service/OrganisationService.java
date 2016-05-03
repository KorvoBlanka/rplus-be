package service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import entity.Organisation;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by owl on 5/3/16.
 */
public class OrganisationService {
    Logger logger = LoggerFactory.getLogger(OrganisationService.class);

    private final Client elasticClient;
    private final String E_INDEX = "rplus-index";
    private final String E_TYPE = "organisations";

    Gson gson = new GsonBuilder().create();

    public OrganisationService(Client elasticClient) {
        this.elasticClient = elasticClient;
    }

    public List<Organisation> list(int page, int perPage, String searchQuery) {
        this.logger.info("list");

        List<Organisation> orgList = new LinkedList<>();

        SearchRequestBuilder req = elasticClient.prepareSearch(E_INDEX)
                .setTypes(E_TYPE)
                .setSearchType(SearchType.DEFAULT)
                .setFrom(page * perPage).setSize(perPage);

        if (searchQuery.length() > 0) {
            req.setQuery(QueryBuilders.prefixQuery("_all", searchQuery));
        }

        SearchResponse response = req.execute().actionGet();

        for (SearchHit sh: response.getHits()) {
            Organisation org = gson.fromJson(sh.getSourceAsString(), Organisation.class);
            org.id = sh.getId();
            orgList.add(org);
        }

        return orgList;
    }

    public Organisation get(String id) {
        this.logger.info("get");

        GetResponse response = elasticClient.prepareGet(E_INDEX, E_TYPE, id).get();
        Organisation org = gson.fromJson(response.getSourceAsString(), Organisation.class);
        org.id = response.getId();

        return org;
    }

    public Organisation update(String id, String body) throws Exception {
        this.logger.info("update");

        Organisation tOrg = gson.fromJson(body, Organisation.class);

        UpdateRequest updateRequest = new UpdateRequest(E_INDEX, E_TYPE, id).doc(gson.toJson(tOrg));
        UpdateResponse updateResponse = elasticClient.update(updateRequest).get();

        GetResponse response = elasticClient.prepareGet(E_INDEX, E_TYPE, id).get();
        Organisation org = gson.fromJson(response.getSourceAsString(), Organisation.class);
        org.id = response.getId();

        return org;
    }

    public Organisation create(String body) throws Exception {
        this.logger.info("create");

        Organisation tOrg = gson.fromJson(body, Organisation.class);

        IndexResponse idxResponse = elasticClient.prepareIndex(E_INDEX, E_TYPE).setSource(gson.toJson(tOrg)).execute().actionGet();
        GetResponse response = elasticClient.prepareGet(E_INDEX, E_TYPE, idxResponse.getId()).get();
        Organisation org = gson.fromJson(response.getSourceAsString(), Organisation.class);
        org.id = response.getId();

        return org;
    }

    public Organisation delete(String id) {
        throw new NotImplementedException();
    }
}
