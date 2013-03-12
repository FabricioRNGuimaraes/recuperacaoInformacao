package br.ufla.lucene;

import java.util.List;
import java.util.Set;

/**
 * Classe que representa um objeto extraido do arquivo cfquery</br>
 * Possui as propriedades:</br>
 * <b>qn</b> Numero da query </br>
 * <b>qu</b> Texto da query </br>
 * <b>nr</b> Numero de documentos relevantes aos humanos </br>
 * <b>rd</b> Set de documentos relevantes aos humanos
 */
public class QueryModel {

	private String qn;
	private String qu;
	private String nr;
	private List<Integer> rd;
	
	public String getQn() {
		return qn;
	}
	
	public void setQn(String qn) {
		this.qn = qn;
	}
	
	/**
	 * Texto da query </br>
	 */
	public String getQu() {
		return qu;
	}
	
	/**
	 * Texto da query </br>
	 */
	public void setQu(String qu) {
		this.qu = qu;
	}
	
	public String getNr() {
		return nr;
	}
	
	public void setNr(String nr) {
		this.nr = nr;
	}
	
	public List<Integer> getRd() {
		return rd;
	}
	
	public void setRd(List<Integer> rd) {
		this.rd = rd;
	}
}
