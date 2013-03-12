package br.ufla.ri.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;

import org.primefaces.context.RequestContext;

import br.ufla.ri.util.MountList;

@ManagedBean(name="informationRecoveryBean")
@ViewScoped
public class InformationRecoveryBean implements Serializable {

	private static final long serialVersionUID = 1L;

	public static final Integer TOTAL_DOC_CF74_LENGTH = 167;
	public static final Integer TOTAL_DOC_CF75_LENGTH = 88;
	public static final Integer TOTAL_DOC_CF76_LENGTH = 327;
	public static final Integer TOTAL_DOC_CF77_LENGTH = 199; 
	public static final Integer TOTAL_DOC_CF78_LENGTH = 201;
	public static final Integer TOTAL_DOC_CF79_LENGTH = 257;
	
	private Integer CONSULT_TYPE_TRABALHO1 = 2;
	private Integer CONSULT_TYPE_TRABALHO1_OTIMIZADO = 3;
	private Integer consultType;
	
	private String CF74 = "cf74";
	private String CF75 = "cf75";
	private String CF76 = "cf76";
	private String CF77 = "cf77";
	private String CF78 = "cf78";
	private String CF79 = "cf79";
	private List<String> documentsCF;
	
	private String stopWords = "";
	private Map<Integer, Double> dataGridList;
	
	private MountList mountList = new MountList();
	private Map<String, Map<Integer, Double>> recallPrecisionValues;

	@PostConstruct
	private void init() {

		consultType = CONSULT_TYPE_TRABALHO1;
		createAllDocumentsCF();
	}
	
	public void search(ActionEvent event) {

		String[] stopWordsArr = stopWords.split(";");
		if(stopWordsArr != null) {
			for(String word : stopWordsArr) {
				if(word.trim().isEmpty()) {
					continue;
				}
				mountList.addStopWord(word.trim());
			}
		}
//		mountList.addStopWord("cf");
//		mountList.addStopWord("patients");
//		mountList.addStopWord("cystic");
//		mountList.addStopWord("fibrosis");
//		mountList.addStopWord("disease");
//		mountList.addStopWord("found");
//		mountList.addStopWord("male");
		mountList.createDocumentsWeb();
		
		recallPrecisionValues = mountList.calculateRecallPrecision();
		int total = recallPrecisionValues.size();
		Map<Integer, Double> averageRecallPrecision = new HashMap<Integer, Double>();

		for(String query : recallPrecisionValues.keySet()) {
			Map<Integer, Double> recallPrecision = recallPrecisionValues.get(query);
			for(int i = 0; i < 101; i+=10) {
				Double val = recallPrecision.get(i);
				Double oldVal = averageRecallPrecision.get(i);
				if(oldVal != null) {
					Double sum = (val + oldVal);
					Double average =  sum / total;
					averageRecallPrecision.put(i, average);
				} else {
					Double average = val / total;
					averageRecallPrecision.put(i, average);
				}
			}
		}
		
		StringBuilder sb = new StringBuilder();
		dataGridList = new HashMap<Integer, Double>();
		for(int i = 0; i < 101; i+=10) {
			sb.append(averageRecallPrecision.get(i));
			sb.append(",");
			dataGridList.put(i, averageRecallPrecision.get(i));
		}

		RequestContext requestContext = RequestContext.getCurrentInstance();  
		requestContext.execute("mountMaps(" + "\"" + sb.substring(0, sb.length()-1) + "\"" + ")");
	}
	
	public void changeConsultType() {

		clean();
		switch(consultType) {
			
			case 2:
				consultType = CONSULT_TYPE_TRABALHO1;
				createAllDocumentsCF();
			break;
			
			case 3:
				consultType = CONSULT_TYPE_TRABALHO1_OTIMIZADO;
				createAllDocumentsCF();
				for(String word : mountList.getIgnoreList()) {
					stopWords += word + ";";
				}
			break;
		}
	}
	
	private void clean() {

		stopWords = "";
		documentsCF = null;
		if(mountList != null) {
			mountList.clean();
		}
		if(dataGridList != null) {
			dataGridList.clear();
		}
	}

	private void createAllDocumentsCF() {
		documentsCF = new ArrayList<String>();
		documentsCF.add(CF74);
		documentsCF.add(CF75);
		documentsCF.add(CF76);
		documentsCF.add(CF77);
		documentsCF.add(CF78);
		documentsCF.add(CF79);
	}
	
	public Integer getConsultType() {
		return consultType;
	}

	public void setConsultType(Integer consultType) {
		this.consultType = consultType;
	}

	public List<String> getDocumentsCF() {
		return documentsCF;
	}

	public void setDocumentsCF(List<String> documentsCF) {
		this.documentsCF = documentsCF;
	}

	public String getStopWords() {
		return stopWords;
	}

	public void setStopWords(String stopWords) {
		this.stopWords = stopWords;
	}

	public int getCONSULT_TYPE_TRABALHO1() {
		return CONSULT_TYPE_TRABALHO1;
	}

	public void setCONSULT_TYPE_TRABALHO1(int cONSULT_TYPE_TRABALHO1) {
		CONSULT_TYPE_TRABALHO1 = cONSULT_TYPE_TRABALHO1;
	}

	public int getCONSULT_TYPE_TRABALHO1_OTIMIZADO() {
		return CONSULT_TYPE_TRABALHO1_OTIMIZADO;
	}

	public void setCONSULT_TYPE_TRABALHO1_OTIMIZADO(
			int cONSULT_TYPE_TRABALHO1_OTIMIZADO) {
		CONSULT_TYPE_TRABALHO1_OTIMIZADO = cONSULT_TYPE_TRABALHO1_OTIMIZADO;
	}

	public String getCF74() {
		return CF74;
	}

	public void setCF74(String cF74) {
		CF74 = cF74;
	}

	public String getCF75() {
		return CF75;
	}

	public void setCF75(String cF75) {
		CF75 = cF75;
	}

	public String getCF76() {
		return CF76;
	}

	public void setCF76(String cF76) {
		CF76 = cF76;
	}

	public String getCF77() {
		return CF77;
	}

	public void setCF77(String cF77) {
		CF77 = cF77;
	}

	public String getCF78() {
		return CF78;
	}

	public void setCF78(String cF78) {
		CF78 = cF78;
	}

	public String getCF79() {
		return CF79;
	}

	public void setCF79(String cF79) {
		CF79 = cF79;
	}

	public ArrayList<Entry<Integer, Double>> getDataGridList() {
		if(dataGridList != null) {
			Set<Entry<Integer, Double>> retorno = dataGridList.entrySet();
			return new ArrayList<Entry<Integer, Double>>(retorno);
		}
		return new ArrayList<Map.Entry<Integer,Double>>();
	}

	public void setDataGridList(Map<Integer, Double> dataGridList) {
		this.dataGridList = dataGridList;
	}

}