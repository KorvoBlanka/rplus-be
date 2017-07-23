package entity;

/**
 * Created by Aleksandr on 20.01.17.
 */

import lombok.Getter;
import lombok.Setter;


public class Account {

    @Getter
    @Setter
    private Long id;

    @Getter
    @Setter
    private String name;

    @Getter
    @Setter
    private String location;

}
