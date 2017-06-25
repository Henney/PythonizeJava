package pythonizeJava;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.StringTokenizer;

public class JavaPythonizer {

	private String[] frontKeywords = { "public", "private", "protected", "static", "new", ";", "}" };
	
	private final String SPACING = new String(new char[20]).replace("\0", "\t");

	public void pythonize(String fileName) {
		File file = new File(fileName);
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(file));
		} catch (FileNotFoundException e) {
			throw new IllegalArgumentException("The specified file does not exist");
		}
		
		StringBuilder newFileContents = new StringBuilder();
		
		String prevLine = "";
		String line;
		try {
			while ((line = br.readLine()) != null) {
				Pair<String, String> cleanedLines = cleanLine(line);

				newFileContents.append("\n ");
				newFileContents.append(prevLine);
				newFileContents.append(" ");
				newFileContents.append(cleanedLines.a);
				prevLine = cleanedLines.b;
			}
			newFileContents.append("\n");
			newFileContents.append(prevLine);
			
			file.delete();
			file.createNewFile();
			BufferedWriter bw = new BufferedWriter(new FileWriter(file));
			bw.write(newFileContents.toString());
			
			br.close();
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private Pair<String, String> cleanLine(String line) {		
		StringBuilder prevLine = new StringBuilder();
		StringBuilder thisLine = new StringBuilder();
				
		prevLine.append(SPACING);
		for (int i = 0; i < line.length(); i++) {
			if (line.charAt(i) == '\t')
				thisLine.append('\t');
			else
				break;
		}
		
		StringTokenizer st = new StringTokenizer(line);
		while (st.hasMoreTokens()) {
			String word = st.nextToken();
			
			
			if (!Arrays.asList(frontKeywords).contains(word)) {
				thisLine.append(word);
				thisLine.append(' ');
				break;
			}
			
			prevLine.append(word);
			prevLine.append(' ');
		}
		while (st.hasMoreTokens()) {
			String word = st.nextToken();	
			
			if (!st.hasMoreTokens()) {
				if (word.equals("{")) {
					thisLine.append(SPACING);
					thisLine.append(word);
					thisLine.append(' ');
				} else if (word.endsWith(";")) {
					thisLine.append(word.substring(0, word.length() - 1));
					thisLine.append(SPACING);
					thisLine.append(";");
					thisLine.append(' ');
				} else {
					thisLine.append(word);
					thisLine.append(' ');
				}
				break;
			}
			
			thisLine.append(word);
			thisLine.append(' ');			
		}
		
		return new Pair<String, String>(prevLine.toString(), thisLine.toString());
	}

	private class Pair<A, B> {
		A a;
		B b;
		
		private Pair(A a, B b) {
			this.a = a;
			this.b = b;
		}
	}
	
//	public static void main(String[] args) {
//		new JavaPythonizer().pythonize("src\\pythonizeJava\\JavaPythonizer.java");
//	}	
	
}
