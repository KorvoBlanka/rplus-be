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
    private String stage;

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
    private Float leaseDeposite;

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
    public Long userId;

    @Getter
    @Setter
    @ManyToOne
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "USER_ID_FK"))
    private User user;


    @Getter
    @Setter
    public Long personId;

    @Getter
    @Setter
    @ManyToOne
    @JoinColumn(name = "person_id", foreignKey = @ForeignKey(name = "PERSON_ID_FK"))
    private Person person;
    //public GeoLocation location;
    //photos

    // tags

    @PreUpdate
    @PrePersist
    void preInsert() {
        if (getId() == null) {
            setAddDate(getUnixTimestamp());
        }
        setChangeDate(getUnixTimestamp());

        /*
        if (this.getPersonId() != null) {
            Person pers = em.find(Person.class, offer.getPersonId());
            offer.setPerson(pers);
        }

        if (this.getUserId() != null) {
            User usr = em.find(User.class, offer.getUserId());
            offer.setUser(usr);
        }
        */
    }
}
