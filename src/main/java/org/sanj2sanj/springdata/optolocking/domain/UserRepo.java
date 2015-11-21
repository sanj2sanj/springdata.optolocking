package org.sanj2sanj.springdata.optolocking.domain;

import org.sanj2sanj.springdata.optolocking.AuditableUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepo extends JpaRepository<AuditableUser, Long> {

}
