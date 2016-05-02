package service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import morphia.entity.Offer;
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

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by owl on 3/27/16.
 */
public class OfferService {
    Logger logger = LoggerFactory.getLogger(OfferService.class);

    private final Client elasticClient;
    private final String E_INDEX = "rplus-index";
    private final String E_TYPE = "offers";

    Gson gson = new GsonBuilder().create();

    private List<String> keywordList = Arrays.asList("ул", "улица", "пер", "переулок", "г", "город");

    public OfferService(Client elasticClient) {
        this.elasticClient = elasticClient;
    }

    private static boolean regExTest(String regexp, String testString){
        Pattern p = Pattern.compile(regexp);
        Matcher m = p.matcher(testString);
        return m.matches();
    }

    class FilterObject {
        String fieldName;

        Integer exactVal;
        Integer lowerVal;
        Integer upperVal;

        public FilterObject(String fieldName, Integer exactVal) {
            this.fieldName = fieldName;
            this.exactVal = exactVal;
            this.lowerVal = null;
            this.upperVal = null;
        }

        public FilterObject(String fieldName, Integer lowerVal, Integer upperVal) {
            this.fieldName = fieldName;
            this.exactVal = exactVal;
            this.lowerVal = lowerVal;
            this.upperVal = upperVal;
        }
    }

