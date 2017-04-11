package utils;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by owl on 4/11/17.
 */
public class ParseResult {
    public String query;
    public List<FilterObject> filterList;

    ParseResult() {
        filterList = new LinkedList<>();
    }
}
