package service;

import auxclass.ImportOffer;
import entity.*;
import com.google.gson.*;

import configuration.AppConfig;

import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.geo.GeoPoint;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import utils.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.*;

import static utils.CommonUtils.getUnixTimestamp;


public class OfferService {

    public class ListResult {
        long hitsCount;
        List<Offer> list;
    }

    private final String E_INDEX = "rplus";
    private final String E_TYPE = "offer";
    private final String E_DATAFIELD = "data";


    Logger logger = LoggerFactory.getLogger(OfferService.class);
    private final Client elasticClient;


    Gson gson = new GsonBuilder().create();

    HashMap<String, String> dTypeCode = new HashMap<>();
    HashMap<Integer, String> dApScheme = new HashMap<>();
    HashMap<Integer, String> dBalcony = new HashMap<>();
    HashMap<Integer, String> dBathroom = new HashMap<>();
    HashMap<Integer, String> dCondition = new HashMap<>();
    HashMap<Integer, String> dHouseType = new HashMap<>();
    HashMap<Integer, String> dRoomScheme = new HashMap<>();


    public OfferService (Client elasticClient) {

        this.elasticClient = elasticClient;


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


        dApScheme.put(1, "Индивидуальная");
        dApScheme.put(2, "Новая");
        dApScheme.put(3, "Общежитие");
        dApScheme.put(4, "Сталинка");
        dApScheme.put(5, "Улучшенная");
        dApScheme.put(6, "Хрущевка");


        dBalcony.put(1, "без балкона");
        dBalcony.put(2, "балкон");
        dBalcony.put(3, "лоджия");
        dBalcony.put(4, "2 балкона");
        dBalcony.put(5, "2 лоджии");
        dBalcony.put(6, "балкон и лоджия");
        dBalcony.put(7, "балкон застеклен");
        dBalcony.put(8, "лоджия застеклена");


        dBathroom.put(1, "без удобств");
        dBathroom.put(2, "туалет");
        dBathroom.put(3, "с удобствами");
        dBathroom.put(4, "душ и туалет");
        dBathroom.put(5, "2 смежных санузла");
        dBathroom.put(6, "2 раздельных санузла");
        dBathroom.put(7, "санузел совмещенный");


        dCondition.put(1, "социальный ремонт");
        dCondition.put(2, "сделан ремонт");
        dCondition.put(3, "дизайнерский ремонт");
        dCondition.put(4, "требуется ремонт");
        dCondition.put(5, "требуется косм. ремонт");
        dCondition.put(6, "после строителей");
        dCondition.put(7, "евроремонт");
        dCondition.put(8, "удовлетворительное");
        dCondition.put(9, "нормальное");


        dHouseType.put(1, "Брус");
        dHouseType.put(2, "Деревянный");
        dHouseType.put(3, "Каркасно-засыпной");
        dHouseType.put(4, "Кирпичный");


        dRoomScheme.put(1, "Икарус");
        dRoomScheme.put(2, "Кухня-гостинная");
        dRoomScheme.put(3, "Раздельные");
        dRoomScheme.put(4, "Смежно-раздельные");
        dRoomScheme.put(5, "Смежные");
        dRoomScheme.put(6, "Студия");
    }


