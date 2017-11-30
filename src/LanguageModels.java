import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.similarities.BasicStats;
import org.apache.lucene.search.similarities.SimilarityBase;
import org.apache.lucene.search.similarities.LMDirichletSimilarity;
import org.apache.lucene.search.similarities.LMSimilarity;
import org.apache.lucene.search.similarities.LMDirichletSimilarity;
import org.apache.lucene.search.similarities.LMSimilarity.DefaultCollectionModel;

import co.nstant.in.cbor.CborException;
import lucene.*;
import treccar.*;

public class LanguageModels {

	static String TEST200_DIR = "/Users/Nithin/Desktop/test200/train.test200.cbor.paragraphs";
	static String INDEX_DIR = "/Users/Nithin/Desktop/freshnewIndex";
	static String OUTLINES_DIR = "/Users/Nithin/Desktop/test200/train.test200.cbor.outlines";
	static String OUTPUT_FILE_PATH = "/Users/Nithin/Desktop/outputfile";
	static String OUTPUT_FILE_PATH_CUSTOM_SCORING_FUNCTION = "/Users/Nithin/Desktop/outputfile_custom";
	static String OUTPUT_FILE_PATH_UJM = "/Users/Nithin/Desktop/outputfile/runfile_UJM";
	static String OUTPUT_FILE_PATH_UDS = "/Users/Nithin/Desktop/outputfile/runfile_UDS";
	static String OUTPUT_FILE_PATH_UL = "/Users/Nithin/Desktop/outputfile/runfile_UL";
	static String OUTPUT_FILE_PATH_Bigram = "/Users/Nithin/Desktop/outputfile/runfile_Bi";

	static int VocabularySize = 0;

	static String query = "Brush%20rabbit";

	// Unigram language model with Jelinek-Mercer smoothing (λ = 0.9)
	public static void UJM() throws Exception {

		SimilarityBase UJM_similarity = new SimilarityBase() {
			@Override
			public String toString() {
				// TODO Auto-generated method stub
				return null;
			}

			DefaultCollectionModel model = new LMSimilarity.DefaultCollectionModel();
			float lambda = 0.1f;

			@Override
			protected float score(BasicStats stats, float termFreq, float docLength) {
				// TODO Auto-generated method stub
				return (1 - lambda) * model.computeProbability(stats) + lambda * (termFreq / docLength);
			}
		};

		Search search = new Search(UJM_similarity, INDEX_DIR, OUTPUT_FILE_PATH_UJM);
		Search search1 = new Search(UJM_similarity, INDEX_DIR, OUTPUT_FILE_PATH_UJM, query);
	}

	// Unigram language model with Dirichlet smoothing (μ = 1000)
	public static void UDS() throws Exception {

		SimilarityBase UDS_similarity = new SimilarityBase() {
			@Override
			public String toString() {
				// TODO Auto-generated method stub
				return null;
			}

			DefaultCollectionModel model = new LMSimilarity.DefaultCollectionModel();
			float mew = 1000;

			@Override
			protected float score(BasicStats stats, float termFreq, float docLength) {
				// TODO Auto-generated method stub
				return (termFreq + mew * model.computeProbability(stats)) / (docLength + mew);
			}
		};

		Search search = new Search(UDS_similarity, INDEX_DIR, OUTPUT_FILE_PATH_UDS);
		Search search1 = new Search(UDS_similarity, INDEX_DIR, OUTPUT_FILE_PATH_UDS, query);
	}

	// Helper method to determine VocabSize
	public static int VocabSize(ArrayList<String> Unilist) throws CborException, IOException {

		ArrayList<String> vlist = new ArrayList<String>();

		Set<String> s = new HashSet<>();

		s.addAll(Unilist);

		vlist.addAll(s);

		return vlist.size();

	}

	// Tokenizer/ parser will add to Array list
	public static List<String> parseKeywords(String query) throws IOException {

		Analyzer analyzer = new StandardAnalyzer();
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

	// Unigram language model with Laplace smoothing (α = 1)
	public static void UL() throws Exception {

		String paragraphs = TEST200_DIR;
		final FileInputStream fileInputStream2 = new FileInputStream(new File(paragraphs));
		for (Data.Paragraph p : DeserializeData.iterableParagraphs(fileInputStream2)) {

			String paraId = p.getParaId().toString();
			String paraText = p.getTextOnly().toString();

			ArrayList<String> Unilist = (ArrayList<String>) parseKeywords(paraText);

			VocabularySize = VocabSize(Unilist);

		}

		SimilarityBase UL_similarity = new SimilarityBase() {
			@Override
			public String toString() {
				return null;
			}

			DefaultCollectionModel model = new LMSimilarity.DefaultCollectionModel();

			@Override
			protected float score(BasicStats stats, float termFreq, float docLength) {

				return (termFreq + 1) / (docLength + VocabularySize);
			}

		};

		Search search = new Search(UL_similarity, INDEX_DIR, OUTPUT_FILE_PATH_UL);

		Search search1 = new Search(UL_similarity, INDEX_DIR, OUTPUT_FILE_PATH_UL, query);

	}

	// Bigram language model with Laplace smoothing (α = 1)
	public static void BigramAdd1() throws Exception {
		SimilarityBase BigramSim = new SimilarityBase() {

			@Override
			public String toString() {
				// TODO Auto-generated method stub
				return null;
			}

			float bigram = 0.0f;

			@Override
			protected float score(BasicStats stats, float termfreq, float docLen) {
				// TODO Auto-generated method stub
				bigram = (termfreq + 1) / (docLen + stats.getAvgFieldLength() + VocabularySize);
				return bigram;
			}
		};

		Search search = new Search(BigramSim, INDEX_DIR, OUTPUT_FILE_PATH_Bigram);

		Search search1 = new Search(BigramSim, INDEX_DIR, OUTPUT_FILE_PATH_Bigram, query);

	}

	

	/** Simple command-line based search demo. */
	public static void main(String[] args) throws Exception {

		// LNC_LTN();
		// BNN_BNN();
		// ANC_APC();

		Indexer index = new Indexer(TEST200_DIR, INDEX_DIR);

		UJM();

		UDS();

		UL();

		BigramAdd1();

		System.out.println("File write finished \n");

	}

}
