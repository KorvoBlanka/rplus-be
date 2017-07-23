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

import entity.Person;
import utils.CommonUtils;


public class PersonService {

    private final String E_INDEX = "rplus";
    private final String E_TYPE = "person";
    private final String E_DATAFIELD = "data";

    Logger logger = LoggerFactory.getLogger(PersonService.class);
    private final Client elasticClient;
    Gson gson = new Gson();

    public PersonService (Client elasticClient) {

        this.elasticClient = elasticClient;
    }

    public List<String> check (Person person) {
        List<String> errors = new LinkedList<>();

        //if ((person.getPhones() == null || person.getPhones().length == 0) && (person.getEmails() == null || person.getEmails().length == 0)) errors.add("no phones and no emails given");

        return errors;
    }

    public List<Person> list (Long accountId, int page, int perPage, Long userId, Long organisationId, String searchQuery) {

        logger.info("list");


        List<Person> personList = new ArrayList<>();

        SearchRequestBuilder rb = elasticClient.prepareSearch(E_INDEX)
                .setTypes(E_TYPE)
                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                .setFrom(page * perPage).setSize(perPage);


        BoolQueryBuilder q = QueryBuilders.boolQuery();

        q.must(QueryBuilders.termQuery("accountId", accountId));
        if (userId != null) {
            q.must(QueryBuilders.termQuery("userId", userId));
        }
        if (organisationId != null) {
            q.must(QueryBuilders.termQuery("organisationId", organisationId));
        }


        if (searchQuery != null && searchQuery.length() > 2) {

            q.should(QueryBuilders.matchQuery("name", searchQuery));
            q.should(QueryBuilders.matchQuery("phones", searchQuery));
            q.should(QueryBuilders.matchQuery("emails", searchQuery));
        }

        rb.setQuery(q);

        SearchResponse response = rb.execute().actionGet();

        for (SearchHit sh: response.getHits()) {
            String dataJson = sh.getSourceAsMap().get(E_DATAFIELD).toString();
            personList.add(gson.fromJson(dataJson, Person.class));
        }

        return personList;
    }

    public Person get (long id) {

        this.logger.info("get");

        Person result = null;

        GetResponse response = this.elasticClient.prepareGet(E_INDEX, E_TYPE, Long.toString(id)).get();
        String dataJson = response.getSourceAsMap().get(E_DATAFIELD).toString();
        result = gson.fromJson(dataJson, Person.class);

        return result;
    }

    public Person save (Person person) throws Exception {

        this.logger.info("save");

        indexPerson(person);

        return person;
    }

    public Person delete (long id) {
        return null;
    }


    public void indexPerson(Person person) {

        Map<String, Object> json = new HashMap<String, Object>();

        if (person.getId() == null) {
            person.setId(CommonUtils.getSystemTimestamp());
        }

        json.put("id", person.getId());
        json.put("accountId", person.getAccountId());
        json.put("name", person.getName());

        // filters
        json.put("agentId", person.getUserId());
        json.put("organisationId", person.getOrganisationId());

        List<String> phoneArray = person.getPhoneBlock().getAsList();
        String phones = String.join(" ", phoneArray);
        json.put("phones", phones);

        List<String> mailArray = person.getEmailBlock().getAsList();
        String mails = String.join(" ", mailArray);
        json.put("emails", mails);

        json.put(E_DATAFIELD, gson.toJson(person));

        IndexResponse response = this.elasticClient.prepareIndex(E_INDEX, E_TYPE, Long.toString(person.getId())).setSource(json).get();
    }
}
