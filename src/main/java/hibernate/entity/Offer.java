package hibernate.entity;
/**
 * Created by Aleksandr on 09.11.16.
 */

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;



import static utils.CommonUtils.getUnixTimestamp;


@Entity
public class Offer {

    @Id
    @GeneratedValue
    @Getter
    @Setter
    private Long id;

    @Getter
    @Setter
    private String stateCode;

    @Getter
    @Setter
    private String stageCode;

    @Getter
    @Setter
    private String typeCode;
    @Getter
    @Setter
    private String offerTypeCode;

    @Getter
    @Setter
    private String locality;
    @Getter
    @Setter
    private String address;
    @Getter
    @Setter
    private String houseNum;
    @Getter
    @Setter
    private String apNum;

    @Getter
    @Setter
    private String district;
    @Getter
    @Setter
    private String poi;

    @Getter
    @Setter
    private Integer houseTypeId;
    @Getter
    @Setter
    private Integer apSchemeId;
    @Getter
    @Setter
    private Integer roomSchemeId;
    @Getter
    @Setter
    private Integer conditionId;
    @Getter
    @Setter
    private Integer balconyId;
    @Getter
    @Setter
    private Integer bathroomId;

    @Getter
    @Setter
    private Integer roomsCount;
    @Getter
    @Setter
    private Integer roomsOfferCount;

    @Getter
    @Setter
    private Integer floor;
    @Getter
    @Setter
    private Integer floorsCount;
    @Getter
    @Setter
    private Integer levelsCount;

    @Getter
    @Setter
    private Float squareTotal;
    @Getter
    @Setter
    private Float squareLiving;
    @Getter
    @Setter
    private Float squareKitchen;
    @Getter
    @Setter
    private Float squareLand;

    @Getter
    @Setter
    private Float ownerPrice;
    @Getter
    @Setter
    private Float agencyPrice;
    @Getter
    @Setter
    private Float leaseDeposit;

    @Getter
    @Setter
    @Column(columnDefinition="TEXT")
    private String workInfo;
    @Getter
    @Setter
    @Column(columnDefinition="TEXT")
    private String description;
    @Getter
    @Setter
    private String sourceMedia;
    @Getter
    @Setter
    private String sourceUrl;
    @Getter
    @Setter
    @Column(columnDefinition="TEXT")
    private String sourceMediaText;

    @Getter
    @Setter
    private Long addDate;
    @Getter
    @Setter
    private Long openDate;
    @Getter
    @Setter
    private Long changeDate;
    @Getter
    @Setter
    private Long deleteDate;
    @Getter
    @Setter
    private Long lastSeenDate;


    @Getter
    @Setter
    private Boolean multylisting;
    @Getter
    @Setter
    private String mlsPriceType;
    @Getter
    @Setter
    private Float mlsPrice;


    @Getter
    @Setter
    public Long agentId;

    @Getter
    @Setter
    @ManyToOne
    @JoinColumn(name = "agent_id", foreignKey = @ForeignKey(name = "AGENT_ID_FK"))
    private User agent;


    @Getter
    @Setter
    public Long personId;

    @Getter
    @Setter
    @ManyToOne
    @JoinColumn(name = "person_id", foreignKey = @ForeignKey(name = "PERSON_ID_FK"))
    private Person person;

    @Getter
    @Setter
    public Double locationLat;

    @Getter
    @Setter
    public Double locationLon;

    @Getter
    @Setter
    @Column(columnDefinition="BLOB")
    public String[] photoUrl;

    // tags

    @Getter
    @Setter
    private Long accountId;

    @PreUpdate
    @PrePersist
    void preInsert() {

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("rplus-be-dev.jpa.hibernate");
        EntityManager em = emf.createEntityManager();

        if (getId() == null) {
            setAddDate(getUnixTimestamp());
        }
        setChangeDate(getUnixTimestamp());

        if (this.getPersonId() != null) {
            Person p = em.find(Person.class, this.getPersonId());
            this.setPerson(p);
        }

        if (this.getAgentId() != null) {
            User agent = em.find(User.class, this.getAgentId());
            this.setAgent(agent);
        }
    }

    public static Offer fromImportOffer(ImportOffer io) {

        Offer o = new Offer();

        o.typeCode = io.type_code;

        o.offerTypeCode = io.offer_type_code;

        o.locality = io.locality;
        o.address = io.address;
        o.houseNum = io.house_num;
        o.apNum = io.ap_num;

        o.district = "";
        o.poi = "";

        o.houseTypeId = io.house_type_id;
        o.apSchemeId = io.ap_scheme_id;
        o.roomSchemeId = io.room_scheme_id;
        o.conditionId = io.condition_id;
        o.balconyId = io.balcony_id;
        o.bathroomId = io.bathroom_id;

        o.roomsCount = io.rooms_count;
        o.roomsOfferCount = io.rooms_offer_count;

        o.floor = io.floor;
        o.floorsCount = io.floors_count;
        o.levelsCount = io.levels_count;

        o.squareTotal = io.square_total;
        o.squareLiving = io.square_living;
        o.squareKitchen = io.square_kitchen;
        o.squareLand = io.square_land;

        o.ownerPrice = io.owner_price;

        o.sourceMedia = io.source_media;
        o.sourceUrl = io.source_url;
        o.sourceMediaText = io.source_media_text;

        o.addDate = 0L;
        o.lastSeenDate = 0L;


        o.locationLat = 0.0;
        o.locationLon = 0.0;

        o.photoUrl = io.photo_url;

        o.person = new Person();
        o.person.setPhones(io.owner_phones);
        o.person.setName(io.mediator_company);

        o.locationLat = io.location_lat;
        o.locationLon = io.location_lon;
        /*
        io.owner_phones;
        io.mediator_company;
        */

        return o;
    }
}
