package service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import entity.Request;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.geo.GeoPoint;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.CommonUtils;
import utils.FilterObject;
import utils.ParseResult;
import utils.Query;

import java.util.*;


public class RequestService {

    private final String E_INDEX = "rplus";
    private final String E_TYPE = "request";
    private final String E_DATAFIELD = "data";

    Logger logger = LoggerFactory.getLogger(RequestService.class);
    private final Client elasticClient;
    Gson gson = new GsonBuilder().create();
    //Gson gson = new Gson();


    public RequestService (Client elasticClient) {

        this.elasticClient = elasticClient;
    }


    public List<Request> list (long accountId, int page, int perPage, Map<String, String> filter, String searchQuery) {

        logger.info("list");

        List<Request> requestList = new ArrayList<>();


        SearchRequestBuilder rb = elasticClient.prepareSearch(E_INDEX)
                .setTypes(E_TYPE)
                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                .setFrom(page * perPage).setSize(perPage);


        BoolQueryBuilder q = QueryBuilders.boolQuery();


        q.must(QueryBuilders.termQuery("accountId", accountId));

        if (filter != null) {
            filter.forEach((k, v) -> {
                if (k.equals("stageCode")) {
                    if (v != null && !v.equals("all")) {
                        q.must(QueryBuilders.termQuery(k, v));
                    } else {
                        q.mustNot(QueryBuilders.termQuery(k, "archive"));
                    }
                } else {
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
                }
            });
        }

        if (searchQuery != null && searchQuery.length() > 2) {
            //q.must(QueryBuilders.matchPhraseQuery("allTags", searchQuery).slop(50));
            q.must(QueryBuilders.matchQuery("request", searchQuery));
        }

        rb.addSort(SortBuilders.fieldSort("id").order(SortOrder.DESC));

        rb.setQuery(q);

        SearchResponse response = rb.execute().actionGet();


        for (SearchHit sh: response.getHits()) {
            String dataJson = sh.getSourceAsMap().get(E_DATAFIELD).toString();
            requestList.add(gson.fromJson(dataJson, Request.class));
        }

        return requestList;
    }

    public float checkOffer (Long accountId, Long offerId, String offerTypeCode, String searchQuery, List<GeoPoint> geoSearchPolygon) {

        this.logger.info("check offer");

        Map<String, String> queryParts = Query.process(searchQuery);
        String request = queryParts.get("req");
        String excl = queryParts.get("excl");
        String near = queryParts.get("near");
        ParseResult pr = Query.parse(request);
        List<FilterObject> rangeFilters = pr.filterList;

        SearchRequestBuilder rb = elasticClient.prepareSearch(E_INDEX)
                .setTypes("offer")
                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                .setFrom(0).setSize(10);


        BoolQueryBuilder q = QueryBuilders.boolQuery();

        q.must(QueryBuilders.termQuery("accountId", accountId));
        q.must(QueryBuilders.termQuery("id", offerId));
        q.must(QueryBuilders.termQuery("offerTypeCode", offerTypeCode));

        if (geoSearchPolygon.size() > 0) {
            q.filter(QueryBuilders.geoPolygonQuery("location", geoSearchPolygon));
        }

        if (excl != null && excl.length() > 0) {
            q.mustNot(QueryBuilders.matchQuery("title", excl));
            q.mustNot(QueryBuilders.matchQuery("address_ext", excl));
            q.mustNot(QueryBuilders.matchQuery("spec", excl));
            q.mustNot(QueryBuilders.matchQuery("description", excl));
        }

        rangeFilters.forEach(fltr -> {
            if (fltr.exactVal != null) {
                q.must(QueryBuilders.termQuery(fltr.fieldName, fltr.exactVal));
            } else {
                if (fltr.lowerVal != null) {
                    q.must(QueryBuilders.rangeQuery(fltr.fieldName).gte(fltr.lowerVal));
                }

                if (fltr.upperVal != null) {
                    q.must(QueryBuilders.rangeQuery(fltr.fieldName).lte(fltr.upperVal));
                }
            }
        });

        if (pr.query != null && pr.query.length() > 0) {
            q.should(QueryBuilders.matchQuery("title", pr.query).boost(8));
            q.should(QueryBuilders.matchQuery("address_ext", pr.query).boost(4));
            q.should(QueryBuilders.matchQuery("spec", pr.query).boost(2));
            q.should(QueryBuilders.matchQuery("description", pr.query));
        }

        rb.setQuery(q);

        SearchResponse response = rb.execute().actionGet();


        if (response.getHits().getTotalHits() > 0) {
            return response.getHits().getMaxScore();
        }

        return 0.0f;
    }

    public List<Request> listForOffer (Long accountId, int page, int perPage, long offerId) {

        logger.info("list for offer");

        List<Request> requestList = new ArrayList<>();

        List<Request> rqList = list(accountId, 0, 100, null, null);

        // перебрать и проверить подходит ли объект
        HashMap<Float, Request> tMap = new HashMap<Float, Request>();
        rqList.forEach(rq -> {
            ArrayList<GeoPoint> gpa = new ArrayList<>();
            for (auxclass.GeoPoint p : rq.getSearchArea()) {
                gpa.add(new GeoPoint(p.lat, p.lon));
            }
            if (rq.getOfferTypeCode() != null) {
                float score = checkOffer(accountId, offerId, rq.getOfferTypeCode(), rq.getRequest(), gpa);
                if (score > 2.1f) {
                    if (tMap.get(score) != null) {
                        score += 0.00001;
                    }
                    tMap.put(score, rq);
                }
            }
        });

        // отсортировать подходящие по оценке
        SortedSet<Float> scores = new TreeSet<Float>(tMap.keySet());
        for (Float s : scores) {
            Request r = tMap.get(s);
            requestList.add(0, r);
        }

        return requestList;
    }

    public Request get (long id) {

        this.logger.info("get");

        Request result = null;

        GetResponse response = this.elasticClient.prepareGet(E_INDEX, E_TYPE, Long.toString(id)).get();
        String dataJson = response.getSourceAsMap().get(E_DATAFIELD).toString();
        result = gson.fromJson(dataJson, Request.class);

        return result;
    }

    public Request save (Request request) throws Exception {

        this.logger.info("save");

        indexRequest(request);

        return request;
    }

    public Request delete (String id) {
        return null;
    }


    public void indexRequest(Request request) {

        Map<String, Object> json = new HashMap<>();

        if (request.getId() == null) {
            request.setId(CommonUtils.getSystemTimestamp());
        }


        json.put("id", request.getId());
        json.put("accountId", request.getAccountId());
        json.put("request", request.request);

        json.put("agentId", request.getAgentId());
        json.put("personId", request.getPersonId());

        json.put("stageCode", request.getStageCode());
        json.put("offerTypeCode", request.getOfferTypeCode());

        json.put(E_DATAFIELD, gson.toJson(request));


        IndexResponse response = this.elasticClient.prepareIndex(E_INDEX, E_TYPE, Long.toString(request.getId())).setSource(json).get();
    }
}
