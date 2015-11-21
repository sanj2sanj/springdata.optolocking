package org.sanj2sanj.springdata.optolocking;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class AuditableUser {

    public String name = "SANJEEV";
    @Id
    @GeneratedValue
    Long id;

    @Override
    public String toString() {
        return "AuditableUser [id=" + id + ", name=" + name + "]";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
