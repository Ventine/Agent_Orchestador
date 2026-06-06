package com.datacancha.agent.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.datacancha.agent.entity.ClubFixture;

@Repository
public interface ClubFixtureRepository extends JpaRepository<ClubFixture, Long> {
}