package hibernate.entity;
/**
 * Created by Aleksandr on 07.11.16.
 */

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

import static utils.CommonUtils.getUnixTimestamp;


@Entity
public class Person {

    @Id
    @GeneratedValue
    @Getter
    @Setter
    private Long id;

    @Getter
    @Setter
    private String name;

    @Getter
    @Setter
    @Column(nullable = false)
    private String phones[];

    @Getter
    @Setter
    @Column(nullable = false)
    private String emails[];

    @Getter
    @Setter
    private String description;

    @Getter
    @Setter
    private Long addDate;

    @Getter
    @Setter
    private Long changeDate;

    @Getter
    @Setter
    private Long organisationId;

    @Getter
    @Setter
    @ManyToOne
    @JoinColumn(name = "organisation_id", foreignKey = @ForeignKey(name = "ORGANISATION_ID_FK"))
    private Organisation organisation;

    @Getter
    @Setter
    private Long userId;

    @Getter
    @Setter
    @ManyToOne
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "USER_ID_FK"))
    private User user;

    @Getter
    @Setter
    private Long accountId;


    @Getter
    @Setter
    private String typeCode_n;

    @Getter
    @Setter
    private String fullName_n;

    @Getter
    @Setter
    private String region_n;
    @Getter
    @Setter
    private String city_n;
    @Getter
    @Setter
    private String area_n;
    @Getter
    @Setter
    private String admArea_n;
    @Getter
    @Setter
    private String street_n;
    @Getter
    @Setter
    private String house_n;
    @Getter
    @Setter
    private String housing_n;
    @Getter
    @Setter
    private String apartment_n;


    @Getter
    @Setter
    private String officePhone_n;
    @Getter
    @Setter
    private String homePhone_n;
    @Getter
    @Setter
    private String cellPhone_n;
    @Getter
    @Setter
    private String fax_n;
    @Getter
    @Setter
    private String mainPhone_n;
    @Getter
    @Setter
    private String otherPhone_n;

    @Getter
    @Setter
    private String workEmail_n;
    @Getter
    @Setter
    private String mainEmail_n;

    @Getter
    @Setter
    private String webSite_n;

    @Getter
    @Setter
    @ManyToOne
    @JoinColumn(name = "organisation_n_id", foreignKey = @ForeignKey(name = "ORGANISATION_N_ID_FK"))
    private Organisation organisation_n;

    @Getter
    @Setter
    private String positionCode_n;


    @Getter
    @Setter
    @ManyToOne
    @JoinColumn(name = "agent_id", foreignKey = @ForeignKey(name = "AGENT_ID_FK"))
    private User agent_n;


    @Getter
    @Setter
    private String contract_n;

    @Getter
    @Setter
    private String stateCode_n;

    @Getter
    @Setter
    private String sourceCode_n;


    @PreUpdate
    @PrePersist
    void preInsert() {
        if (getId() == null) {
            setAddDate(getUnixTimestamp());
        }
        setChangeDate(getUnixTimestamp());

        if (getPhones() == null) {
            setPhones(new String[0]);
        }

        if (getEmails() == null) {
            setEmails(new String[0]);
        }
    }
}
