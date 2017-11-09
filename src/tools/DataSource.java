package tools;

public class DataSource {

	/*
	 * This class is responsible for retrieving topic queries
	 */
	public final static class topics {
		private final String number;
		private final String type;
		private final String note;
		private final String description;
		private final String summary;

		public topics(String number, String type, String note, String description, String summary) {
			this.number = number;
			this.type = type;
			this.note = note;
			this.description = description;
			this.summary = summary;

		}

		public String getNumber() {
			return number;
		}
		
		public String getType() {
			return type;
		}
		
		public String getNote()
		{
			return note;
		}
		
		public String getDescription()
		{
			return description;
		}
		
		public String getSummary()
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
