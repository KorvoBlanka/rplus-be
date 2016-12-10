package hibernate.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * Created by Aleksandr on 09.11.16.
 */

@Entity
public class Request {

    @Id
    @GeneratedValue
    @Getter
    @Setter
    public Long id;

    @Getter
    @Setter
    public String agentId;
    @Getter
    @Setter
    public String personId;

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
    public String searchArea;

}
