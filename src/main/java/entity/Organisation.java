package entity;
/**
 * Created by Aleksandr on 07.11.16.
 */

import auxclass.EmailBlock;
import auxclass.FullAddress;
import auxclass.PhoneBlock;
import auxclass.Requisites;
import lombok.Getter;
import lombok.Setter;


import static utils.CommonUtils.getUnixTimestamp;


public class Organisation {

    @Getter
    @Setter
    private Long id;
    @Getter
    @Setter
    private Long accountId;

    @Getter
    @Setter
    private String type;
    @Getter
    @Setter
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
    private String typeCode;

    @Getter
    @Setter
    private String area;

    @Getter
    @Setter
    private FullAddress fullAddress;

    @Getter
    @Setter
    private PhoneBlock phoneBlock;

    @Getter
    @Setter
    private EmailBlock emailBlock;

    @Getter
    @Setter
    private String webSite;

    @Getter
    @Setter
    private Requisites requisites;


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
    private Long headId;

    @Getter
    @Setter
    private Long contactId;

    @Getter
    @Setter
    private String contract;

    @Getter
    @Setter
    private String stateCode;

    @Getter
    @Setter
    private String sourceCode;

    @Getter
    @Setter
    private User agent;

    @Getter
    @Setter
    private Long agentId;


    void preIndex() {
        if (getId() == null) {
            setAddDate(getUnixTimestamp());
        }
        setChangeDate(getUnixTimestamp());
    }
}
