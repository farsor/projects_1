package com.neech.source.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Sources {
	
	//source/entry variables
	@Id
	@GeneratedValue
	@Column(name="id")
	private int id;
	@Column(name="collection_name")
	private String collection;
	@Column(name="source_number")
	private int sourceNumber;											//current source number
	@Column(name="source_call_number")
	private String callNumber;							//call number for current source, indicated by bold text
	@Column(name="source_author")
	private String author;						//
	@Column(name="source_title")
	private String title;							//
	@Column(name="source_inscription")
	private String inscription;
	@Column(name="source_description")
	private String description;							//description of current source, containing all details that cannot be parsed
	
	public Sources(){
		
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getCollection() {
		return collection;
	}
	public void setCollection(String collection) {
		this.collection = collection;
	}
	public int getSourceNumber() {
		return sourceNumber;
	}
	public void setSourceNumber(int sourceNumber) {
		this.sourceNumber = sourceNumber;
	}
	public String getCallNumber() {
		return callNumber;
	}
	public void setCallNumber(String callNumber) {
		this.callNumber = callNumber;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getInscription() {
		return inscription;
	}
	public void setInscription(String inscription) {
		this.inscription = inscription;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String toString() {
		return "Sources [id=" + id + ", collection=" + collection + ", sourceNumber=" + sourceNumber + ", callNumber="
				+ callNumber + ", author=" + author + ", title=" + title + ", inscription=" + inscription
				+ ", description=" + description + "]";
	}
	
	
	
	
	
	
}
