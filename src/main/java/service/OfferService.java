package service;

import com.google.gson.*;

import configuration.AppConfig;
import hibernate.entity.ImportOffer;
import hibernate.entity.Offer;
import hibernate.entity.Person;
import hibernate.entity.User;

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
import utils.GeoUtils;
import utils.Query;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.*;


public class OfferService {

    public class ListResult {
        long hitsCount;
        List<Offer> list;
    }

    Logger logger = LoggerFactory.getLogger(OfferService.class);

    EntityManagerFactory emf;
    private final Client elasticClient;

    private final String E_INDEX = "rplus-index-dev";
    private final String E_TYPE = "offers";

    Gson gson = new GsonBuilder().create();


    public OfferService (EntityManagerFactory emf, Client elasticClient) {

        this.emf = emf;
        this.elasticClient = elasticClient;
    }

    public ListResult listImport (int page, int perPage, Map<String, String> filter, Map<String, String> sort, String searchQuery, List<GeoPoint> geoSearchPolygon) {
        List<Offer> offerList = new ArrayList<>();
        Long hitsCount = 0L;
        ListResult r = new ListResult();
        r.hitsCount = 0;

        this.logger.info("list import");

        String url = AppConfig.IMPORT_URL + "/api/offer/search?"
        + "query=" + URLEncoder.encode(searchQuery)
        + "&offer_type=" + filter.get("offerTypeCode")
        + "&page=" + page
        + "&per_page=" + perPage
        + "&sort=" + gson.toJson(sort)
        + "&search_area=" + gson.toJson(geoSearchPolygon);

        this.logger.info(url);

        try {

            URL iurl = new URL(url);

            HttpURLConnection uc = (HttpURLConnection) iurl.openConnection();
            uc.connect();

            int status = uc.getResponseCode();

            switch (status) {
                case 200:
                case 201:
                    String jsonStr;
                    BufferedReader br = new BufferedReader(new InputStreamReader(uc.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line+"\n");
                    }
                    br.close();
                    jsonStr = sb.toString();

                    JsonObject jsonObject = new JsonParser().parse(jsonStr).getAsJsonObject();

                    JsonArray t = jsonObject.get("list").getAsJsonArray();
                    hitsCount = jsonObject.get("hitsCount").getAsLong();

                    t.forEach(je -> {
                        String os = je.getAsString();

                        ImportOffer io = gson.fromJson(os, ImportOffer.class);

                        Offer offer = Offer.fromImportOffer(io);
                        offerList.add(offer);
                    });

            }
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            return null;
        }

        r.hitsCount = hitsCount;
        r.list = offerList;

        return r;
    }

    public ListResult list (Long accountId, int page, int perPage, Map<String, String> filter, Map<String, String> sort, String searchQuery, List<GeoPoint> geoSearchPolygon) {

        this.logger.info("list");

        List<Offer> offerList = new ArrayList<>();

        EntityManager em = emf.createEntityManager();

        Map<String, String> queryParts = Query.process(searchQuery);
        String request = queryParts.get("req");
        String excl = queryParts.get("excl");
        String near = queryParts.get("near");
        List<FilterObject> rangeFilters = Query.parse(request);

        SearchRequestBuilder rb = elasticClient.prepareSearch("rplus")
                .setTypes("offer")
                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                .setFrom(page * perPage).setSize(perPage);


        BoolQueryBuilder q = QueryBuilders.boolQuery();

        q.must(QueryBuilders.termQuery("accountId", accountId));

        if (geoSearchPolygon.size() > 0) {
            q.filter(QueryBuilders.geoPolygonQuery("location", geoSearchPolygon));
        }

        if (excl != null && excl.length() > 0) {
            q.mustNot(QueryBuilders.matchQuery("title", excl));
            q.mustNot(QueryBuilders.matchQuery("address", excl));
            q.mustNot(QueryBuilders.matchQuery("spec", excl));
            q.mustNot(QueryBuilders.matchQuery("description", excl));
        }


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

        if (request != null && request.length() > 0) {
            //q.must(QueryBuilders.matchPhraseQuery("allTags", searchQuery).slop(50));
            q.should(QueryBuilders.matchQuery("title", request).boost(8));
            q.should(QueryBuilders.matchQuery("address", request).boost(4));
            q.should(QueryBuilders.matchQuery("spec", request).boost(2));
            q.should(QueryBuilders.matchQuery("description", request));
        }

        sort.forEach((k,v) -> {
            if (v.equals("ASC")) {
                rb.addSort(SortBuilders.fieldSort(k).order(SortOrder.ASC));
            } else if (v.equals("DESC")) {
                rb.addSort(SortBuilders.fieldSort(k).order(SortOrder.DESC));
            }
        });


        rb.setQuery(q);

        SearchResponse response = rb.execute().actionGet();

        ListResult r = new ListResult();
        r.hitsCount = response.getHits().getTotalHits();


        for (SearchHit sh: response.getHits()) {
            Offer offer = em.find(hibernate.entity.Offer.class, Long.parseLong(sh.getId()));
            offerList.add(offer);
        }

        r.list = offerList;

        return r;
    }

