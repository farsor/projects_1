package com.neech.source.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.neech.source.model.Sources;
import com.neech.source.repository.SourceRepo;

@RestController
@RequestMapping(value="/collections")
public class SourceController {
	
	@Autowired
	SourceRepo repo;
	
	@GetMapping(value="/sources")
	public List<Sources> getAll(){
		return repo.findAll();
	}

}
