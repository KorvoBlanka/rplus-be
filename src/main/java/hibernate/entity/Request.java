package hibernate.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

import static utils.CommonUtils.getUnixTimestamp;

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


    @Getter
    @Setter
    public Long personId;

    @Getter
    @Setter
    @ManyToOne
    @JoinColumn(name = "person_id", foreignKey = @ForeignKey(name = "PERSON_ID_FK"))
    private Person person;

    @Getter
    @Setter
    public String agentId;

    @Getter
    @Setter
    @ManyToOne
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "USER_ID_FK"))
    private User user;

    @PreUpdate
    @PrePersist
    void preInsert() {
        if (getId() == null) {
            setAddDate(getUnixTimestamp());
        }
        setChangeDate(getUnixTimestamp());
    }
}