    public Offer get (long id) {

        this.logger.info("get");

        EntityManager em = emf.createEntityManager();

        Offer offer = em.find(hibernate.entity.Offer.class, id);

        em.close();

        return offer;
    }

    public Offer save (Offer offer) throws Exception {

        this.logger.info("save");

        EntityManager em = emf.createEntityManager();

        Offer result;

        String fullAddress = CommonUtils.strNotNull(offer.getLocality()) + " " + CommonUtils.strNotNull(offer.getAddress()) + " " + CommonUtils.strNotNull(offer.getHouseNum());

        Double[] latLon = GeoUtils.getCoordsByAddr(fullAddress);
        if (latLon != null) {
            offer.setLocationLat(latLon[0]);
            offer.setLocationLon(latLon[1]);

            List<String> districts = GeoUtils.getLocationDistrict(latLon[0], latLon[1]);
            if (!districts.isEmpty()) {
                offer.setDistrict(districts.get(0));
            }
        }

        Person p = offer.getPerson();
        if (offer.getPersonId() == null && p != null) {
            em.getTransaction().begin();
            Person r = em.merge(p);
            em.getTransaction().commit();
            offer.setPersonId(r.getId());
            offer.setPerson(null);
        }

        em.getTransaction().begin();
        result = em.merge(offer);
        em.getTransaction().commit();

        indexOffer(result);

        return result;
    }

