package br.ufla.lucene;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.event.ActionEvent;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.IntField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParserTokenManager;
import org.apache.lucene.search.Explanation;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

import br.ufla.enums.Types;
import br.ufla.ri.util.FileUtil;
import br.ufla.ri.util.PathFiles;
import br.ufla.ri.util.Regex;

@ManagedBean(name="luceneBean")
@ApplicationScoped
public class LuceneBean implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Integer termQuery = 1;
	private Integer rangeQuery = 2;
	private Integer prefixQuery = 3;
	private Integer booleanQuery = 4;
	private Integer phraseQuery = 5;
	private Integer wildcardQuery = 6;
	private Integer fuzzyQuery = 7;
	
	private Integer queryType = 1;
	private String queryText;
	
	public static void main(String[] args) {
		try {
			init();
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unused")
	@PostConstruct
	private static void init() throws IOException, ParseException {
		
		CharArraySet a = null;
		Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_41, StopAnalyzer.ENGLISH_STOP_WORDS_SET); //StopAnalyzer.ENGLISH_STOP_WORDS_SET
//		WhitespaceAnalyzer analyzer2 = new WhitespaceAnalyzer(Version.LUCENE_41);
//		Analyzer analyzer = new StopAnalyzer(Version.LUCENE_41, StopAnalyzer.ENGLISH_STOP_WORDS_SET); // ClassicAnalyzer 
		IndexWriterConfig writerConfig = new IndexWriterConfig(Version.LUCENE_41, analyzer);
		Directory directory = new RAMDirectory();
//		Directory directory =  FSDirectory.open(new File("src/main/resources/indexDirectory"));
		
		IndexWriter writer = new IndexWriter(directory, writerConfig);
		
		Set<String> documentTypes = new HashSet<String>();
//		documentTypes.add(Types.PN.toString());
//		documentTypes.add(Types.RN.toString());
		documentTypes.add(Types.AU.toString());
		documentTypes.add(Types.TI.toString());
		documentTypes.add(Types.SO.toString());
		documentTypes.add(Types.MJ.toString());
		documentTypes.add(Types.MN.toString());
		documentTypes.add(Types.AB.toString());
		
		FileUtil fileUtil = new FileUtil();
		Long inicioLeituraArquivo = System.currentTimeMillis();
		List<BufferedReader> files = fileUtil.readFilesFromFolder(PathFiles.DOCUMENTS_FOLDER);
		BufferedReader file = fileUtil.readFile("src/main/resources/documents/cf74");
		List<QueryModel> filesQuery = createQueryDocuments(); 
		
		String documentLine = null;
		String typeAnalyzing = null;

//		for(BufferedReader br : files) {

			Document document = new Document();

			while ((documentLine = file.readLine()) != null) {

				if(documentLine.isEmpty()) {
					if(!document.getFields().isEmpty()) {
						writer.addDocument(document);
					}
					
					for(IndexableField f : document.getFields()) {
						System.out.println(f.name() + ":" + f.stringValue());
					}
					
					document = new Document();
					continue;
				}
				
				if (documentLine.length() > 2) {
					if (!documentLine.substring(0, 3).startsWith(Regex.BLANK_SPACE_THREE)) {
						typeAnalyzing = documentLine.substring(0, 2);
					}
					
					if (documentTypes.contains(typeAnalyzing)) {
						if(document.getField(typeAnalyzing) == null) {
							Field field = new TextField(typeAnalyzing, documentLine.substring(3).trim(), Store.YES);
							document.add(field);
						} else {
							StringBuilder sb = new StringBuilder(document.getField(typeAnalyzing).stringValue());
							if(typeAnalyzing.equals(Types.AU.toString()) || typeAnalyzing.equals(Types.TI.toString()) || typeAnalyzing.equals(Types.SO.toString()) || typeAnalyzing.equals(Types.AB.toString())) {
								sb.append(" ");
							} else {
								sb.append("  ");
							}
							sb.append(documentLine.substring(3).trim());
							Field field = new TextField(typeAnalyzing, sb.toString(), Store.YES);
							document.removeField(typeAnalyzing);
							document.add(field);
						}
					} else if (typeAnalyzing.equalsIgnoreCase(Types.PN.toString()) || typeAnalyzing.equalsIgnoreCase(Types.RN.toString())) {
						Field field = new IntField(typeAnalyzing, Integer.valueOf(documentLine.substring(3).trim()), Store.YES);
						document.add(field);
					}
				}
			}
			
//		}

		writer.close();
		
		Long fimLeituraArquivo = System.currentTimeMillis();
		System.out.println(fimLeituraArquivo - inicioLeituraArquivo);
		QueryParserTokenManager manager = new QueryParserTokenManager(null);		
		
	    DirectoryReader directoryReader = DirectoryReader.open(directory);
	    IndexSearcher searcher = new IndexSearcher(directoryReader);
	    String[] af = documentTypes.toArray(new String[documentTypes.size()]);
	    MultiFieldQueryParser multiFieldQueryParser = new MultiFieldQueryParser(Version.LUCENE_41, af, analyzer);
//	    QueryParser queryParser = new QueryParser(Version.LUCENE_41, Types.AB.toString(), analyzer);
//	    Query queryParsed = multiFieldQueryParser.parse("What are the effects of calcium on the physical properties of mucus from CF patients?");
//	    Query queryParsed = multiFieldQueryParser.parse("Hoiby-N.  Jacobsen-L.  Jorgensen-B-A.  Lykkegaard-E.  Weeke-B.");
//	    Query query = new TermQuery(new Term(Types.AU.toString(), "Hoiby-N."));
        int i = 10;
	    for(QueryModel queryModel : filesQuery) {
	    	if(queryModel.getQn().equals("00010"))
	    		break;
	    	Query queryParsed = multiFieldQueryParser.parse(queryModel.getQu());
	    	TopDocs topDocs = searcher.search(queryParsed, 100);
	    	ScoreDoc[] scoreDosArray = topDocs.scoreDocs;
	    	
	    	List<Integer> documentsRelevantsToAlgorith = new ArrayList<Integer>();
	    	List<Integer> both = new ArrayList<Integer>();
	    	for(ScoreDoc score:scoreDosArray) {
	    		Explanation explanation = searcher.explain(queryParsed, score.doc);
	    		Document doc = searcher.doc(score.doc);
//	    		System.out.println("QN - " + queryModel.getQn() + " PN - " + doc.getField(Types.PN.toString()).stringValue() + " RN - " + doc.getField(Types.RN.toString()).stringValue());
	    		documentsRelevantsToAlgorith.add(doc.getField(Types.RN.toString()).numericValue().intValue());
	    		if(queryModel.getRd().contains(doc.getField(Types.RN.toString()).numericValue().intValue())) {
	    			both.add(doc.getField(Types.RN.toString()).numericValue().intValue());
	    		}
//	    		System.out.println(explanation);
	    	}
	    	
//	    	Collections.sort(documentsRelevantsToAlgorith);
	    	System.out.println("humanos: " + queryModel.getRd() + " size: " + queryModel.getRd().size());
	    	System.out.println("algoritmo: " + documentsRelevantsToAlgorith + " size: " + scoreDosArray.length);
	    	System.out.println("ambos: " + both + " size: " + both.size());
	    }
	}
	
	private static List<QueryModel> createQueryDocuments() throws IOException {
		
		List<QueryModel> documents = new ArrayList<QueryModel>();
		List<Integer> documentsRelevantHumansFromQueryDocument = new ArrayList<Integer>(); 
		FileUtil fileUtil = new FileUtil();
		BufferedReader cfQueryFile = fileUtil.getResource(PathFiles.QUERY_FILE_WEB);
		
		String queryDocumentLine;
		String typeAnalyzing = null;
		QueryModel queryModel = new QueryModel();
		
		while((queryDocumentLine = cfQueryFile.readLine()) != null) {

			if(queryDocumentLine.trim().isEmpty()) {
				documents.add(queryModel);
				queryModel = new QueryModel();
				documentsRelevantHumansFromQueryDocument = new ArrayList<Integer>();
			}
			
			if (queryDocumentLine.startsWith(Types.QN.toString())) {
				queryModel.setQn(queryDocumentLine.substring(3).trim());
				continue;
			}
			
			if (queryDocumentLine.startsWith(Types.NR.toString())) {
				queryModel.setNr(queryDocumentLine.substring(3).trim());
				continue;
			}
			
			if(queryDocumentLine.length() > 2) {

				if(!queryDocumentLine.substring(0, 3).startsWith(Regex.BLANK_SPACE_SINGLE)) {
					typeAnalyzing = queryDocumentLine.substring(0,2);
				}

				String conteudo = queryDocumentLine.substring(3, queryDocumentLine.length()).trim();
				if(typeAnalyzing.equalsIgnoreCase(Types.QU.toString())) {
					if(queryModel.getQu() == null || queryModel.getQu().isEmpty()) {
						conteudo = removeCharacter(conteudo);
						queryModel.setQu(conteudo);
					} else {
						StringBuilder sb = new StringBuilder(queryModel.getQu());
						conteudo = removeCharacter(conteudo);
						sb.append(" ").append(conteudo);
						queryModel.setQu(sb.toString());
					}
					continue;
				}
				
				if(typeAnalyzing.equalsIgnoreCase(Types.RD.toString())) {
					
					String[] valuesString = conteudo.trim().split(Regex.BLANK_SPACE_AT_LEAST_ONE);
					for(int i = 0; i < valuesString.length; i+=2) {
						if(!valuesString[i].trim().isEmpty()) {
							documentsRelevantHumansFromQueryDocument.add(Integer.valueOf(valuesString[i].trim()));
						}
					}
					queryModel.setRd(documentsRelevantHumansFromQueryDocument);
					continue;
				}
			}
		}
		return documents;
	}
	
	/**
	 * Retira alguns caracteres especiais observados nas perguntas.
	 * */
	private static String removeCharacter(String str) {
		str = str.replace("?", "");
		str = str.replace(")", "");
		str = str.replace("(", "");
		str = str.replace(",", "");
		str = str.replace("\"", "");
		str = str.replace("/", " ");
		str = str.replace("-", " ");
		str = str.replace(".", " ");
		return str;
	}
	
	public void consultar(ActionEvent event) {
		System.out.println("consultar");
	}
	
	public Integer getTermQuery() {
		return termQuery;
	}

	public void setTermQuery(Integer termQuery) {
		this.termQuery = termQuery;
	}

	public Integer getRangeQuery() {
		return rangeQuery;
	}

	public void setRangeQuery(Integer rangeQuery) {
		this.rangeQuery = rangeQuery;
	}

	public Integer getPrefixQuery() {
		return prefixQuery;
	}

	public void setPrefixQuery(Integer prefixQuery) {
		this.prefixQuery = prefixQuery;
	}

	public Integer getBooleanQuery() {
		return booleanQuery;
	}

	public void setBooleanQuery(Integer booleanQuery) {
		this.booleanQuery = booleanQuery;
	}

	public Integer getPhraseQuery() {
		return phraseQuery;
	}

	public void setPhraseQuery(Integer phraseQuery) {
		this.phraseQuery = phraseQuery;
	}

	public Integer getWildcardQuery() {
		return wildcardQuery;
	}

	public void setWildcardQuery(Integer wildcardQuery) {
		this.wildcardQuery = wildcardQuery;
	}

	public Integer getFuzzyQuery() {
		return fuzzyQuery;
	}

	public void setFuzzyQuery(Integer fuzzyQuery) {
		this.fuzzyQuery = fuzzyQuery;
	}

	public Integer getQueryType() {
		return queryType;
	}

	public void setQueryType(Integer queryType) {
		this.queryType = queryType;
	}

	public String getQueryText() {
		return queryText;
	}

	public void setQueryText(String queryText) {
		this.queryText = queryText;
	}

}