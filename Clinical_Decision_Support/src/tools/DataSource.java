package tools;

import java.util.ArrayList;

public class DataSource {

	/*
	 * This class is responsible for retrieving topic queries
	 */
	public final static class topics {
//		private final String number;
//		private final String type;
//		private final String note;
//		private final String description;
//		private final String summary;
		
		ArrayList<String> number = new ArrayList<>();
		ArrayList<String> type = new ArrayList<>();
		ArrayList<String> note = new ArrayList<>();
		ArrayList<String> description = new ArrayList<>();
		ArrayList<String> summary = new ArrayList<>();

		public topics(ArrayList<String> numberlist, ArrayList<String> typelist, ArrayList<String> notelist, ArrayList<String> descriptionlist, ArrayList<String> summarylist) {
			this.number = numberlist;
			this.type = typelist;
			this.note = notelist;
			this.description = descriptionlist;
			this.summary = summarylist;

		}

		public ArrayList<String> getNumber() {
			return number;
		}
		
		public ArrayList<String> getType() {
			return type;
		}
		
		public ArrayList<String> getNote()
		{
			return note;
		}
		
		public ArrayList<String> getDescription()
		{
			return description;
		}
		
		public ArrayList<String> getSummary()
		{
			return summary;
		}
		
		
		
		

	}


	
	
//	public final static class PubMed()
//	{
//		private final String Pubmed;
//		
//	}

}
