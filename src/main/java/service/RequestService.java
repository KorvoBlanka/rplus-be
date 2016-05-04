package service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import entity.Organisation;
import entity.Person;
import entity.Request;
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
public class RequestService {
    Logger logger = LoggerFactory.getLogger(RequestService.class);

    private final Client elasticClient;
    private final String E_INDEX = "rplus-index";
    private final String E_TYPE = "requests";

    Gson gson = new GsonBuilder().create();

    public RequestService(Client elasticClient) {
        this.elasticClient = elasticClient;
    }

    private Organisation getOrganisationById(String id) {
        GetResponse response = elasticClient.prepareGet(E_INDEX, "organisations", id).get();
        Organisation org = gson.fromJson(response.getSourceAsString(), Organisation.class);
        org.id = response.getId();

        return org;
    }

    public List<Request> list(int page, int perPage, String personId, String searchQuery) {
        logger.info("list");

        List<Request> requestList = new LinkedList<>();

        SearchRequestBuilder req = elasticClient.prepareSearch(E_INDEX)
                .setTypes(E_TYPE)
                .setSearchType(SearchType.DEFAULT)
                .setFrom(page * perPage).setSize(perPage);

        if (personId.length() > 0) {
            req.setQuery(QueryBuilders.matchQuery("person_id", personId));
        }

        if (searchQuery.length() > 0) {
            req.setQuery(QueryBuilders.prefixQuery("_all", searchQuery));
        }

        SearchResponse response = req.execute().actionGet();

        for (SearchHit sh: response.getHits()) {
            Request request = gson.fromJson(sh.getSourceAsString(), Request.class);
            request.id = sh.getId();

            requestList.add(request);
        }

        return requestList;
    }

    public Request get(String id) {
        this.logger.info("get");

        GetResponse response = elasticClient.prepareGet(E_INDEX, E_TYPE, id).get();
        Request request = gson.fromJson(response.getSourceAsString(), Request.class);
        request.id = response.getId();

        return request;
    }

    public Request update(String id, String body) throws Exception {
        this.logger.info("update");

        Request tOrg = gson.fromJson(body, Request.class);
        //t_offer.GenerateTags();

        UpdateRequest updateRequest = new UpdateRequest(E_INDEX, E_TYPE, id).doc(gson.toJson(tOrg));
        UpdateResponse updateResponse = elasticClient.update(updateRequest).get();

        GetResponse response = elasticClient.prepareGet(E_INDEX, E_TYPE, id).get();
        Request request = gson.fromJson(response.getSourceAsString(), Request.class);
        request.id = response.getId();


        return request;
    }

    public Request create(String body) throws Exception {
        this.logger.info("create");

        Request tReq = gson.fromJson(body, Request.class);

        IndexResponse idxResponse = elasticClient.prepareIndex(E_INDEX, E_TYPE).setSource(gson.toJson(tReq)).execute().actionGet();
        GetResponse response = elasticClient.prepareGet(E_INDEX, E_TYPE, idxResponse.getId()).get();
        Request request = gson.fromJson(response.getSourceAsString(), Request.class);
        request.id = response.getId();

        return request;
    }

    public Request delete(String id) {
        throw new NotImplementedException();
    }
}
