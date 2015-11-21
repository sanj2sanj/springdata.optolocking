package org.sanj2sanj.springdata.optolocking;

import org.sanj2sanj.springdata.optolocking.domain.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.AuditorAware;

public class AuditorAwareImpl implements AuditorAware<AuditableUser> {

    public AuditableUser user;

    @Autowired
    UserRepo userRepo;

    @Override
    public AuditableUser getCurrentAuditor() {
        return user;
    }

}
