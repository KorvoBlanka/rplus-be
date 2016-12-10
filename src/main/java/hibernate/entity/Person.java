package hibernate.entity;
/**
 * Created by Aleksandr on 07.11.16.
 */

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

import static utils.CommonUtils.getUnixTimestamp;


@Entity
public class Person {

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
    @Column(nullable = false)
    private String phones[];

    @Getter
    @Setter
    @Column(nullable = false)
    private String emails[];

    @Getter
    @Setter
    private String description;

    @Getter
    @Setter
    private Long addDate;

    @Getter
    @Setter
    private Long changeDate;

    @Getter
    @Setter
    private Long organisationId;

    @Getter
    @Setter
    @ManyToOne
    @JoinColumn(name = "organisation_id", foreignKey = @ForeignKey(name = "ORGANISATION_ID_FK"))
    private Organisation organisation;

    @Getter
    @Setter
    private Long userId;

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

        if (getPhones() == null) {
            setPhones(new String[0]);
        }

        if (getEmails() == null) {
            setEmails(new String[0]);
        }
    }
}
