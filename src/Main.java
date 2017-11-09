import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import tools.DataSource;
import tools.XMLReader;

public class Main {

	static String number;
	static String type;
	static String note;
	static String description;
	static String summary;

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		System.out.println(number + " " + summary + "\n");

		String filename = "/Users/Nithin/Desktop/topics2016.xml";
		 for(DataSource.topics t : XMLReader.iterableTopics(filename))
		 {
			 
			
		 number = t.getNumber();
		 type = t.getType();
		 note = t.getNote();
		 description = t.getDescription();
		 summary = t.getSummary();
		
		
		
		 System.out.println(number + " " + summary + "\n");
		 }

		
		

	}
}
