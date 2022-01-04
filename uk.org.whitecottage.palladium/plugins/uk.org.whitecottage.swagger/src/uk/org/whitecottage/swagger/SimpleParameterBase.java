package uk.org.whitecottage.swagger;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

import uk.org.whitecottage.swagger.Parameter.Style;

@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class SimpleParameterBase extends ParameterBase {
	private String style;
	private boolean explode = false;
	private boolean allowReserved = false;
	private Schema schema;
	private Map<String, Object> example;
	private Map<String, Example> examples;
	
	public String getStyle() {
		return style;
	}

	public void setStyle(Style style) {
		switch (style) {
		case MATRIX:
			this.style = "matrix";
			break;
		case LABEL:
			this.style = "label";
			break;
		case FORM:
			this.style = "form";
			break;
		case SIMPLE:
			this.style = "simple";
			break;
		case SPACE_DELIMITED:
			this.style = "spaceDelimited";
			break;
		case PIPE_DELIMITED:
			this.style = "pipeDelimited";
			break;
		case DEEP_OBJECT:
			this.style = "deepObject";
			break;
		default:
			this.style = "matrix";
			break;
		}
	}

	public boolean isExplode() {
		return explode;
	}

	public void setExplode(boolean explode) {
		this.explode = explode;
	}

	public boolean isAllowReserved() {
		return allowReserved;
	}

	public void setAllowReserved(boolean allowReserved) {
		this.allowReserved = allowReserved;
	}

	public Schema getSchema() {
		return schema;
	}

	public Map<String, Object> getExample() {
		return example;
	}

	public void setExample(Map<String, Object> example) {
		this.example = example;
		this.examples = null;
	}

	public Map<String, Example> getExamples() {
		return examples;
	}

	public void setExamples(Map<String, Example> examples) {
		this.examples = examples;
		this.example = null;
	}
}
