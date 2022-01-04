package uk.org.whitecottage.swagger;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ComplexParameterObject extends ComplexParameterBase implements Parameter {
	private String name = "/";
	private String in = "path";

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getIn() {
		return in;
	}
	
	public void setIn(In in) {
		switch (in) {
		case QUERY:
			this.in = "query";
			break;
		case HEADER:
			this.in = "header";
			break;
		case PATH:
			this.in = "path";
			break;
		case COOKIE:
			this.in = "cookie";
			break;
		default:
			this.in = "query";
			break;
		}
	}
}