    public List<FilterObject> parseQuery(String query) {

        List<FilterObject> filterList = new LinkedList<>();


        // Some commonly used regexes
        String sta_re = "(?:^|\\s+|,\\s*)";
        String end_re = "(?:\\s+|,|$)";
        String tofrom_re = "(?:от|до|с|со|по)";

        {
            boolean matched;
            Integer exactPrice = null;
            Integer price1 = null;
            Integer price2 = null;

            Pattern p;
            Matcher m;

            do {
                matched = false;

                String float_re = "\\d+(?:[,.]\\d+)?";
                String rub_re = "р(?:\\.|(?:уб(?:\\.|лей)?)?)?";
                String ths_re = "т(?:\\.|(ыс(?:\\.|яч)?)?)?";
                String mln_re = "(?:(?:млн\\.?)|(?:миллион\\w*))";

                // Range

                p = Pattern.compile(
                        sta_re + "(?:(?:от|с)\\s+)?(" + float_re + ")\\s*(?:до|по|\\-)\\s*(" + float_re + ")\\s*((?:" + rub_re + ")|(?:" + ths_re + "}\\s*" + rub_re + ")|(?:" + mln_re + "\\s*(?:" + rub_re + ")?))" + end_re);
                m = p.matcher(query);
                if (m.find()) {
                    String ss = m.group(3);
                    price1 = Integer.parseInt(m.group(1));
                    price2 = Integer.parseInt(m.group(2));

                    if (regExTest("^" + rub_re + "$", ss)) {
                        price1 = price1 / 1000;
                        price2 = price2 / 1000;
                    } else if (regExTest("^" + mln_re + "\\s*(?:" + rub_re + ")?$", ss)) {
                        price1 = price1 * 1000;
                        price2 = price2 * 1000;
                    } else {

                    }

                    m.replaceFirst("");
                }
                // Single value
                else {
                    p = Pattern.compile(sta_re + "(?:(" + tofrom_re + ")\\s+)?(" + float_re + ")\\s*((?:" + rub_re + ")|(?:" + ths_re + "\\s*" + rub_re + ")|(?:" + mln_re + "\\s*(?:" + rub_re + ")?))" + end_re);
                    m = p.matcher(query);
                    if (m.find()) {
                        String prefix = m.group(1);
                        String ss = m.group(3);
                        Integer price = Integer.parseInt(m.group(2));

                        if (regExTest("^" + rub_re + "$", ss)) {
                            price = price / 1000;
                        } else if (regExTest("^" + mln_re + "\\s*(?:" + rub_re + ")?$", ss)) {
                            price = price * 1000;
                        } else {
                            price = price;
                        }
                        if (prefix != null) {
                            if (prefix.matches("от") || prefix.matches("с")) {
                                price1 = price;
                            } else if (prefix.matches("до") || prefix.matches("по")) {
                                price2 = price;
                            }
                        } else {
                            exactPrice = price;
                        }
                        //matched = true;
                        m.replaceFirst("");
                    }
                }
            } while (matched == true);

            if (price1 != null || price2 != null) {
                filterList.add(new FilterObject("owner_price", price1, price2));
            } else if (exactPrice != null) {
                filterList.add(new FilterObject("owner_price", exactPrice));
            }
        }

        // Rooms count
        {
            boolean matched = false;
            Integer rooms_count = null;
            Integer rooms_count1 = null;
            Integer rooms_count2 = null;

            do {
                matched = false;

                // range
                Pattern p = Pattern.compile(
                        sta_re + "(\\d)\\s*\\-\\s*(\\d)\\s*к(?:\\.|(?:омн(?:\\.|ат\\w*)?)?)?" + end_re);
                Matcher m = p.matcher(query);

                if (m.find()) {
                    rooms_count1 = Integer.parseInt(m.group(1));
                    rooms_count2 = Integer.parseInt(m.group(2));

                    m.replaceFirst("");
                } else {
                    //single
                    p = Pattern.compile(
                            sta_re + "(\\d)(?:\\-?х\\s)?\\s*к(?:\\.|(?:омн(?:\\.|ат\\w*)?)?)?" + end_re);
                    m = p.matcher(query);
                    if (m.find()) {
                        rooms_count = Integer.parseInt(m.group(1));

                        m.replaceFirst("");
                    } else {
                        // special case
                        p = Pattern.compile(
                                sta_re + "(одн[оа]|двух|трех|четырех|пяти|шести|семи|восьми|девяти)\\s*комн(?:\\.|(?:ат\\w*)?)?" + end_re);
                        m = p.matcher(query);
                        if (m.find()) {
                            String v = m.group(1);

                            if (v.matches("одно")) rooms_count = 1;
                            if (v.matches("двух")) rooms_count = 2;
                            if (v.matches("трех")) rooms_count = 3;
                            if (v.matches("четырех")) rooms_count = 4;
                            if (v.matches("пяти")) rooms_count = 5;
                            if (v.matches("шести")) rooms_count = 6;
                            if (v.matches("семи")) rooms_count = 7;
                            if (v.matches("восьми")) rooms_count = 8;
                            if (v.matches("девяти")) rooms_count = 9;

                            m.replaceFirst("");
                        }
                    }
                }
            } while (matched);

            //$q = trim($q) if $matched;

            if (rooms_count1 != null || rooms_count2 != null) {
                filterList.add(new FilterObject("rooms_count", rooms_count1, rooms_count2));
            } else if (rooms_count != null) {
                filterList.add(new FilterObject("rooms_count", rooms_count));
            };
        }

        // Floor
        {
            boolean matched = false;
            Integer exactFloor = null;
            Integer floor1 = null;
            Integer floor2 = null;
            do {
                matched = false;

                String flr_re = "э(?:\\.|(?:т(?:\\.|аж\\w*)?)?)?";

                // Range
                Pattern p = Pattern.compile(
                        sta_re + "(?:(?:от|с|со)\\s+)?(\\d{1,2})\\s*(?:до|по|\\-)\\s*(\\d{1,2})\\s*" + flr_re + end_re);
                Matcher m = p.matcher(query);
                if (m.find()) {
                    floor1 = Integer.parseInt(m.group(1));
                    floor2 = Integer.parseInt(m.group(2));

                    m.replaceFirst("");
                } else {
                // Single value
                    p = Pattern.compile(
                        sta_re + "(?:(" + tofrom_re + ")\\s+)?(\\d{1,2})\\s*" + flr_re + end_re);
                    m = p.matcher(query);
                    if (m.find()) {

                        String prefix = m.group(1);
                        if (prefix != null) {
                            if (prefix.matches("до") || prefix.matches("по")){
                                floor2 = Integer.parseInt(m.group(2));
                            } else if(prefix.matches("от") || prefix.matches("с")) {
                                floor1 = Integer.parseInt(m.group(2));
                            }
                        } else{
                            exactFloor = Integer.parseInt(m.group(2));
                        }

                        m.replaceFirst("");
                    }
                }
            } while (matched);

            if (floor1 != null || floor2 != null) {
                filterList.add(new FilterObject("floor", floor1, floor2));
            } else if (exactFloor != null) {
                filterList.add(new FilterObject("floor", exactFloor));
            }
        }

        // Square
        {
            boolean matched = false;
            Integer exactSquare = null;
            Integer square1 = null;
            Integer square2 = null;
            do {
                matched = false;

                String sqr_re = "(?:кв(?:\\.|адратн\\w*)?)?\\s*м(?:\\.|2|етр\\w*)?";

                // Range
                Pattern p = Pattern.compile(
                        sta_re + "(?:(?:от|с)\\s+)?(\\d+)\\s*(?:до|по|\\-)\\s*(\\d+)\\s*" + sqr_re + end_re);
                Matcher m = p.matcher(query);
                if (m.find()) {
                    square1 = Integer.parseInt(m.group(1));
                    square2 = Integer.parseInt(m.group(2));

                    m.replaceFirst("");
                } else {
                    // Single value

                    p = Pattern.compile(
                            sta_re + "(?:(" + tofrom_re + ")\\s+)?(\\d+)\\s*" + sqr_re + end_re);
                    m = p.matcher(query);
                    if (m.find()) {
                        String prefix = m.group(1);
                        logger.info(prefix);
                        if (prefix != null) {
                            if (prefix.matches("до") || prefix.matches("по")) {
                                square2 = Integer.parseInt(m.group(2));
                            } else if(prefix.matches("от") || prefix.matches("с")){
                                square1 = Integer.parseInt(m.group(2));
                            }
                        } else {
                            exactSquare = Integer.parseInt(m.group(2));
                        }

                        m.replaceFirst("");
                    }
                }
            } while (matched);

            if (square1 != null || square2 != null) {
                filterList.add(new FilterObject("square_total", square1, square2));
            } else if (exactSquare != null) {
                filterList.add(new FilterObject("square_total", exactSquare));
            }
        }

        return filterList;
    }

