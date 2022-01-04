package uk.org.whitecottage.swagger;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Tag {
	private String name = "Tag";
	private String description;
	private ExternalDocumentation externalDocs;
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public ExternalDocumentation getExternalDocs() {
		return externalDocs;
	}
	
	public void setExternalDocs(ExternalDocumentation externalDocs) {
		this.externalDocs = externalDocs;
	}
}
