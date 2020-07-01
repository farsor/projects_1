package com.neech.source.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.neech.source.model.Sources;

public interface SourceRepo extends JpaRepository<Sources, Integer> {

}
