package hibernate.entity;
/**
 * Created by Aleksandr on 07.11.16.
 */

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

import static utils.CommonUtils.getUnixTimestamp;



@Entity
public class User {

    public enum Role {
        AGENT, MANAGER, TOP;

        public static boolean contains(String s)
        {
            for(Role r: values())
                if (r.name().equals(s))
                    return true;
            return false;
        }
    }

    @Id
    @GeneratedValue
    @Getter
    @Setter
    private Long id;

    @Getter
    @Setter
    @Column(nullable = false)
    private String login;

    @Getter
    @Setter
    @Column(nullable = false)
    private String password;


    @Getter
    @Setter
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    @Getter
    @Setter
    private String name;

    @Getter
    @Setter
    private Integer superiorId;

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
    private Long addDate;

    @Getter
    @Setter
    private Long changeDate;

    @Getter
    @Setter
    private Long accountId;


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
    private String contract_n;

    @Getter
    @Setter
    private Long recruitmentDate_n;
    @Getter
    @Setter
    private Long dismissalDate_n;

    @Getter
    @Setter
    private String statusCode_n;

    @Getter
    @Setter
    private String positionCode_n;

    @Getter
    @Setter
    private String departmentCode_n;

    @Getter
    @Setter
    private String office_n;


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