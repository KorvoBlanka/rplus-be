package entity;
/**
 * Created by Aleksandr on 09.11.16.
 */

import auxclass.FullAddress;
import auxclass.ImportOffer;
import auxclass.Rating;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;


import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static utils.CommonUtils.getUnixTimestamp;


@EqualsAndHashCode(exclude={"agent", "person", "openDate"}, doNotUseGetters = true)
public class Offer {

    @Getter
    @Setter
    private Long id;
    @Getter
    @Setter
    private Long accountId;

    @Getter
    @Setter
    private String stateCode;

    @Getter
    @Setter
    private String stageCode;

    @Getter
    @Setter
    private FullAddress fullAddress;

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
    private Integer roomsOfferCount;

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
    private String workInfo;

    @Getter
    @Setter
    private String description;
    @Getter
    @Setter
    private String sourceMedia;
    @Getter
    @Setter
    private String sourceUrl;
    @Getter
    @Setter
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
    private Long assignDate;
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
    private Long agentId;

    @Getter
    @Setter
    private User agent;


    @Getter
    @Setter
    private Long personId;

    @Getter
    @Setter
    private Person person;

    @Getter
    @Setter
    private Double locationLat;

    @Getter
    @Setter
    private Double locationLon;

    @Getter
    @Setter
    private String[] photoUrl;


    // new stuff

    @Getter
    @Setter
    private String  sourceCode;

    @Getter
    @Setter
    private String offerTypeCode;

    @Getter
    @Setter
    private String typeCode;

    @Getter
    @Setter
    private String settlement;

    @Getter
    @Setter
    private Boolean newBuilding;

    @Getter
    @Setter
    private String objectStage;

    @Getter
    @Setter
    private String buildYear;

    @Getter
    @Setter
    private String houseType;
    @Getter
    @Setter
    private String houseMaterial;
    @Getter
    @Setter
    private Integer roomsCount;

    @Getter
    @Setter
    private String roomsType;

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
    private Float squareLandType;

    @Getter
    @Setter
    private Boolean balcony;
    @Getter
    @Setter
    private Boolean loggia;

    @Getter
    @Setter
    private String bathroom;

    @Getter
    @Setter
    private String condition;

    @Getter
    @Setter
    private Float price;
    @Getter
    @Setter
    private Float comission;
    @Getter
    @Setter
    private Float comissionPerc;

    @Getter
    @Setter
    private Float distance;

    @Getter
    @Setter
    private Boolean guard;

    @Getter
    @Setter
    private Boolean waterSupply;

    @Getter
    @Setter
    private Boolean gasification;
    @Getter
    @Setter
    private Boolean electrification;
    @Getter
    @Setter
    private Boolean sewerage;
    @Getter
    @Setter
    private Boolean centralHeating;
    @Getter
    @Setter
    private Boolean lift;
    @Getter
    @Setter
    private Boolean parking;

    @Getter
    @Setter
    private String landPurpose;
    @Getter
    @Setter
    private String objectName;
    @Getter
    @Setter
    private String buildingType;
    @Getter
    @Setter
    private String buildingClass;

    @Getter
    @Setter
    private Float сeilingHeight;


    @Getter
    @Setter
    private String contractStr;

    // ratings
    @Getter
    @Setter
    private Rating locRating;

    @Getter
    @Setter
    private Rating offerRaiting;

    @Getter
    @Setter
    private String tag;


    @Getter
    @Setter
    private Float ceilingHeight;


    public void preIndex() {

        if (getAddDate() == null) {
            setAddDate(getUnixTimestamp());
        }

        /*
        if (this.getPersonId() != null) {
            Person p = em.find(Person.class, this.getPersonId());
            this.setPerson(p);
        }

        if (this.getAgentId() != null) {
            User agent = em.find(User.class, this.getAgentId());
            this.setAgent(agent);
        }
        */
    }

    public static Offer fromImportOffer(ImportOffer io) {

        Offer o = new Offer();

        o.typeCode = io.type_code;

        o.offerTypeCode = io.offer_type_code;

        o.fullAddress = new FullAddress();
        o.fullAddress.setCity(io.locality);
        o.fullAddress.setStreet(io.address);
        o.fullAddress.setHouse(io.house_num);
        o.fullAddress.setApartment(io.ap_num);

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

        // get from import object
        o.addDate = 0L;
        o.lastSeenDate = 0L;


        o.locationLat = 0.0;
        o.locationLon = 0.0;

        o.photoUrl = io.photo_url;

        o.person = new Person();
        //o.person.setPhones(io.owner_phones);
        //o.person.setName(io.mediator_company);
        //o.person.setAccountId(); ???

        o.locationLat = io.location_lat;
        o.locationLon = io.location_lon;
        /*
        io.owner_phones;
        io.mediator_company;
        */

        return o;
    }
}
