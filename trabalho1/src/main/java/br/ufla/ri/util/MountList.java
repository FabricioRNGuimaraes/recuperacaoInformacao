package br.ufla.ri.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import br.ufla.enums.Types;
import br.ufla.ri.model.Document;

public class MountList implements Serializable {

	private static final long serialVersionUID = 1L;

	private Set<String> ignoreListFileWords = new HashSet<String>();
	private Set<String> ignoreList = new HashSet<String>();
	private Integer total = 1239; 
	private FileUtil fileUtil;
	private BufferedReader ignoreListFile;
	private BufferedReader cfQueryFile;
	private List<BufferedReader> documentsFile;
	
	private Map<String, String> documentSpliterMap = new HashMap<String, String>();
	private Map<String, String> querySpliterMap = new HashMap<String, String>();

	private Set<String> documentTypes = new HashSet<String>();

	private Map<String, Map<String, Document>> inverseListDocument = new HashMap<String, Map<String, Document>>();
	private Map<String, Map<String, Integer>> inverseListQuery = new HashMap<String, Map<String, Integer>>();
	
	private Map<String, Set<Integer>> documentsRelevantHuman = new HashMap<String, Set<Integer>>();
	private Map<String, TreeSet<Document>> documentsRelevantAlgorith = new HashMap<String, TreeSet<Document>>();

	private boolean isWebGlobal;
	
	public MountList() {
		fileUtil = new FileUtil();
	}
	
	public void createDocumentsWeb() {
		init(true);
	}
	
	public void createDocumentsDesktop() {
		init(false);
	}

	private void init(boolean isWeb) {
		
		isWebGlobal = isWeb;
		readDocumentsFile(isWeb);
		readQueryFile(isWeb);
		readIgnoreListFile(isWeb);
		
		mountSpliterMap();
		mountTypesMap();
	}

