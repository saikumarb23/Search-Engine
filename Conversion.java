package lucestest;

import java.io.File;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.store.FSDirectory;
import com.google.gson.JsonElement;
public class Conversion {
	public static void main(String args[])
	{
	try
	{
	File path1=new File("\\S:\\callidus\\Search-Engine-master\\testdata");
	FSDirectory fs=FSDirectory.open(path1);
	JsonElement jsonDoc;
	Luces lucesConverter = new Luces(org.apache.lucene.util.Version.LUCENE_29);
	Document doc =new Document();
	@SuppressWarnings("deprecation")
	IndexReader r= IndexReader.open(fs);
	for (int i=0; i<r.maxDoc(); i++) {
	    if (r.isDeleted(i))
	        continue;
	    doc = r.document(i);
	    doc=r.document(i);
	    jsonDoc = lucesConverter.documentToJSON(doc);
	    System.out.println(jsonDoc);
	}
	}
	catch(Exception e)
	{
		System.out.println(e);
	}
	
	}
}
