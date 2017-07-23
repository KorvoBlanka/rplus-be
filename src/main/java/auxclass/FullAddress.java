package auxclass;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public class FullAddress {
    @Getter
    @Setter
    private String region;
    @Getter
    @Setter
    private String admArea;
    @Getter
    @Setter
    private String city;
    @Getter
    @Setter
    private String street;
    @Getter
    @Setter
    private String house;
    @Getter
    @Setter
    private String housing;
    @Getter
    @Setter
    private String apartment;

    public String getAsString() {
        String result = "";

        List<String> t = new ArrayList<>();

        if (region != null) t.add(region);
        if (admArea != null) t.add(admArea);
        if (city != null) t.add(city);
        if (street != null) t.add(street);
        if (house != null) t.add(house);
        if (housing != null) t.add(housing);
        if (apartment != null) t.add(apartment);

        return result;
    }
}