    private Map<String, String> processQuery (String query) {

        Map<String, String> result = new HashMap<String, String>();
        String req = null;
        String excl = null;
        String near = null;
        Pattern p = Pattern.compile("(.+|)(кроме |не )(.+)");
        Matcher m = p.matcher(query);
        if (m.find()) {
            req = m.group(1);
            excl = m.group(3);
        } else {
            req = query;
        }

        Pattern.compile("(рядом |рядом с)(.+)");
        if (m.find()) {
            near = m.group(2);
        }

        result.put("req", req);
        result.put("excl", excl);
        result.put("near", near);
        return result;
    }


    public List getAddress(String query) {

        List <String> termList = new LinkedList<>();

        String terms[] = query.split(" ");
        boolean after_street = false;
        SearchRequestBuilder req;
        SearchResponse response;

        for (String term: terms) {
            // определить является ли терм названием нас. пункта, улицы, номером дома
            // if term in keyList
            if (keywordList.contains(term.toLowerCase()) == true) {
                termList.add(term);
            }

            req = elasticClient.prepareSearch("rplus-dict")
                    .setTypes("cities")
                    .setSearchType(SearchType.DFS_QUERY_THEN_FETCH);
            req.setQuery(QueryBuilders.termQuery ("name", term.toLowerCase()));
            response = req.execute().actionGet();
            if (response.getHits().getTotalHits() > 0) {
                termList.add(term);
                continue;
            }

            req = elasticClient.prepareSearch("rplus-dict")
                    .setTypes("streets")
                    .setSearchType(SearchType.DFS_QUERY_THEN_FETCH);
            // если нашил город добавить условие
            req.setQuery(QueryBuilders.termQuery ("name", term.toLowerCase()));
            response = req.execute().actionGet();
            if (response.getHits().getTotalHits() > 0) {
                termList.add(term);
                after_street = true;
                continue;
            }

            if (after_street) {
                if (regExTest("\\d+", term)) {
                    termList.add(term);
                    after_street = false;
                }
            }
            // if term in houselist
            //termMap.put("house", term);
        }

        return termList;
    }

