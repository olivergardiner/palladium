package uk.org.whitecottage.swagger;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class MediaTypeObject {
	private Schema schema;
	private Object example;
	private Map<String, Example> examples;
	private Map<String, EncodingObject> encoding;

	public Schema getSchema() {
		return schema;
	}

	public void setSchema(Schema schema) {
		this.schema = schema;
	}

	public Object getExample() {
		return example;
	}

	public void setExample(Map<String, Object> example) {
		this.example = example;
	}

	public Map<String, Example> getExamples() {
		return examples;
	}

	public void setExamples(Map<String, Example> examples) {
		this.examples = examples;
	}

	public Map<String, EncodingObject> getEncoding() {
		return encoding;
	}

	public void setEncoding(Map<String, EncodingObject> encoding) {
		this.encoding = encoding;
	}

	public EncodingObject putEncoding(String key, EncodingObject value) {
		if (encoding == null) {
			encoding = new HashMap<>();
		}
		
		return encoding.put(key, value);
	}

	public void putAllEncodings(Map<? extends String, ? extends EncodingObject> m) {
		if (encoding == null) {
			encoding = new HashMap<>();
		}
		
		encoding.putAll(m);
	}

	public Example putExample(String key, Example value) {
		return examples.put(key, value);
	}

	public void putAllExamples(Map<? extends String, ? extends Example> m) {
		examples.putAll(m);
	}
}
