package service;

import com.google.gson.Gson;
import entity.Account;
import entity.User;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import entity.Organisation;
import utils.CommonUtils;


public class OrganisationService {

    private final String E_INDEX = "rplus";
    private final String E_TYPE = "organisation";
    private final String E_DATAFIELD = "data";

    Logger logger = LoggerFactory.getLogger(OrganisationService.class);
    private final Client elasticClient;
    Gson gson = new Gson();

    public OrganisationService (Client elasticClient) {

        this.elasticClient = elasticClient;
    }

    public List<String> check (Organisation org) {
        List<String> errors = new LinkedList<>();

        if (org.getName() == null || org.getName().length() < 2) errors.add("name is empty or too short");

        return errors;
    }

    public List<Organisation> list (long accountId, int page, int perPage, String searchQuery) {

        this.logger.info("list");

        List<Organisation> orgList = new ArrayList<>();

        SearchRequestBuilder rb = elasticClient.prepareSearch(E_INDEX)
                .setTypes(E_TYPE)
                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                .setFrom(page * perPage).setSize(perPage);

        BoolQueryBuilder q = QueryBuilders.boolQuery();

        // set query
        q.must(QueryBuilders.matchQuery("accountId", accountId));

        if (searchQuery != null && searchQuery.length() > 0) {

            q.should(QueryBuilders.matchQuery("name", searchQuery));
            //
        }

        rb.setQuery(q);

        // execute

        SearchResponse response = rb.execute().actionGet();

        for (SearchHit sh: response.getHits()) {
            String dataJson = sh.getSourceAsMap().get(E_DATAFIELD).toString();
            orgList.add(gson.fromJson(dataJson, Organisation.class));
        }

        return orgList;
    }

    public Organisation get (long id) {

        this.logger.info("get");

        Organisation result = null;

        GetResponse response = this.elasticClient.prepareGet(E_INDEX, E_TYPE, Long.toString(id)).get();

        String dataJson = response.getSourceAsMap().get(E_DATAFIELD).toString();
        result = gson.fromJson(dataJson, Organisation.class);

        return result;
    }

    public Organisation save (Organisation organisation) throws Exception {

        this.logger.info("save");

        indexAccount(organisation);

        return organisation;
    }

    public Organisation delete (long id) {
        return null;
    }


    public void indexAccount(Organisation organisation) {

        Map<String, Object> json = new HashMap<String, Object>();

        if (organisation.getId() == null) {
            organisation.setId(CommonUtils.getSystemTimestamp());
        }

        json.put("id", organisation.getId());
        json.put("accountId", organisation.getAccountId());

        json.put("name", organisation.getName());
        //

        json.put(E_DATAFIELD, gson.toJson(organisation));

        IndexResponse response = this.elasticClient.prepareIndex(E_INDEX, E_TYPE, Long.toString(organisation.getId())).setSource(json).get();
    }
}
