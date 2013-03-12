package br.ufla.ri.model;

import br.ufla.ri.util.Calculate;

public class Document implements Comparable<Document>{

	private String key;
	private Integer quantity;
	private Integer rn;
	private Double termFrequency;
	private Double idf;
	private Double tfIDF;
	
	public String getKey() {

		return key;
	}
	
	public void setKey(String key) {
		this.key = key;
	}
	
	public Integer getQuantity() {
		return quantity;
	}
	
	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public Integer getRn() {
		return rn;
	}

	public void setRn(Integer rn) {
		this.rn = rn;
	}

	public Double getTermFrequency() {
		setTermFrequency(Calculate.termFrequency(this.quantity));
		return termFrequency;
	}

	public void setTermFrequency(Double termFrequency) {
		this.termFrequency = termFrequency;
	}
	
	public Double getIdf() {
		return idf;
	}

	public void setIdf(Double idf) {
		this.idf = idf;
	}

	public Double getTfIDF() {
		return tfIDF;
	}

	public void setTfIDF(Double tfIDF) {
		this.tfIDF = tfIDF;
	}
	
	@Override
	public String toString() {
		return "RN: " + this.rn + " TF-IDF: " + this.idf;
	}

//	@Override
	public int compareTo(Document o) {
		return this.tfIDF.compareTo(o.getTfIDF());
	}

}