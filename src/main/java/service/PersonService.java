package service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import entity.Organisation;
import entity.Person;
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
public class PersonService {
    Logger logger = LoggerFactory.getLogger(PersonService.class);

    private final Client elasticClient;
    private final String E_INDEX = "rplus-index";
    private final String E_TYPE = "persons";

    Gson gson = new GsonBuilder().create();

    public PersonService(Client elasticClient) {
        this.elasticClient = elasticClient;
    }

    private Organisation getOrganisationById(String id) {
        GetResponse response = elasticClient.prepareGet(E_INDEX, "organisations", id).get();
        Organisation org = gson.fromJson(response.getSourceAsString(), Organisation.class);
        org.id = response.getId();

        return org;
    }

    public List<Person> list(int page, int perPage, String organisationId, String searchQuery) {
        logger.info("list");

        List<Person> orgList = new LinkedList<>();

        SearchRequestBuilder req = elasticClient.prepareSearch(E_INDEX)
                .setTypes(E_TYPE)
                .setSearchType(SearchType.DEFAULT)
                .setFrom(page * perPage).setSize(perPage);

        if (organisationId.length() > 0) {
            logger.info("org_id - " + organisationId);
            req.setQuery(QueryBuilders.matchQuery("organisation_id", organisationId));
        }

        if (searchQuery.length() > 0) {
            logger.info("s_query - " + searchQuery);
            req.setQuery(QueryBuilders.prefixQuery("_all", searchQuery));
        }

        SearchResponse response = req.execute().actionGet();

        for (SearchHit sh: response.getHits()) {
            Person person = gson.fromJson(sh.getSourceAsString(), Person.class);
            person.id = sh.getId();

            if (person.organisation_id != null) {
                Organisation org = getOrganisationById(person.organisation_id);
                person.organisation_name = org.name;
            }

            orgList.add(person);
        }

        return orgList;
    }

    public Person get(String id) {
        this.logger.info("get");

        GetResponse response = elasticClient.prepareGet(E_INDEX, E_TYPE, id).get();
        Person person = gson.fromJson(response.getSourceAsString(), Person.class);
        person.id = response.getId();

        if (person.organisation_id != null) {
            Organisation org = getOrganisationById(person.organisation_id);
            person.organisation_name = org.name;
        }

        return person;
    }

    public Person update(String id, String body) throws Exception {
        this.logger.info("update");

        Person tOrg = gson.fromJson(body, Person.class);
        //t_offer.GenerateTags();

        UpdateRequest updateRequest = new UpdateRequest(E_INDEX, E_TYPE, id).doc(gson.toJson(tOrg));
        UpdateResponse updateResponse = elasticClient.update(updateRequest).get();

        GetResponse response = elasticClient.prepareGet(E_INDEX, E_TYPE, id).get();
        Person person = gson.fromJson(response.getSourceAsString(), Person.class);
        person.id = response.getId();

        if (person.organisation_id != null) {
            Organisation org = getOrganisationById(person.organisation_id);
            person.organisation_name = org.name;
        }

        return person;
    }

    public Person create(String body) throws Exception {
        this.logger.info("create");

        Person tOrg = gson.fromJson(body, Person.class);

        IndexResponse idxResponse = elasticClient.prepareIndex(E_INDEX, E_TYPE).setSource(gson.toJson(tOrg)).execute().actionGet();
        GetResponse response = elasticClient.prepareGet(E_INDEX, E_TYPE, idxResponse.getId()).get();
        Person person = gson.fromJson(response.getSourceAsString(), Person.class);
        person.id = response.getId();

        if (person.organisation_id != null) {
            Organisation org = getOrganisationById(person.organisation_id);
            person.organisation_name = org.name;
        }

        return person;
    }

    public Person delete(String id) {
        throw new NotImplementedException();
    }
}
