package br.ufla.ri.util;

import java.util.Map;

public class Regex {

	public static String OR = "|";
	public static String DOT = "\\.";
	public static String COLON = "\\:";
	public static String SEMICOLON = "\\;";
	public static String COMMA = "\\,";
	public static String LEFT_BRACKET = "\\(";
	public static String RIGHT_BRACKET = "\\)";
	public static String BLANK_SPACE_AT_LEAST_ONE = "\\s+";
	public static String BLANK_SPACE_AT_LEAST_TWO = "\\s{2,}?";	
	public static String BLANK_SPACE_SINGLE = " ";
	public static String BLANK_SPACE_THREE = "   ";
	public static String ONLY_CHARACTER = "\\w";
	
	public static String SPLITER_GENERIC = BLANK_SPACE_AT_LEAST_ONE + OR + DOT + OR + COMMA + OR + COLON + OR + LEFT_BRACKET + OR + RIGHT_BRACKET;
//	public static String SPLITER_GENERIC = ONLY_CHARACTER + OR + BLANK_SPACE_AT_LEAST_ONE + OR + DOT + OR + COMMA + OR + COLON + OR + LEFT_BRACKET + OR + RIGHT_BRACKET;
	public static String SPLITER_AU = BLANK_SPACE_AT_LEAST_ONE;
	public static String SPLITER_MJ = DOT;
	public static String SPLITER_MN = DOT;
	public static String SPLITER_QU = BLANK_SPACE_SINGLE;
	public static String SPLITER_TI = BLANK_SPACE_SINGLE;
	
	public static String getSpliterType(Map<String, String> spliter, String type) {
		String splitter = spliter.get(type);
		return splitter != null ? splitter : SPLITER_GENERIC;
	}
}