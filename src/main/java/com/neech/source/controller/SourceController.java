package com.neech.source.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import com.neech.source.model.Sources;
import com.neech.source.repository.SourceRepo;

@RestController
public class SourceController {
	
	@Autowired
	SourceRepo repo;
	
	@RequestMapping("/")
	public String home() {
		
		return "home.html";
		
	}
	
	@GetMapping(value="/sources")
	public List<Sources> getAll(){
		return repo.findAll();
	}
	
	@RequestMapping("/getSources")
	public ModelAndView getSources(@RequestParam int id) {
		System.out.println("hello");		
		ModelAndView mv = new ModelAndView("viewsource.html");
		Sources sources =  repo.findById(id).orElse(new Sources());
//		System.out.println(repo.findByTech("Java"));
//		System.out.println(repo.findByAidGreaterThan(102));
//		System.out.println(repo.findByTechSorted("Java"));

		mv.addObject(sources);
		
//		mv.addObject("id", sources.getId());
//		mv.addObject("collection", sources.getCollection());
//		mv.addObject("sourceNumber", sources.getSourceNumber());
//		mv.addObject("callNumber", sources.getCallNumber());
//		mv.addObject("author", sources.getAuthor());
//		mv.addObject("title", sources.getTitle());
//		mv.addObject("inscription", sources.getInscription());
//		mv.addObject("description", sources.getDescription());
		return mv;
		
	}

}
