package hibernate.entity;
/**
 * Created by Aleksandr on 07.11.16.
 */

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static utils.CommonUtils.getUnixTimestamp;


@Entity
public class Organisation {

    @Id
    @GeneratedValue
    @Getter
    @Setter
    private Long id;

    @Getter
    @Setter
    @Column(nullable = false)
    private String name;

    @Getter
    @Setter
    private String address;

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
    private Long accountId;

    @Getter
    @Setter
    private String typeCode_n;

    @Getter
    @Setter
    private String orgName_n;

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
    private String inn_n;

    @Getter
    @Setter
    private String kpp_n;

    @Getter
    @Setter
    private String cor_n;

    @Getter
    @Setter
    private String bic_n;

    @Getter
    @Setter
    private String other_n;

    /*
    @Getter
    @Setter
    @ManyToOne
    @JoinColumn(name = "head_id", foreignKey = @ForeignKey(name = "HEAD_ID_FK"))
    private Person head_n;


    @Getter
    @Setter
    @ManyToOne
    @JoinColumn(name = "contact_id", foreignKey = @ForeignKey(name = "CONTACT_ID_FK"))
    private Person contact_n;
    */

    @Getter
    @Setter
    private Long head_id_n;

    @Getter
    @Setter
    private Long contact_id_n;

    @Getter
    @Setter
    private String contract_n;

    @Getter
    @Setter
    private String stateCode_n;

    @Getter
    @Setter
    private String sourceCode_n;

    @Getter
    @Setter
    @ManyToOne
    @JoinColumn(name = "agent_id", foreignKey = @ForeignKey(name = "AGENT_ID_FK"))
    private User agent_n;


    @PreUpdate
    @PrePersist
    void preInsert() {
        if (getId() == null) {
            setAddDate(getUnixTimestamp());
        }
        setChangeDate(getUnixTimestamp());
    }
}
