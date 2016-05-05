package service; /**
 * Created by owl on 3/23/16.
 */
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import entity.User;
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


public class UserService {
    Logger logger = LoggerFactory.getLogger(UserService.class);

    private final Client elasticClient;
    private final String E_INDEX = "rplus-index";
    private final String E_TYPE = "users";

    Gson gson = new GsonBuilder().create();

    public UserService(Client elasticClient) {
        this.elasticClient = elasticClient;
    }

    public List<User> list(String role, String searchQuery) {
        this.logger.info("list");

        List<User> userList = new LinkedList<>();

        SearchRequestBuilder req = elasticClient.prepareSearch(E_INDEX)
                .setTypes(E_TYPE)
                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH);

        if (role.length() > 0) {
            req.setQuery(QueryBuilders.matchQuery("role", role));
        }

        if (searchQuery.length() > 0) {
            req.setQuery(QueryBuilders.prefixQuery("_all", searchQuery));
        }

        SearchResponse response = req.execute().actionGet();

        for (SearchHit sh: response.getHits()) {
            User u = gson.fromJson(sh.getSourceAsString(), User.class);
            u.id = sh.getId();
            userList.add(u);
        }

        return userList;
    }

    public User get(String id) {
        this.logger.info("get");

        GetResponse response = elasticClient.prepareGet(E_INDEX, E_TYPE, id).get();
        User user = gson.fromJson(response.getSourceAsString(), User.class);
        user.id = response.getId();

        return user;
    }

    public User getByName(String name) {
        this.logger.info("get by name");
        this.logger.info(name);

        User user = null;

        SearchRequestBuilder req = elasticClient.prepareSearch(E_INDEX)
                .setTypes(E_TYPE)
                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                .setQuery(QueryBuilders.termQuery("name", name));

        SearchResponse response = req.execute().actionGet();

        if (response.getHits().getTotalHits() > 0) {
            user = gson.fromJson(response.getHits().getAt(0).getSourceAsString(), User.class);
        }

        return user;
    }

    public User update(String id, String body) throws Exception {
        this.logger.info("update");

        User tOffer = gson.fromJson(body, User.class);
        //t_offer.GenerateTags();

        UpdateRequest updateRequest = new UpdateRequest(E_INDEX, E_TYPE, id).doc(gson.toJson(tOffer));
        UpdateResponse updateResponse = elasticClient.update(updateRequest).get();

        GetResponse response = elasticClient.prepareGet(E_INDEX, E_TYPE, id).get();
        User user = gson.fromJson(response.getSourceAsString(), User.class);
        user.id = response.getId();

        return user;
    }

    public User create(String body) throws Exception {
        this.logger.info("create");

        User tOffer = gson.fromJson(body, User.class);
        //tOffer.GenerateTags();

        IndexResponse idxResponse = elasticClient.prepareIndex(E_INDEX, E_TYPE).setSource(gson.toJson(tOffer)).execute().actionGet();
        GetResponse response = elasticClient.prepareGet(E_INDEX, E_TYPE, idxResponse.getId()).get();
        User user = gson.fromJson(response.getSourceAsString(), User.class);
        user.id = response.getId();

        return user;
    }

    public User delete(String id) {
        throw new NotImplementedException();
    }
}
