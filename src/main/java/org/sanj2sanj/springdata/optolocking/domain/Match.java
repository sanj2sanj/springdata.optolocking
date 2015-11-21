package org.sanj2sanj.springdata.optolocking.domain;

import com.google.common.collect.Lists;
import org.sanj2sanj.springdata.optolocking.AuditableUser;
import org.springframework.data.jpa.domain.AbstractAuditable;

import javax.persistence.*;
import java.util.List;


@Entity
@SequenceGenerator(name = "Matchable_SEQ_GEN", sequenceName = "Matchable_SEQ_GEN", initialValue = 0, allocationSize = 1000)
public class Match extends AbstractAuditable<AuditableUser, Long> {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(generator = "Matchable_SEQ_GEN")
    Long id;
    @ManyToMany
    List<Matchable> matchables = Lists.newArrayList();

    @Override
    public String toString() {
        return "Match [id=" + id + ", matchables=" + matchables + ", size()="
                + size() + ", getCreatedBy()=" + getCreatedBy()
                + ", getCreatedDate()=" + getCreatedDate()
                + ", getLastModifiedBy()=" + getLastModifiedBy()
                + ", getLastModifiedDate()=" + getLastModifiedDate()
                + ", getId()=" + getId() + ", isNew()=" + isNew()
                + ", toString()=" + super.toString() + ", hashCode()="
                + hashCode() + ", getClass()=" + getClass() + "]";
    }

    public int size() {
        return matchables.size();
    }

    public boolean add(Matchable e) {
        return matchables.add(e);
    }

    public boolean addAll(List<? extends Matchable> c) {
        return matchables.addAll(c);
    }

    public void addAll(Iterable<Matchable> all) {
        for (Matchable m : all) {
            add(m);
        }
    }
}
