package entity;

import auxclass.GeoPoint;
import lombok.Getter;
import lombok.Setter;


import static utils.CommonUtils.getUnixTimestamp;

/**
 * Created by Aleksandr on 09.11.16.
 */


public class Request {

    @Getter
    @Setter
    public Long id;
    @Getter
    @Setter
    private Long accountId;

    @Getter
    @Setter
    public Long agentId;
    @Getter
    @Setter
    public Long personId;


    @Getter
    @Setter
    private String stateCode;

    @Getter
    @Setter
    private String stageCode;

    @Getter
    @Setter
    public String offerTypeCode;


    @Getter
    @Setter
    public String request;

    @Getter
    @Setter
    public Long addDate;
    @Getter
    @Setter
    public Long changeDate;

    @Getter
    @Setter
    public GeoPoint[] searchArea;

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
