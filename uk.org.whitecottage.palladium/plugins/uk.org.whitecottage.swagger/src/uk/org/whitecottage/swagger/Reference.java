package uk.org.whitecottage.swagger;

public abstract class Reference {
	private String $ref = "";
	
	public Reference(String $ref) {
		this.$ref = $ref;
	}

	public String get$ref() {
		return $ref;
	}

	public void set$ref(String $ref) {
		this.$ref = $ref;
	}
}
