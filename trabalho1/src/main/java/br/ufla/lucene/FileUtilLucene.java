package br.ufla.lucene;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;

public class FileUtilLucene {

public BufferedReader readFile(String path) {
		
		try {
			FileInputStream fstream = new FileInputStream(path);
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			return br;

		} catch (FileNotFoundException e) {

			e.printStackTrace();
			return null;
		}
	}
}