    public ListResult list (Long accountId, int page, int perPage, Map<String, String> filter, Map<String, String> sort, String searchQuery, List<GeoPoint> geoSearchPolygon) {

        this.logger.info("list");

        List<Offer> offerList = new ArrayList<>();

        Map<String, String> queryParts = Query.process(searchQuery);
        String request = queryParts.get("req");
        String excl = queryParts.get("excl");
        String near = queryParts.get("near");

        ParseResult pr = Query.parse(request);
        List<FilterObject> rangeFilters = pr.filterList;


        SearchRequestBuilder rb = elasticClient.prepareSearch(E_INDEX)
                .setTypes(E_TYPE)
                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                .setFrom(page * perPage).setSize(perPage);


        BoolQueryBuilder q = QueryBuilders.boolQuery();

        q.must(QueryBuilders.termQuery("accountId", accountId));

        if (geoSearchPolygon.size() > 0) {
            q.filter(QueryBuilders.geoPolygonQuery("location", geoSearchPolygon));
        }

        if (excl != null && excl.length() > 0) {
            q.mustNot(QueryBuilders.matchQuery("title", excl));
            q.mustNot(QueryBuilders.matchQuery("address_ext", excl));
            q.mustNot(QueryBuilders.matchQuery("spec", excl));
            q.mustNot(QueryBuilders.matchQuery("description", excl));
        }


        filter.forEach((k,v) -> {
            logger.info(k + " - " + v);
            if (k.equals("stageCode")) {
                if (v != null && !v.equals("all")) {
                    q.must(QueryBuilders.termQuery(k, v));
                } else {
                    q.mustNot(QueryBuilders.termQuery(k, "archive"));
                }
            } else if (k.equals("orgType")) {
                if (v != null && !v.equals("all")) {
                    if (StringUtils.isNumeric(v)) {
                        q.must(QueryBuilders.termQuery("agentId", v));
                    } else {
                        q.must(QueryBuilders.termQuery(k, v));
                    }
                }
                /*
                switch (v) {
                    case "realtor":
                        // выбрать все организации
                        // выбрать всех агентов
                        // выбрать все предложения агентов
                        break;
                    case "private":
                        break;
                    case "partner":
                        break;
                    case "company":
                        break;
                    case "my":

                        break;
                }
                */
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

        if (pr.query != null && pr.query.length() > 2) {

            q.must(QueryBuilders.matchQuery("tags", pr.query).operator(Operator.AND));

            q.should(QueryBuilders.matchQuery("title", pr.query).boost(8));
            q.should(QueryBuilders.matchQuery("address_ext", pr.query).boost(4));
            q.should(QueryBuilders.matchQuery("district", pr.query).boost(2));
            q.should(QueryBuilders.matchQuery("spec", pr.query).boost(2));
            q.should(QueryBuilders.matchQuery("description", pr.query));
            q.should(QueryBuilders.matchQuery("orgName", pr.query));
        }


        if (sort.size() == 0) {
            rb.addSort(SortBuilders.fieldSort("changeDate").order(SortOrder.DESC));
        } else {
            sort.forEach((k, v) -> {
                if (v.equals("ASC")) {
                    rb.addSort(SortBuilders.fieldSort(k).order(SortOrder.ASC));
                } else if (v.equals("DESC")) {
                    rb.addSort(SortBuilders.fieldSort(k).order(SortOrder.DESC));
                }
            });
        }


        rb.setQuery(q);

        SearchResponse response = rb.execute().actionGet();

        ListResult r = new ListResult();
        r.hitsCount = response.getHits().getTotalHits();

        for (SearchHit sh: response.getHits()) {
            String dataJson = sh.getSourceAsMap().get(E_DATAFIELD).toString();
            offerList.add(gson.fromJson(dataJson, Offer.class));
        }

        r.list = offerList;

        return r;
    }


    public ListResult listImport (int page, int perPage, Map<String, String> filter, Map<String, String> sort, String searchQuery, List<GeoPoint> geoSearchPolygon)
    throws UnsupportedEncodingException
    {
        List<Offer> offerList = new ArrayList<>();
        Long hitsCount = 0L;
        ListResult r = new ListResult();
        r.hitsCount = 0;

        this.logger.info("list import");

        String url = AppConfig.IMPORT_URL + "/api/offer/search?"
        + "query=" + URLEncoder.encode(searchQuery, "UTF-8")
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

                        try {
                            ImportOffer io = gson.fromJson(os, ImportOffer.class);

                            Offer offer = Offer.fromImportOffer(io);
                            offerList.add(offer);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            logger.error(ex.getMessage());
                        }
                    });

            }
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error(ex.getMessage());
            return null;
        }

        r.hitsCount = hitsCount;
        r.list = offerList;

