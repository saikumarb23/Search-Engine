package lucestest;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Fieldable;

import com.google.gson.JsonElement;

/**
 * Provides a conversion from Lucene objects to a typed value (for Elasticsearch).
 *
 * @author Doug Schroeder
 * @since 12/2/15.
 */
public interface LucesConverter {

	/**
	 * @param field the field to extract the typed value for
	 * @return the value (converted from {@link Fieldable#stringValue()} to the appropriate type for Elasticsearch
	 */
	Object getFieldValue(Fieldable field);

	/**
	 * @param name the field name
	 * @param value the field value, as a string
	 * @return he value (converted from value) to the appropriate type for Elasticsearch
	 */
	Object getFieldValue(String name, String value);

	/**
	 * Convert the document to a JSON representation of a Lucene document for indexing into Elasticsearch
	 *
	 * @param doc the Lucene document
	 * @return the JSON representation of the document
	 */
	JsonElement documentToJSON(Document doc);

	/**
	 * Gets a string representation of the JSON object that the document has been converted to
	 *
	 * @param doc            the Lucene document to convert
	 * @param setPrettyPrint pretty print the JSON string
	 * @return the String version of the JSON version of a document
	 */
	String documentToJSONStringified(Document doc, boolean setPrettyPrint);
}
