package auxclass;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public class EmailBlock {
    @Getter
    @Setter
    private String work;
    @Getter
    @Setter
    private String main;

    public List<String> getAsList() {
        List<String> result = new ArrayList<>();

        if (work != null) result.add(work);
        if (main != null) result.add(main);

        return result;
    }
}