        return r;
    }


    public ListResult listSimilar (Long accountId, int page, int perPage, long id) {

        this.logger.info("list similar");

        List<Offer> offerList = new ArrayList<>();

        Offer offer = get(id);

        SearchRequestBuilder rb = elasticClient.prepareSearch(E_INDEX)
                .setTypes(E_TYPE)
                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                .setFrom(page * perPage).setSize(perPage);

        BoolQueryBuilder q = QueryBuilders.boolQuery();

        q.must(QueryBuilders.termQuery("accountId", accountId));
        q.must(QueryBuilders.termQuery("typeCode", offer.getTypeCode()));
        q.mustNot(QueryBuilders.termQuery("id", offer.getId()));

        if (offer.getHouseTypeId() != null && offer.getHouseTypeId() > 0) {
            q.must(QueryBuilders.termQuery("houseType", dHouseType.get(offer.getHouseTypeId())));
        }

        if (offer.getRoomsCount() != null && offer.getRoomsCount() > 0) {
            q.must(QueryBuilders.termQuery("roomsCount", offer.getRoomsCount()));
        }

        if (offer.getSquareTotal() != null && offer.getSquareTotal() > 0) {
            q.must(QueryBuilders.rangeQuery("squareTotal").lte(offer.getSquareTotal() + 10).gte(offer.getSquareTotal() - 10));
        }

        if (offer.getLocationLat() != null) {
            GeoDistanceQueryBuilder gdr = QueryBuilders.geoDistanceQuery("location");
            gdr.point(offer.getLocationLat(), offer.getLocationLon());
            gdr.distance("500m");
            q.filter(gdr);
        } else if (offer.getDistrict() != null && offer.getDistrict().length() > 0) {
            q.must(QueryBuilders.termQuery("district", offer.getDistrict()));
        }

        rb.setQuery(q);

        SearchResponse response = rb.execute().actionGet();

        ListResult r = new ListResult();
        r.hitsCount = response.getHits().getTotalHits();


        for (SearchHit sh: response.getHits()) {
            String dataJson = sh.getSourceAsMap().get(E_DATAFIELD).toString();
            offerList.add(gson.fromJson(dataJson, Offer.class));
        }

        r.list = offerList;

        return r;
    }


    public Offer get (long id) {

        this.logger.info("get");

        Offer result = null;

        GetResponse response = this.elasticClient.prepareGet(E_INDEX, E_TYPE, Long.toString(id)).get();

        String dataJson = response.getSourceAsMap().get(E_DATAFIELD).toString();
        result = gson.fromJson(dataJson, Offer.class);

        return result;
    }


    public Offer save (Offer offer) throws Exception {

        this.logger.info("save");

        Offer result;


        String fullAddress = offer.getFullAddress().getAsString();

        if (fullAddress.length() > 0) {
            Double[] latLon = GeoUtils.getCoordsByAddr(fullAddress);
            if (latLon != null) {
                offer.setLocationLat(latLon[0]);
                offer.setLocationLon(latLon[1]);

                List<String> districts = GeoUtils.getLocationDistrict(latLon[0], latLon[1]);
                if (!districts.isEmpty()) {
                    offer.setDistrict(districts.get(0));
                }
            }
        }

        if (offer.getId() != null) {
            Offer so = get(offer.getId());

            assert (so != null);

            if (offer.equals(so) == false) {
                offer.setChangeDate(getUnixTimestamp());
                if (!offer.getAgentId().equals(so.getAgentId())) {
                    offer.setAssignDate(getUnixTimestamp());
                }

            }
        }
        offer.preIndex();
        indexOffer(offer);

        return offer;
    }


    public Offer delete (int id) {
        return null;
    }



    public void indexOffer(Offer offer) {

        if (offer.getId() == null) {
            offer.setId(CommonUtils.getSystemTimestamp());
        }

        String title = dTypeCode.get(offer.getTypeCode());

        String address = offer.getFullAddress().getAsString();

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
        json.put("accountId", offer.getAccountId());
        json.put("title", title);
        json.put("address_ext", address);
        json.put("spec", spec);
        json.put("description", CommonUtils.strNotNull(offer.getDescription()));

        json.put("tags", title + " " + address + " " + spec + " " + CommonUtils.strNotNull(offer.getDescription()));

        // geo search
        if (offer.getLocationLat() != null && offer.getLocationLon() != 0) {
            json.put("location", new GeoPoint(offer.getLocationLat(), offer.getLocationLon()));
        }

        // filters
        json.put("offerTypeCode", offer.getOfferTypeCode());
        json.put("typeCode", offer.getTypeCode());
        json.put("stageCode", offer.getStageCode());
        json.put("agentId", offer.getAgentId());
        json.put("personId", offer.getPersonId());
        json.put("changeDate", offer.getChangeDate());

        // range query
        json.put("floor", offer.getFloor());
        json.put("ownerPrice", offer.getOwnerPrice());
        json.put("roomsCount", offer.getRoomsCount());
        json.put("squareTotal", offer.getSquareTotal());

        // sort
        if (offer.getFullAddress() != null) {
            json.put("locality", offer.getFullAddress().getCity());
            json.put("address", offer.getFullAddress().getStreet());
        }
        json.put("district", offer.getDistrict());
        json.put("poi", offer.getPoi());


        json.put("houseType", dHouseType.get(offer.getHouseTypeId()));
        json.put("apScheme", dApScheme.get(offer.getApSchemeId()));
        json.put("roomScheme", dRoomScheme.get(offer.getRoomSchemeId()));
        json.put("condition", dCondition.get(offer.getConditionId()));
        json.put("balcony", dBalcony.get(offer.getBalconyId()));
        json.put("bathroom", dBathroom.get(offer.getBalconyId()));

        json.put("addDate", offer.getAddDate());
        json.put("changeDate", offer.getChangeDate());
        json.put("lastSeenDate", offer.getLastSeenDate());

        // TODO: что если имя изменили?
        if (offer.getAgent() != null) {
            json.put("agentName", offer.getAgent().getName());
        }
        if (offer.getPerson() != null) {
            json.put("contactName", offer.getPerson().getName());
            if (offer.getPerson().getOrganisationId() != null) {

                /*
                PersonService.get(personId)
                OrganisationService.get(organisationId)
                EntityManager em = emf.createEntityManager();
                Organisation org = em.find(Organisation.class, offer.getPerson().getOrganisationId());
                json.put("orgName", org.getName());
                json.put("orgType", org.getTypeCode_n());
                em.close();
                */
            } else {
                json.put("orgName", offer.getPerson().getName());
                json.put("orgType", "private");
            }
        }

        offer.preIndex();
        json.put(E_DATAFIELD, gson.toJson(offer));

        IndexResponse response = this.elasticClient.prepareIndex(E_INDEX, E_TYPE, Long.toString(offer.getId())).setSource(json).get();
    }
}
