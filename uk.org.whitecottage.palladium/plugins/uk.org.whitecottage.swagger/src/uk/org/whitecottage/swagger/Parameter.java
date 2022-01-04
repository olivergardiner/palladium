package uk.org.whitecottage.swagger;

public interface Parameter {
	public enum In {
		QUERY,
		HEADER,
		PATH,
		COOKIE
	}
	
	public enum Style {
		MATRIX,
		LABEL,
		FORM,
		SIMPLE,
		SPACE_DELIMITED,
		PIPE_DELIMITED,
		DEEP_OBJECT
	}
}
