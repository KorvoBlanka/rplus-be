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


public class User {

    @Getter
    @Setter
    private Long id;
    @Getter
    @Setter
    private Long accountId;

    @Getter
    @Setter
    private String login;

    @Getter
    @Setter
    private String password;


    @Getter
    @Setter
    private String role;

    @Getter
    @Setter
    private String name;

    @Getter
    @Setter
    private Long superiorId;

    @Getter
    @Setter
    private Long addDate;

    @Getter
    @Setter
    private Long changeDate;


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
    private String contract;

    @Getter
    @Setter
    private Long recruitmentDate;
    @Getter
    @Setter
    private Long dismissalDate;

    @Getter
    @Setter
    private String statusCode;

    @Getter
    @Setter
    private String positionCode;

    @Getter
    @Setter
    private String departmentCode;

    @Getter
    @Setter
    private String office;

    @Getter
    @Setter
    private String info;


    void preIndex() {
        if (getId() == null) {
            setAddDate(getUnixTimestamp());
        }
        setChangeDate(getUnixTimestamp());

    }
}