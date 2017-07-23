package auxclass;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public class PhoneBlock {
    @Getter
    @Setter
    private String office;
    @Getter
    @Setter
    private String home;
    @Getter
    @Setter
    private String cellphone;
    @Getter
    @Setter
    private String fax;
    @Getter
    @Setter
    private String main;
    @Getter
    @Setter
    private String other;


    public List<String> getAsList() {
        List<String> result = new ArrayList<>();

        if (office != null) result.add(office);
        if (home != null) result.add(home);
        if (cellphone != null) result.add(cellphone);
        if (fax != null) result.add(fax);
        if (main != null) result.add(main);
        if (other != null) result.add(other);

        return result;
    }
}
