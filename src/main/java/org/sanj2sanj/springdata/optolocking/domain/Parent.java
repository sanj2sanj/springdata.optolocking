package org.sanj2sanj.springdata.optolocking.domain;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorValue("Parent")
public class Parent extends Matchable {

    String firstName;
    String lastName;

    public Parent() {

    }

    public Parent(String string, String string2) {
        this.firstName = string;
        this.lastName = string2;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

}
