package search;

import java.io.BufferedReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;


import org.apache.lucene.search.similarities.BasicStats;
import org.apache.lucene.search.similarities.SimilarityBase;

public class Search {

	private final HashMap<String, String> note_queries;
	private final HashMap<String, String> desc_queries;
	private final HashMap<String, String> summ_queries;

	static String pmcID;
	static String text;

	static IndexSearcher searcher;
	Analyzer analyzer;

	public Search(String INDEX_DIR, String output_File_PATH, ArrayList<String> Querylist, ArrayList<String> numList)
			throws Exception {
		searcher = createSearcher(INDEX_DIR);
		analyzer = new EnglishAnalyzer();
		
		

		note_queries = new HashMap();
		desc_queries = new HashMap();
		summ_queries = new HashMap();
		
		String fname = "/Users/Nithin/Desktop/topics2016.xml";
		loadXMLFile(fname);
		//runQuery(summ_queries, output_File_PATH);
		QuerySearch(Querylist, numList, output_File_PATH);

	}
	
    public synchronized static String tidyWord(String str){
        
        if(str.matches("[-]+")){
            return "";
        }else if(str.length() > 20){
            return "";
        }else if( str.startsWith("<") && str.endsWith(">")){
            return "";
        }else {
            
            //to lower case
            str = str.toLowerCase();

            //replace any non word characters
            str = str.replaceAll("[^a-zA-Z0-9- ]", "");

            

            return str;
        }
        
    }  

    
    public static String strip_whitespace(String word){
        word = word.replaceAll("[^a-zA-Z0-9-]", "");
        return word;
    }
    
    
    public String[] clean_line(String q){
        
        StringBuilder sb = new StringBuilder();
        
        for (String w:q.split(" ")){
            sb.append(tidyWord(w)).append(" ");
            
        }
        
        return sb.toString().split(" ");
    }

	public void loadXMLFile(String fname) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(fname));
		String line = "";
		String type = "";
		String desc = "";
		String summary = "";
		String diagnosis = "";
		String num = "";
		int s, e;

		while ((line = br.readLine()) != null) {

			if (line.contains("<topic number=")) {
				s = line.indexOf("=") + 2;
				e = line.indexOf("\"", s);
				num = line.substring(s, e);

				s = line.indexOf("type") + 6;
				e = line.indexOf("\">", s);
				type = line.substring(s, e);

			}

			if (line.contains("<description>")) {
				s = line.indexOf("<description>") + 13;
				e = line.indexOf("</description>", s);
				desc = line.substring(s, e);
				desc = desc.replace("[^.,()]", " ").toLowerCase();
			}

			if (line.contains("<summary>")) {
				s = line.indexOf("<summary>") + 9;
				e = line.indexOf("</summary>", s);
				summary = line.substring(s, e);

			}

			// if (line.contains("<diagnosis>")) {
			// s = line.indexOf("<diagnosis>") + 11;
			// e = line.indexOf("</diagnosis>", s);
			// diagnosis = line.substring(s, e);
			//
			//
			//
			// }

			if (line.contains("</topic>")) {

				desc_queries.put(num, desc);
				summ_queries.put(num, summary);

			}

		}

		br.close();

	}
	
	
	static void runQuery(HashMap<String, String> Queryhash_map, String path) throws Exception
	{
		File file_write = new File(path);
		file_write.createNewFile();
		// creates a FileWriter Object
		FileWriter writer = new FileWriter(file_write);
		
		Set set = Queryhash_map.entrySet();
		Iterator iterator = set.iterator();
		
		while(iterator.hasNext())
		{
			Map.Entry<String, String> entry = (Map.Entry)iterator.next();
			
			String num = entry.getKey();
			String text = entry.getValue();
			
			int rank = 0;
			System.out.println(text + " " + rank);
			TopDocs foundDocs1 = searchQuery(text, searcher);
			for (ScoreDoc sd : foundDocs1.scoreDocs) {
				Document d = searcher.doc(sd.doc);
				rank = rank + 1;
				String a = d.get("id");
				writer.write(num + " Q0 " + a + " " + rank + " " + sd.score + " $team5-$CDS-runFile " + "\n");
				writer.flush();

		}
			
			writer.close();
		}

	}
	

	static void QuerySearch(ArrayList<String> Querylist, ArrayList<String> numList, String path) throws Exception {
		File file_write = new File(path);
		file_write.createNewFile();
		// creates a FileWriter Object
		FileWriter writer = new FileWriter(file_write);

		for (int i = 0; i < Querylist.size(); i++) {
			String text = strip_whitespace(Querylist.get(i));

			String num = numList.get(i);
			int rank = 0;

			
			TopDocs foundDocs1 = searchQuery(text.toLowerCase(), searcher);
			for (ScoreDoc sd : foundDocs1.scoreDocs) {
				Document d = searcher.doc(sd.doc);
				rank = rank + 1;
				String a = d.get("id");
				//System.out.println(num + " " + text + "\n" );
				writer.write(num + " Q0 " + a + " " + rank + " " + sd.score + " $team5-$CDS-runFile " + "\n");
				writer.flush();

			}

		}
		
		writer.close();

	}


	public static List<String> parseKeywords(Analyzer analyzer, String query) throws IOException {

		List<String> result = new ArrayList<String>();
		TokenStream stream = analyzer.tokenStream(null, new StringReader(query));
		stream.reset();
		try {
			while (stream.incrementToken()) {
				result.add(stream.getAttribute(CharTermAttribute.class).toString());
			}
		} catch (IOException e) {
			// not thrown b/c we're using a string reader...
		}
		stream.close();
		return result;
	}

	/*
	 * Argument 1 - Query String returns total number of results
	 */
	private static TopDocs searchQuery(String query, IndexSearcher searcher) throws Exception {
		QueryParser qp = new QueryParser("contents", new StandardAnalyzer());
		Query QueryString = qp.parse(query);
		TopDocs hits = searcher.search(QueryString, 15); // Display 10 lines
		return hits;
	}

	/*
	 * Arg 1 - relative path to the index directory
	 */
	public static IndexSearcher createSearcher(String INDEX_DIR) throws IOException {
		String index_dir = INDEX_DIR;
		Directory dir = FSDirectory.open(Paths.get(index_dir));
		IndexReader reader = DirectoryReader.open(dir);
		IndexSearcher searcher = new IndexSearcher(reader);
		return searcher;
	}
}