	private void readIgnoreListFile(boolean isWeb) {

		if(isWeb) {
			ignoreListFile = fileUtil.getResource(PathFiles.STOP_WORDS_FILE_WEB);
		} else {
			ignoreListFile = fileUtil.readFile(PathFiles.STOP_WORDS_FILE);
		}
		
		String strLine;

		if(ignoreListFile == null) {
			return;
		}

		try {
			while ((strLine = ignoreListFile.readLine()) != null) {
				ignoreListFileWords.add(strLine.toLowerCase());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void readDocumentsFile(boolean isWeb) {
		
		if(isWeb) {
			documentsFile = new ArrayList<BufferedReader>();
			documentsFile.add(fileUtil.getResource(PathFiles.DOCUMENT_CF74));
			documentsFile.add(fileUtil.getResource(PathFiles.DOCUMENT_CF75));
			documentsFile.add(fileUtil.getResource(PathFiles.DOCUMENT_CF76));
			documentsFile.add(fileUtil.getResource(PathFiles.DOCUMENT_CF77));
			documentsFile.add(fileUtil.getResource(PathFiles.DOCUMENT_CF78));
			documentsFile.add(fileUtil.getResource(PathFiles.DOCUMENT_CF79));
		} else {
			documentsFile = fileUtil.readFilesFromFolder(PathFiles.DOCUMENTS_FOLDER);
		}
	}
	
	private void readQueryFile(boolean isWeb) {
		if(isWeb) {
			cfQueryFile = fileUtil.getResource(PathFiles.QUERY_FILE_WEB);
		} else {
			cfQueryFile = fileUtil.readFile(PathFiles.QUERY_FILE);
		}
	}
	
	private void mountSpliterMap() {

		documentSpliterMap.put(Types.AU.toString(), Regex.SPLITER_AU);
		documentSpliterMap.put(Types.MJ.toString(), Regex.SPLITER_MJ);
		documentSpliterMap.put(Types.MN.toString(), Regex.SPLITER_MN);
		documentSpliterMap.put(Types.TI.toString(), Regex.SPLITER_TI);
		
		querySpliterMap.put(Types.QU.toString(), Regex.SPLITER_QU);
	}
	
	private void mountTypesMap() {
		
		documentTypes.add(Types.AU.toString());
		documentTypes.add(Types.TI.toString());
		documentTypes.add(Types.SO.toString());
		documentTypes.add(Types.MJ.toString());
		documentTypes.add(Types.MN.toString());
		documentTypes.add(Types.AB.toString());
	}
	
	private void mountInverseList() {
		
		String documentLine = null;
		String key = null;
		String typeAnalyzing = null;
		Integer rnKey = null;
		
		try {
			
			for(BufferedReader br : documentsFile) {
				while ((documentLine = br.readLine()) != null) {
					
					if (documentLine.startsWith(Types.PN.toString())) {
						key = documentLine.substring(3).trim();
						continue;
					}
					
					if(documentLine.startsWith(Types.RN.toString())) {
						rnKey = Integer.valueOf(documentLine.substring(3).trim());
					}
					
					if (documentLine.length() > 2) {
						if (!documentLine.substring(0, 3).startsWith(Regex.BLANK_SPACE_THREE)) {
							typeAnalyzing = documentLine.substring(0, 2);
						}
						
						if (documentTypes.contains(typeAnalyzing)) {
							String content = documentLine.substring(3, documentLine.length());
							createInverseListMap(key, content.toLowerCase(), typeAnalyzing, rnKey);
						}
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void createInverseListMap(String key, String content, String documentType, Integer rnKey) {

		String[] contents = content.split(Regex.getSpliterType(documentSpliterMap, documentType));
		for (int i = 0; i < contents.length; i++) {
			String str = contents[i].trim();
			if (str.trim().isEmpty()) {
				continue;
			}
			
			if(ignoreList.contains(str)) {
				continue;
			}
			
			if(!inverseListDocument.containsKey(str)) {
				createNewKey(key, str, rnKey);
			} else {
				insertExistingKey(key, str, rnKey);
			}
		}
	}

	private void insertExistingKey(String key, String content, Integer rnKey) {

		Map<String, Document> documents = inverseListDocument.get(content);

		if(documents == null) {
			return;
		}

		Document document = documents.get(key);

		if(document == null) {
			document = createDocument(key, rnKey);
			inverseListDocument.get(content).put(key, document);
		} else {
			document.setQuantity(document.getQuantity() + 1);
		}
	}

	private void createNewKey(String key, String content, Integer rnKey) {
		
		Map<String, Document> map = new HashMap<String, Document>();
		map.put(key, createDocument(key, rnKey));
		inverseListDocument.put(content, map);
	}
	
	private Document createDocument(String key, Integer rnKey) {
		
		Document doc = new Document();
		doc.setKey(key);
		doc.setQuantity(1);
		doc.setRn(rnKey);
		return doc;
	}
	
	private void mountQueryList() {
		
		try {
			
			String queryDocumentLine;
			String queryNumber = null;
			String typeAnalyzing = null;
			
			while((queryDocumentLine = cfQueryFile.readLine()) != null) {

				if (queryDocumentLine.startsWith(Types.QN.toString())) {
					queryNumber = queryDocumentLine.substring(3).trim();
					continue;
				}

				if(queryDocumentLine.length() > 2) {

					if(!queryDocumentLine.substring(0, 3).startsWith(Regex.BLANK_SPACE_SINGLE)) {
						typeAnalyzing = queryDocumentLine.substring(0,2);
					}


					if(typeAnalyzing.equalsIgnoreCase(Types.RD.toString())) {
						
						String conteudo = queryDocumentLine.substring(3, queryDocumentLine.length()).trim();
						String[] valuesString = conteudo.trim().split(Regex.BLANK_SPACE_AT_LEAST_ONE);
						Set<Integer> docs = documentsRelevantHuman.get(queryNumber);
						
						for(int i = 0; i < valuesString.length; i+=2) {
							if(!valuesString[i].trim().isEmpty()) {
								if(docs == null) {
									docs = new HashSet<Integer>();
								}
								docs.add(Integer.valueOf(valuesString[i].trim()));
								documentsRelevantHuman.put(queryNumber, docs);
							}
						}
						continue;
					}
					
					if(typeAnalyzing.equalsIgnoreCase(Types.QU.toString())) {
						String conteudo = queryDocumentLine.substring(3, queryDocumentLine.length());
						createMapQuery(conteudo.toLowerCase(), queryNumber, typeAnalyzing);
						continue;
					}
				}
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void createMapQuery(String content, String key, String documentType) {

		String[] contents = content.split(Regex.getSpliterType(querySpliterMap, documentType));
		for (int i = 0; i < contents.length; i++) {
			String str = contents[i].trim().replaceAll("[\"(),?.;]", "");
			if (str.trim().isEmpty()) {
				continue;
			}
			
			if(ignoreList.contains(str)) {
				continue;
			}

			if(!inverseListQuery.containsKey(key)) {
				createNewKeyQuery(key, str);
			} else {
				initExistKeyQuery(key, str);
			}
		}
	}

	private void initExistKeyQuery(String key, String content) {

		Map<String, Integer> map = inverseListQuery.get(key);
		Integer countDocument = map.get(content);
		if (countDocument == null) {
			map.put(content, 1);
		} else {
			map.put(content, ++countDocument);
		}
	}

	private void createNewKeyQuery(String key, String content) {
		
		Map<String, Integer> map = new HashMap<String, Integer>();
		map.put(content, 1);
		inverseListQuery.put(key, map);
	}
	
	private void mountRelevantDocumentsAlgorith() {
		
		for(String queryKey : inverseListQuery.keySet()) {
			
			TreeSet<Document> documentRelevantQuery = new TreeSet<Document>();
			Map<String, Integer> queryKeyValues = inverseListQuery.get(queryKey);
			for(String query : queryKeyValues.keySet()) {
				Map<String, Document> documentsQuery = inverseListDocument.get(query);
				if(documentsQuery != null) {
					for(Document doc : documentsQuery.values()) {
						doc.setIdf(Calculate.inverseDocumentFrequency(total, documentsQuery.size()));
						doc.setTfIDF(Calculate.tfIDF(doc.getTermFrequency(), doc.getIdf()));
						documentRelevantQuery.add(doc);
					}
				}
			}
			documentsRelevantAlgorith.put(queryKey, documentRelevantQuery);
		}
	}
	
	public Map<String, Map<Integer, Double>> calculateRecallPrecision() {

		mountInverseList();
		mountQueryList();
		
		mountRelevantDocumentsAlgorith();
		
		Map<String, Map<Integer, Double>> queryRecallPrecision = new HashMap<String, Map<Integer, Double>>();
		Map<Integer, Double> recallPrecisionValues;
		
		for(String queryNumber : documentsRelevantAlgorith.keySet()) {

			Map<Double, Double> recallPrecision = new HashMap<Double, Double>();
			StringBuilder sb = new StringBuilder();
			Set<Document> docsAlgorith = documentsRelevantAlgorith.get(queryNumber);
			Set<Integer> docsHuman = documentsRelevantHuman.get(queryNumber);
			Integer documentRelevantForHumanSize = docsHuman.size();
			
			Double recallStep = (1.0 / documentRelevantForHumanSize) * 100;
			Double recall = recallStep;
			sb.append("Query numero: (" + queryNumber + ") - " + inverseListQuery.get(queryNumber).keySet());
			sb.append("\nDocumentos relevantes para o algoritmo: (" + docsAlgorith.size() + ") - " + docsAlgorith);
			sb.append("\nDocumentos relevantes para o humano: (" + docsHuman.size() + ") - " +  docsHuman + "\nAlgoritmo e Humano: ");

			if(docsAlgorith != null) {
				Object[] relevantsDocAlgorith = docsAlgorith.toArray();
				for(int i = 0; i < relevantsDocAlgorith.length; i++) {
					Document rel = (Document) relevantsDocAlgorith[i];
					Double qnt = 1.0;
					if(docsHuman != null) {
						if(docsHuman.contains(rel.getRn())) {
							Double precision = (qnt / (i + 1)) * 100; 
							qnt++;
							sb.append("posicao: " + (i + 1) + " valor: " + rel + ", ");
							recallPrecision.put(recall, precision);
							recall += recallStep;
						}
					}
				}
			}

			recallPrecisionValues = normalizeRecallPrecision(recallPrecision);
			
			System.out.println();
			System.out.println(sb.substring(0, sb.length() - 2));
			System.out.println(recallPrecisionValues);

			queryRecallPrecision.put(queryNumber, recallPrecisionValues);
		}
		return queryRecallPrecision;
	}
	
	private Map<Integer, Double> normalizeRecallPrecision(Map<Double, Double> recallPrecision) {
		Map<Integer, Double> recallPrecisionValues = new HashMap<Integer, Double>();

		for(int i = 0; i < 101; i+=10) {
			recallPrecisionValues.put(i, 0.0);
			int intervalo = i - 10;
			for(Double key : recallPrecision.keySet()) {
				if((intervalo <= key) && (key < i)) {
					if(recallPrecisionValues.get(i) < recallPrecision.get(key)) {
						recallPrecisionValues.put(i, recallPrecision.get(key));
					}
				}
			}
		}
		
		for(int i = 100; i > 0; i-=10) {
			Double ultimo = recallPrecisionValues.get(i);
			Double penultimo = recallPrecisionValues.get(i-10); 
			if(ultimo != null && penultimo != null && ultimo > penultimo) {
				recallPrecisionValues.put(i-10, ultimo);
			}
		}
		
		return recallPrecisionValues;
	}
	
	public void addStopWord(String word) {
		ignoreList.add(word);
	}
	
	public Set<String> getIgnoreList() {
		if(ignoreListFileWords.isEmpty()) {
			readIgnoreListFile(true);
		}
		Set<String> retorno = new HashSet<String>(ignoreListFileWords);
		retorno.addAll(ignoreList);
		return retorno;
	}
	
	public void clean() {
		ignoreList.clear();
	}
}
