package hibernate.entity;
/**
 * Created by Aleksandr on 07.11.16.
 */

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

import static utils.CommonUtils.getUnixTimestamp;



@Entity
public class User {

    public enum Role {
        AGENT, MANAGER, TOP;

        public static boolean contains(String s)
        {
            for(Role r: values())
                if (r.name().equals(s))
                    return true;
            return false;
        }
    }

    @Id
    @GeneratedValue
    @Getter
    @Setter
    private Long id;

    @Getter
    @Setter
    @Column(nullable = false)
    private String login;

    @Getter
    @Setter
    @Column(nullable = false)
    private String password;


    @Getter
    @Setter
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    @Getter
    @Setter
    private String name;

    @Getter
    @Setter
    private Integer superiorId;

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
    private Long addDate;

    @Getter
    @Setter
    private Long changeDate;

    @Getter
    @Setter
    private Long accountId;


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