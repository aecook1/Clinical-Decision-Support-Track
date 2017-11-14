package index;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Date;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.jsoup.Jsoup;
import org.jsoup.safety.Cleaner;
import org.jsoup.safety.Whitelist;
import org.jsoup.nodes.Node;

import co.nstant.in.cbor.CborException;

public class Indexer {

	public static int doc_types;

	private static IndexWriter writer;

	private final Analyzer analyzer;
	private int docs = 0;

	public Indexer(String INDEX_DIR) throws IOException {

		System.setProperty("file.encoding", "UTF-8");
		

		

		

		Directory dir = FSDirectory.open(Paths.get(INDEX_DIR));
		analyzer = new EnglishAnalyzer();
		IndexWriterConfig config = new IndexWriterConfig(analyzer);
		writer = new IndexWriter(dir, config);


		

	}
	
	


	void index_TREC_Directory_Files(File dir) throws IOException {

		
		
		File files[] = dir.listFiles();

		if (files != null) {
			for (int i = 0; i < files.length; i++) {
				if (files[i].isDirectory()) {
					System.out.println(files[i].getName() + "\n");
					index_TREC_Directory_Files(files[i]);
				}

				else {
					
					System.out.println("---" +files[i].getName().toString()+ "\n");
					index_TREC_File(files[i].getName());
				}
					
		}
	}
	}

	void index_TREC_File(String filename) throws IOException
	{
		BufferedReader br;
		br = new BufferedReader(new FileReader(filename));

		String line;
		String[] terms;
		String doc_id = null;
		String text;

		String start_delim = "<article-id pub-id-type=\"pmc\">";
		String end_delim = "</article-id>";
		StringBuilder sb = new StringBuilder();
		while ((line = br.readLine()) != null) {

			sb.append(line);

			/**
			 * get the id
			 */
			if (line.contains(start_delim)) {
				int start = line.indexOf(start_delim) + start_delim.length();
				int end = line.indexOf(end_delim, start);
				doc_id = line.substring(start, end);

				// logger.info("id " + doc_id);
			}

		}
		
		text = convert(sb.toString().replace("><", "> <"));
		
		System.out.println(doc_id + " --- " + text + "/n");
		createIndex(doc_id, text);

		br.close();

	}

	
	
	/**
	 * uses jJSOUP
	 * 
	 * @param html
	 * @return
	 */
	private synchronized String convert(String html) {

		org.jsoup.nodes.Document doc = Jsoup.parse(html);
		// Document doc = Jsoup.parse(html);
		removeComments(doc);
		doc = new Cleaner(Whitelist.relaxed()).clean(doc);

		String str = doc.text();

		str = str.replaceAll("/", " ");
		str = str.replaceAll("\n", " ");

		return str;
	}

	private synchronized static void removeComments(Node node) {
		for (int i = 0; i < node.childNodes().size();) {
			Node child = node.childNode(i);
			if (child.nodeName().equals("#comment")) {
				child.remove();
			} else {
				removeComments(child);
				i++;
			}
		}
	}

	

	/*
	 * createIndex - Responsible for creating Index file Two Fields are added to
	 * the index document Field 1 - id, Field 2 - contents
	 */
	static void createIndex(String id, String text) throws IOException {
		Document doc = new Document();
		doc.add(new StringField("id", id, Field.Store.YES));
		doc.add(new TextField("contents", text, Field.Store.YES));

		writer.addDocument(doc);

	}

	public void close() throws IOException {
		writer.close();
	}
	
	
	public static void main(String[] args) throws IOException
	{
		String TREC_FILE = "/Users/Nithin/Desktop/PubMedDatas/pmc-00/00/13900.nxml";
		String INDEX_DIR = "/Users/Nithin/Desktop/ClinicalIndex";
		Date start = new Date();

		Indexer index = new Indexer(INDEX_DIR);
		
		File dir_Name = new File(TREC_FILE);
		//index.index_TREC_Directory_Files(dir_Name);
		
		index.index_TREC_File(TREC_FILE);
		
		index.close();
		Date end = new Date();
		System.out.println(end.getTime() - start.getTime() + " total milliseconds");
	}

}
