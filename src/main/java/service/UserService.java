package service;

import com.google.gson.Gson;
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

import entity.User;
import utils.CommonUtils;


public class UserService {

    private final String E_INDEX = "rplus";
    private final String E_TYPE = "user";
    private final String E_DATAFIELD = "data";

    Logger logger = LoggerFactory.getLogger(UserService.class);
    private final Client elasticClient;
    Gson gson = new Gson();

    public UserService (Client elasticClient) {

        this.elasticClient = elasticClient;
    }

    public List<String> check (User user) {
        // check login, pass, role
        List<String> errors = new LinkedList<>();

        if (user.getLogin() == null || user.getLogin().length() < 4) errors.add("login is null or too short");
        if (user.getPassword() == null || user.getPassword().length() < 4) errors.add("password is null or too short");
        if (user.getRole() == null) {
            errors.add("empty role ");
        }

        return errors;
    }

    public List<User> list (long accountId, int page, int perPage, String role, Long superiorId, String searchQuery) {

        this.logger.info("list");

        List<User> userList = new ArrayList<>();

        SearchRequestBuilder rb = elasticClient.prepareSearch(E_INDEX)
                .setTypes(E_TYPE)
                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                .setFrom(page * perPage).setSize(perPage);


        BoolQueryBuilder q = QueryBuilders.boolQuery();

        q.must(QueryBuilders.termQuery("accountId", accountId));
        if (superiorId != null) {
            q.must(QueryBuilders.termQuery("superiorId", superiorId));
        }
        if (role != null) {
            q.must(QueryBuilders.termQuery("role", role));
        }

        if (searchQuery != null && searchQuery.length() > 0) {

            q.should(QueryBuilders.matchQuery("name", searchQuery));
            //
        }

        rb.setQuery(q);

        SearchResponse response = rb.execute().actionGet();


        for (SearchHit sh: response.getHits()) {
            String dataJson = sh.getSourceAsMap().get(E_DATAFIELD).toString();
            userList.add(gson.fromJson(dataJson, User.class));
        }

        return userList;
    }

    public User get (long id) {

        this.logger.info("get");

        User result = null;

        GetResponse response = this.elasticClient.prepareGet(E_INDEX, E_TYPE, Long.toString(id)).get();
        String dataJson = response.getSourceAsMap().get(E_DATAFIELD).toString();
        result = gson.fromJson(dataJson, User.class);

        return result;
    }

    public User getByLogin (long accountId, String login) {

        this.logger.info("get by login");

        User result = null;


        SearchRequestBuilder rb = elasticClient.prepareSearch(E_INDEX)
                .setTypes(E_TYPE)
                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH);


        BoolQueryBuilder q = QueryBuilders.boolQuery();

        q.must(QueryBuilders.matchQuery("login", login));

        rb.setQuery(q);

        SearchResponse response = rb.execute().actionGet();

        if (response.getHits().getTotalHits() > 0) {
            String dataJson = response.getHits().getAt(0).getSourceAsMap().get(E_DATAFIELD).toString();
            result = gson.fromJson(dataJson, User.class);
        }

        return result;
    }

    public User save (User user) throws Exception {

        this.logger.info("save");

        indexUser(user);

        return user;
    }

    public User delete (long id) {
        return null;
    }


    public void indexUser(User user) {

        Map<String, Object> json = new HashMap<>();

        if (user.getId() == null) {
            user.setId(CommonUtils.getSystemTimestamp());
        }


        json.put("id", user.getId());
        json.put("accountId", user.getAccountId());

        json.put("login", user.getLogin());


        json.put(E_DATAFIELD, gson.toJson(user));


        IndexResponse response = this.elasticClient.prepareIndex(E_INDEX, E_TYPE, Long.toString(user.getId())).setSource(json).get();
    }
}
