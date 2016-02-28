package lucestest;

public interface LucesMapper<MappingTypeT> {

	/**
	 * Specify a mapping JSON object to be able to convert document fields to their proper types
	 *
	 * @param typeName Name of the type. Set to null to reset the mapping file and only output strings for the field
	 *                 values. Throws an error if the typeName doesn't match the root in the mapping, or if the mapping
	 *                 is invalid.
	 * @param mapping  mapping JSON object.
	 * @return this
	 */
	@SuppressWarnings("rawtypes")
	LucesMapper mapping(String typeName, MappingTypeT mapping);

	/**
	 * Flag for setting default values for empty strings. Only used when there is a mapping file to
	 * determine field types. Otherwise empty strings will throw a parsing error
	 * Defaults are:
	 * * 0 for int / long
	 * * 0.0 for float / double
	 * * false for boolean
	 *
	 * @param useDefaults whether to use defaults when an empty string is encountered for a non-string type.
	 *                    If set to true, useNull will be set to false
	 *
	 * @return this
	 */
	@SuppressWarnings("rawtypes")
	LucesMapper useDefaultsForEmpty(boolean useDefaults);

	/**
	 * Use a null value when an empty string is encountered. If set to true, useDefaults will be set to false
	 *
	 * @param useNull whether to use a null value when an empty string is encountered for a non-string type
	 * @return this
	 */
	@SuppressWarnings("rawtypes")
	LucesMapper useNullForEmpty(boolean useNull);
}
