package com.datacancha.agent.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.datacancha.agent.entity.ClubProfile;

@Repository
public interface ClubProfileRepository extends JpaRepository<ClubProfile, Long> {
}