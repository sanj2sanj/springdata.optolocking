package org.sanj2sanj.springdata.optolocking.domain;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorValue("Child")
public class Child extends Parent {

    public Child() {
    }

    public Child(String string, String string2) {
        this.firstName = string;
        this.lastName = string2;
    }

}
