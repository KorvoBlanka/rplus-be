package hibernate.entity;

/**
 * Created by Aleksandr on 20.01.17.
 */

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;


@Entity
public class Account {

    @Id
    @GeneratedValue
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
