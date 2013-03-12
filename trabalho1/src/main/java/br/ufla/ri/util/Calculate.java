package br.ufla.ri.util;

import java.text.DecimalFormat;

public class Calculate {

	public static Double termFrequency(Integer frequency) {
		if(frequency == null || frequency.equals(0)) {
			return 0.0;
		}
		return 1 + Math.log(frequency) / Math.log(2.0);
	}
	
	public static Double inverseDocumentFrequency(Integer totalDocs, Integer numberDocs) {
		if(numberDocs == null || numberDocs.equals(0)) {
			return 0.0;
		}
		
		DecimalFormat a = new DecimalFormat();
		a.setMaximumFractionDigits(5);
		Double div = (totalDocs / Double.valueOf(numberDocs)) / Math.log(2.0);
		String b = a.format(Math.log(div));
		b = b.replace(",", ".");
		return Double.valueOf(b);
	}
	
	public static Double tfIDF(Double tf, Double idf) {
		return tf * idf;
	}
	
	public static Double recall(Integer size) {
		return 0.0;
	}
}