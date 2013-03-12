package br.ufla.ri.util;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class FileUtil implements Serializable {

	private static final long serialVersionUID = 1L;

	public List<BufferedReader> readFilesFromFolder(String folderPath) {

		List<BufferedReader> bufferedReaders = new ArrayList<BufferedReader>();
		FileReader reader;
		BufferedReader br;
		File folder = new File(folderPath);

		if(folder.listFiles() == null) {
			return null;
		}

		for(File file : folder.listFiles()) {
			
			if(file.isFile()) {
				try {
					reader = new FileReader(file);
					br = new BufferedReader(reader);
					bufferedReaders.add(br);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
			}
		}
		return bufferedReaders;
	}
	
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
	
	public BufferedReader getResource(String path) {
	
		InputStream stream = this.getClass().getClassLoader().getResourceAsStream(path);
		DataInputStream in = new DataInputStream(stream);
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		
		return br;
	}
}