    public List<Offer> list(int page, int perPage, Map<String, Integer> filter, String searchQuery) {
        List<Offer> offerList = new LinkedList<>();

        /*
        for (Offer o : ds.find(Offer.class).limit(perPage).offset(page * perPage)) {
            result.add(o);
        }
        */
        // client.prepareSearch("index1", "index2").setTypes("type1", "type2")
        // .setQuery(QueryBuilders.termQuery("multi", "test"))                 // Query
        // .setPostFilter(QueryBuilders.rangeQuery("age").from(12).to(18))     // Filter

        Map<String, String> queryParts = processQuery(searchQuery);

        List<FilterObject> filters = parseQuery(searchQuery);
        logger.info(gson.toJson(filters));

        List addressParts = getAddress(searchQuery);
        logger.info("adr parts");
        logger.info(gson.toJson(addressParts));

        SearchRequestBuilder req = elasticClient.prepareSearch("rplus-index")
                .setTypes("offers")
                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                .setFrom(page * perPage).setSize(perPage);


        for (FilterObject fo: filters) {
            if (fo.exactVal != null) {
                req.setPostFilter(
                        QueryBuilders.termQuery(fo.fieldName, fo.exactVal));
            } else {
                if (fo.lowerVal != null) {
                    req.setPostFilter(
                            QueryBuilders.rangeQuery(fo.fieldName).gte(fo.lowerVal));
                }
                if (fo.upperVal != null) {
                    req.setPostFilter(
                            QueryBuilders.rangeQuery(fo.fieldName).lte(fo.upperVal));
                }
            }
        }

        if (filter != null) {
            if (filter.get("state") != null) {
                req.setPostFilter(
                    QueryBuilders.termQuery("state_code", filter.get("state"))
                );
            }
            if (filter.get("agent") != null) {
                //req.setPostFilter(
                //    QueryBuilders.termQuery("agent", filter.get("agent"));
                //)
            }
            if (filter.get("tag") != null) {
                //QueryBuilders.termQuery("tag", filter.get("tag"));
            }
            if (filter.get("depth") != null) {
                // calc date
                // filter.get("depth")
                //QueryBuilders.rangeQuery("last_seen_date").from(from_date);
            }
        }

        if (addressParts.size() > 0) {
            req.setQuery(QueryBuilders.matchPhraseQuery("address", String.join(" ", addressParts)));
        }

        if (searchQuery.length() > 0) {
            //req.setQuery(QueryBuilders.matchPhraseQuery("source_media_text", searchQuery));
            //req.setQuery(QueryBuilders.)
        }

        SearchResponse response = req.execute().actionGet();

        for (SearchHit sh: response.getHits()) {
            Offer o = gson.fromJson(sh.getSourceAsString(), Offer.class);
            o.id = sh.getId();
            offerList.add(o);
        }

        return offerList;
    }

    public Offer get(String id) {
        this.logger.info("get");

        GetResponse response = elasticClient.prepareGet("rplus-index", "offers", id).get();
        Offer offer = gson.fromJson(response.getSourceAsString(), Offer.class);
        offer.id = response.getId();

        return offer;
    }

    public Offer update(String id, String body) throws Exception {
        this.logger.info("update");
        this.logger.info(body);

        Offer tOffer = gson.fromJson(body, Offer.class);
        tOffer.GenerateTags();

        UpdateRequest updateRequest = new UpdateRequest("rplus-index", "offers", id).doc(gson.toJson(tOffer));
        UpdateResponse updateResponse = elasticClient.update(updateRequest).get();

        GetResponse response = elasticClient.prepareGet("rplus-index", "offers", id).get();
        Offer offer = gson.fromJson(response.getSourceAsString(), Offer.class);
        offer.id = response.getId();

        return offer;
    }

    public Offer create(String body) throws Exception {
        this.logger.info("create");

        Offer tOffer = gson.fromJson(body, Offer.class);
        tOffer.GenerateTags();

        IndexResponse idxResponse = elasticClient.prepareIndex("rplus-index", "offers").setSource(gson.toJson(tOffer)).execute().actionGet();
        GetResponse response = elasticClient.prepareGet("rplus-index", "offers", idxResponse.getId()).get();
        Offer offer = gson.fromJson(response.getSourceAsString(), Offer.class);
        offer.id = response.getId();

        return offer;
    }

    public Offer delete(String id) {
        throw new NotImplementedException();
    }
}
