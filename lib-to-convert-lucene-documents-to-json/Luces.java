package lucestest;

import java.util.ArrayList;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Fieldable;
import org.apache.lucene.util.Version;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;


/**
 * Utility class for converting Lucene Documents to a JSON format for consumption by Elasticsearch. This class is NOT threadsafe.
 *
 * @author Brian Harrington
 */
public class Luces implements LucesConverter, LucesMapper<JsonObject> {

	private enum ParseType {
		BYTE, SHORT, INTEGER, LONG, FLOAT, DOUBLE, BOOLEAN, STRING
	}

	private String typeName;
	private Map<String, ParseType> typeMap;
	private boolean useDefaults;
	private boolean useNull;

	//@SuppressWarnings("unused")
	public Luces(Version version) {
		if (!version.equals(Version.LUCENE_29)) {
			throw new UnsupportedOperationException("This library does not support Lucene version ");
		}
	}

	@Override
	public Luces mapping(String typename, JsonObject mapping) {
		if (null == typename || null == mapping) {
			this.typeName = null;
			this.typeMap = null;
		} else {
			this.typeName = typename;
			this.typeMap = new HashMap<>();
			JsonObject workingJson = mapping.getAsJsonObject(typename);
			if (null == workingJson) {
				throw new NoSuchElementException(typename + " type not present or misnamed in mapping");
			}
			// TODO account for nesting
			workingJson = workingJson.getAsJsonObject("properties");
			for (Entry<String, JsonElement> entry : workingJson.entrySet()) {
				JsonElement typeElt = entry.getValue().getAsJsonObject().get("type");
				if (null == typeElt) {
					throw new NoSuchElementException("Invalid mapping: No type defined for " + entry.getKey() + " field.");
				}
				ParseType parseType;
				try {
					parseType = ParseType.valueOf(typeElt.getAsString().toUpperCase());
				} catch (UnsupportedOperationException ex) {
					throw new UnsupportedOperationException("Invalid Mapping: Type defined is not a string: " + typeElt.toString());
				} catch (IllegalArgumentException illegal) {
					throw new UnsupportedOperationException("The " + typeElt.getAsString() + " type is not supported for conversion");
				}
				if (null != parseType && !ParseType.STRING.equals(parseType)) { // don't need to store info on strings
					typeMap.put(entry.getKey(), parseType);
				}
			}
		}
		return this;
	}

	@Override
	public Luces useDefaultsForEmpty(boolean usedefaults) {
		this.useDefaults = usedefaults;
		if (usedefaults) {
			useNull = false;
		}
		return this;
	}

	@Override
	public Luces useNullForEmpty(boolean usenull) {
		this.useNull = usenull;
		if (usenull) {
			useDefaults = false;
		}
		return this;
	}

	@Override
	public String documentToJSONStringified(Document doc, boolean setPrettyPrint) {
		Gson gson = setPrettyPrint ? new GsonBuilder().setPrettyPrinting().create() : new Gson();
		return gson.toJson(documentToJSON(doc));
	}

	@Override
	public Object getFieldValue(Fieldable field) {
		return getFieldValue(field.name(), field.stringValue());
	}

	@Override
	public Object getFieldValue(String name, String value) {
		ParseType parseType = typeMap.get(name);
		if (null == parseType) {
			parseType = ParseType.STRING;
		}
		String fieldValue = value;
		if (null == fieldValue || (useNull && "".equals(fieldValue.trim()))) {
			return JsonNull.INSTANCE;
		}
		Object parsedValue;
		try {
			switch (parseType) {
				case BYTE:
					// FALL THROUGH
				case SHORT:
					// FALL THROUGH
				case INTEGER:
					// FALL THROUGH
				case LONG:
					fieldValue = fieldValue.trim();
					fieldValue = (useDefaults && "".equals(fieldValue)) ? "0" : fieldValue;
					parsedValue = Long.parseLong(fieldValue);
					break;
				case FLOAT:
					// FALL THROUGH
				case DOUBLE:
					fieldValue = fieldValue.trim();
					fieldValue = (useDefaults && "".equals(fieldValue)) ? "0.0" : fieldValue;
					parsedValue = Double.parseDouble(fieldValue);
					break;
				case BOOLEAN:
					fieldValue = fieldValue.trim();
					// anything that doesn't match the string "true" ignoring case evaluates to false
					parsedValue = Boolean.parseBoolean(fieldValue);
					break;
				default: // leave as untrimmed string
					parsedValue = fieldValue;
					break;
			}
		} catch (NumberFormatException ex) {
			throw new NumberFormatException("Error parsing " + name + " field: " + ex.getMessage());
		}

		return parsedValue;
	}

	@Override
	public JsonElement documentToJSON(Document doc) {
		Map<String, Object> fields = new LinkedHashMap<>();
		@SuppressWarnings("unchecked")
		List<Fieldable> docFields = doc.getFields();
		if (null != typeMap && null != typeName) {
			for (Fieldable field : docFields) {
				putOrAppend(fields, field.name(), getFieldValue(field));
			}
		} else {
			for (Fieldable field : docFields) {
				putOrAppend(fields, field.name(), field.stringValue());
			}
		}
		return new Gson().toJsonTree(fields);
	}

	@SuppressWarnings("unchecked")
	private static List<Object> toObjectList(Object value) {
		return (List<Object>) value;
	}

	private void putOrAppend(Map<String, Object> fieldMap, String fieldName, Object fieldValue) {
		Object value = fieldMap.get(fieldName);
		if (value != null) {
			if (value instanceof ArrayList) {
				List<Object> values = toObjectList(value);
				values.add(fieldValue);
				fieldMap.put(fieldName, values);
			} else {
				List<Object> objects = new ArrayList<>();
				objects.add(value);
				objects.add(fieldValue);
				fieldMap.put(fieldName, objects);
			}
		} else {
			fieldMap.put(fieldName, fieldValue);
		}
	}


}