    public void indexOffer(Offer offer) {

        HashMap<String, String> dTypeCode = new HashMap<>();

        dTypeCode.put("room", "Комната");
        dTypeCode.put("apartment", "Квартира");
        dTypeCode.put("apartment_small", "Малосемейка");
        dTypeCode.put("apartment_new", "Новостройка");
        dTypeCode.put("house", "Дом");

        dTypeCode.put("dacha", "Дача");
        dTypeCode.put("cottage", "Коттедж");

        dTypeCode.put("townhouse", "Таунхаус");

        dTypeCode.put("other", "Другое");
        dTypeCode.put("land", "Земля");

        dTypeCode.put("building", "здание");
        dTypeCode.put("office_place", "офис");
        dTypeCode.put("office", "офис");
        dTypeCode.put("market_place", "торговая площадь");
        dTypeCode.put("production_place", "производственное помещение");
        dTypeCode.put("gpurpose_place", "помещение общего назначения");
        dTypeCode.put("autoservice_place", "автосервис");
        dTypeCode.put("service_place", "помещение под сферу услуг");
        dTypeCode.put("warehouse_place", "склад база");
        dTypeCode.put("garage", "гараж");


        HashMap<Integer, String> dApScheme = new HashMap<>();
        dApScheme.put(1, "Индивидуальная");
        dApScheme.put(2, "Новая");
        dApScheme.put(3, "Общежитие");
        dApScheme.put(4, "Сталинка");
        dApScheme.put(5, "Улучшенная");
        dApScheme.put(6, "Хрущевка");


        HashMap<Integer, String> dBalcony = new HashMap<>();
        dBalcony.put(1, "без балкона");
        dBalcony.put(2, "балкон");
        dBalcony.put(3, "лоджия");
        dBalcony.put(4, "2 балкона");
        dBalcony.put(5, "2 лоджии");
        dBalcony.put(6, "балкон и лоджия");
        dBalcony.put(7, "балкон застеклен");
        dBalcony.put(8, "лоджия застеклена");


        HashMap<Integer, String> dBathroom = new HashMap<>();
        dBathroom.put(1, "без удобств");
        dBathroom.put(2, "туалет");
        dBathroom.put(3, "с удобствами");
        dBathroom.put(4, "душ и туалет");
        dBathroom.put(5, "2 смежных санузла");
        dBathroom.put(6, "2 раздельных санузла");
        dBathroom.put(7, "санузел совмещенный");

        HashMap<Integer, String> dCondition = new HashMap<>();
        dCondition.put(1, "социальный ремонт");
        dCondition.put(2, "сделан ремонт");
        dCondition.put(3, "дизайнерский ремонт");
        dCondition.put(4, "требуется ремонт");
        dCondition.put(5, "требуется косм. ремонт");
        dCondition.put(6, "после строителей");
        dCondition.put(7, "евроремонт");
        dCondition.put(8, "удовлетворительное");
        dCondition.put(9, "нормальное");

        HashMap<Integer, String> dHouseType = new HashMap<>();
        dHouseType.put(1, "Брус");
        dHouseType.put(2, "Деревянный");
        dHouseType.put(3, "Каркасно-засыпной");
        dHouseType.put(4, "Кирпичный");

        HashMap<Integer, String> dRoomScheme = new HashMap<>();
        dRoomScheme.put(1, "Икарус");
        dRoomScheme.put(2, "Кухня-гостинная");
        dRoomScheme.put(3, "Раздельные");
        dRoomScheme.put(4, "Смежно-раздельные");
        dRoomScheme.put(5, "Смежные");
        dRoomScheme.put(6, "Студия");


        String title = dTypeCode.get(offer.getTypeCode());

        String address = CommonUtils.strNotNull(offer.getLocality()) + " " + CommonUtils.strNotNull(offer.getAddress()) + " " + CommonUtils.strNotNull(offer.getHouseNum());
        if (offer.getDistrict() != null) {
            address += " " + offer.getDistrict();
        }

        ArrayList<String> specArray = new ArrayList<>();
        specArray.add(CommonUtils.strNotNull(dApScheme.get(offer.getApSchemeId())));
        specArray.add(CommonUtils.strNotNull(dBalcony.get(offer.getBalconyId())));
        specArray.add(CommonUtils.strNotNull(dBathroom.get(offer.getBathroomId())));
        specArray.add(CommonUtils.strNotNull(dCondition.get(offer.getConditionId())));
        specArray.add(CommonUtils.strNotNull(dHouseType.get(offer.getHouseTypeId())));
        specArray.add(CommonUtils.strNotNull(dRoomScheme.get(offer.getRoomSchemeId())));

        String spec = String.join(" ", specArray);

        Map<String, Object> json = new HashMap<String, Object>();
        json.put("id", offer.getId());
        json.put("title", title);
        json.put("address", address);
        json.put("spec", spec);
        json.put("description", CommonUtils.strNotNull(offer.getDescription()));

        // geo search
        if (offer.getLocationLat() != null && offer.getLocationLon() != 0) {
            json.put("location", new GeoPoint(offer.getLocationLat(), offer.getLocationLon()));
        }

        // filters
        json.put("accountId", offer.getAccountId());
        json.put("offerTypeCode", offer.getOfferTypeCode());
        json.put("typeCode", offer.getTypeCode());
        json.put("stateCode", offer.getStateCode());
        json.put("agentId", offer.getAgentId());
        json.put("personId", offer.getPersonId());
        json.put("changeDate", offer.getChangeDate());

        // range query
        json.put("floor", offer.getFloor());
        json.put("ownerPrice", offer.getOwnerPrice());
        json.put("roomsCount", offer.getRoomsCount());
        json.put("squareTotal", offer.getSquareTotal());

        IndexResponse response = this.elasticClient.prepareIndex("rplus", "offer", Long.toString(offer.getId())).setSource(json).get();
    }

    public Offer delete (int id) {
        return null;
    }
}
