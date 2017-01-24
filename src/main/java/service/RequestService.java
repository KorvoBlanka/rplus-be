package service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import hibernate.entity.Request;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.geo.GeoPoint;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.CommonUtils;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.*;


public class RequestService {

    Logger logger = LoggerFactory.getLogger(RequestService.class);

    EntityManagerFactory emf;
    private final Client elasticClient;

    private final String E_INDEX = "rplus-index";
    private final String E_TYPE = "requests";

    Gson gson = new GsonBuilder().create();


    public RequestService (EntityManagerFactory emf, Client elasticClient) {

        this.emf = emf;
        this.elasticClient = elasticClient;
    }


    public List<Request> list (Long accountId, int page, int perPage, Map<String, String> filter, String searchQuery) {

        logger.info("list");

        List<Request> requestList = new ArrayList<>();

        EntityManager em = emf.createEntityManager();

        SearchRequestBuilder rb = elasticClient.prepareSearch("rplus")
                .setTypes("request")
                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                .setFrom(page * perPage).setSize(perPage);


        BoolQueryBuilder q = QueryBuilders.boolQuery();


        q.must(QueryBuilders.termQuery("accountId", accountId));

        filter.forEach((k,v) -> {
            logger.info(k + " - " + v);
            if (v != null && !v.equals("all")) {
                if (k.equals("changeDate")) {
                    long date = Long.parseLong(v);

                    // 86400 sec in 1 day
                    long ts = CommonUtils.getUnixTimestamp() - date * 86400;
                    q.must(QueryBuilders.rangeQuery(k).gte(ts));
                } else {
                    q.must(QueryBuilders.termQuery(k, v));
                }
            }
        });


        if (searchQuery != null && searchQuery.length() > 0) {
            //q.must(QueryBuilders.matchPhraseQuery("allTags", searchQuery).slop(50));
            q.must(QueryBuilders.matchQuery("request", searchQuery));
        }

        rb.setQuery(q);

        SearchResponse response = rb.execute().actionGet();


        for (SearchHit sh: response.getHits()) {
            Request request = em.find(hibernate.entity.Request.class, Long.parseLong(sh.getId()));
            requestList.add(request);
        }

        return requestList;
    }

    public Request get (long id) {

        this.logger.info("get");

        Request res = null;

        return res;
    }

    public Request save (Request request) throws Exception {

        this.logger.info("save");

        EntityManager em = emf.createEntityManager();

        Request result;

        em.getTransaction().begin();
        result = em.merge(request);
        em.getTransaction().commit();

        indexRequest(result);

        return result;
    }

    public void indexRequest(Request request) {

        Map<String, Object> json = new HashMap<>();
        json.put("id", request.getId());
        json.put("request", request.request);

        json.put("accountId", request.getAccountId());
        json.put("offerTypeCode", request.getOfferTypeCode());
        json.put("agentId", request.getAgentId());
        json.put("personId", request.getPersonId());

        IndexResponse response = this.elasticClient.prepareIndex("rplus", "request", Long.toString(request.getId())).setSource(json).get();
    }

    public Request delete (String id) {
    }
}
