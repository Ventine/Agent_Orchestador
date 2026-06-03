package com.datacancha.agent.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.datacancha.agent.entity.MatchAnalysis;

public interface MatchAnalysisRepository extends JpaRepository<MatchAnalysis, Long> {}