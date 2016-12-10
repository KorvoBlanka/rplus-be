package utils;

/**
 * Created by Aleksandr on 08.11.16.
 */


public class FilterObject {

    public String fieldName;

    public Integer exactVal;
    public Integer lowerVal;
    public Integer upperVal;

    public FilterObject (String fieldName, Integer exactVal) {

        this.fieldName = fieldName;
        this.exactVal = exactVal;
        this.lowerVal = null;
        this.upperVal = null;

    }

    public FilterObject (String fieldName, Integer lowerVal, Integer upperVal) {

        this.fieldName = fieldName;
        this.exactVal = exactVal;
        this.lowerVal = lowerVal;
        this.upperVal = upperVal;

    }

}
