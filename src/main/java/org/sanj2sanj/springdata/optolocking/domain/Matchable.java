package org.sanj2sanj.springdata.optolocking.domain;

import org.sanj2sanj.springdata.optolocking.AuditableUser;
import org.springframework.data.jpa.domain.AbstractAuditable;

import javax.persistence.*;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorValue("Matchable")
public class Matchable extends AbstractAuditable<AuditableUser, Long> {

    private static final long serialVersionUID = 1L;

    @Version
    Long version;

    Long m = Long.valueOf(1);

    String text;

    public Matchable() {
    }

    public Matchable(String text) {
        this.text = text;
    }

    public Matchable(String string, String string2) {
        this();
        text = string2;
    }

    public Long getM() {
        return m;
    }

    public void setM(Long m) {
        this.m = m;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public String getLastname() {
        return text;
    }

    public void setLastname(String lastname) {
        this.text = lastname;
    }

    public void incrementMatchAttempts() {
        m++;
    }

}
