package entity;
/**
 * Created by Aleksandr on 07.11.16.
 */

import auxclass.EmailBlock;
import auxclass.FullAddress;
import auxclass.PhoneBlock;
import lombok.Getter;
import lombok.Setter;


import static utils.CommonUtils.getUnixTimestamp;


public class Person {

    @Getter
    @Setter
    private Long id;
    @Getter
    @Setter
    private Long accountId;

    @Getter
    @Setter
    private String name;

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
    private Organisation organisation;

    @Getter
    @Setter
    private Long userId;
    @Getter
    @Setter
    private User user;


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
    private String positionCode;

    @Getter
    @Setter
    private Long agentId;
    @Getter
    @Setter
    private User agent;


    @Getter
    @Setter
    private String contract;

    @Getter
    @Setter
    private String stateCode;

    @Getter
    @Setter
    private String sourceCode;


    void preIndex() {
        if (getId() == null) {
            setAddDate(getUnixTimestamp());
        }
        setChangeDate(getUnixTimestamp());
    }
}
