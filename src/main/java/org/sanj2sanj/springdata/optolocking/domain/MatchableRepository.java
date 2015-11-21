package org.sanj2sanj.springdata.optolocking.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MatchableRepository extends JpaRepository<Matchable, Long> {
    List<Matchable> findByText(String text);
}