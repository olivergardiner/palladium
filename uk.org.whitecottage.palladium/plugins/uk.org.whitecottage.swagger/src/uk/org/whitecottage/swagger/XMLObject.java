package uk.org.whitecottage.swagger;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class XMLObject {
	private String name;
	private String namespace;
	private String prefix;
	private boolean attribute = false;
	private boolean wrapped = false;
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getNamespace() {
		return namespace;
	}
	
	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}
	
	public String getPrefix() {
		return prefix;
	}
	
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}
	
	public boolean isAttribute() {
		return attribute;
	}
	
	public void setAttribute(boolean attribute) {
		this.attribute = attribute;
	}
	
	public boolean isWrapped() {
		return wrapped;
	}
	
	public void setWrapped(boolean wrapped) {
		this.wrapped = wrapped;
	}
}
