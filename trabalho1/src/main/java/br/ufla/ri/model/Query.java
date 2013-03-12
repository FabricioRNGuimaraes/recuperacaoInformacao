package br.ufla.ri.model;

import java.util.Map;

public class Query {

	private String key;
	private Map<String, Integer> queries;
	private Map<Integer, Integer> relevantDocuments;
	
	public String getKey() {
		return key;
	}
	
	public void setKey(String key) {
		this.key = key;
	}
	
	public Map<String, Integer> getQueries() {
		return queries;
	}
	
	public void setQueries(Map<String, Integer> queries) {
		this.queries = queries;
	}
	
	public Map<Integer, Integer> getRelevantDocuments() {
		return relevantDocuments;
	}
	
	public void setRelevantDocuments(Map<Integer, Integer> relevantDocuments) {
		this.relevantDocuments = relevantDocuments;
	} 
	
}