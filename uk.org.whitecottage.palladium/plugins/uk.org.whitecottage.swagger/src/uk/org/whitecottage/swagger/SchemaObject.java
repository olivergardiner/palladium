package uk.org.whitecottage.swagger;

public class SchemaObject extends JsonSchema implements Schema {
	private boolean nullable = false;
	private Discriminator discriminator;
	private boolean readOnly = false;
	private boolean writeOnly = false;
	private XMLObject xml;
	private ExternalDocumentation externalDocs;
	private Object example;
	private boolean deprecated = false;
	
	public boolean isNullable() {
		return nullable;
	}
	
	public void setNullable(boolean nullable) {
		this.nullable = nullable;
	}
	
	public Discriminator getDiscriminator() {
		return discriminator;
	}
	
	public void setDiscriminator(Discriminator discriminator) {
		this.discriminator = discriminator;
	}
	
	public boolean isReadOnly() {
		return readOnly;
	}
	
	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}
	
	public boolean isWriteOnly() {
		return writeOnly;
	}
	
	public void setWriteOnly(boolean writeOnly) {
		this.writeOnly = writeOnly;
	}
	
	public XMLObject getXml() {
		return xml;
	}
	
	public void setXml(XMLObject xml) {
		this.xml = xml;
	}
	
	public ExternalDocumentation getExternalDocs() {
		return externalDocs;
	}
	
	public void setExternalDocs(ExternalDocumentation externalDocs) {
		this.externalDocs = externalDocs;
	}
	
	public Object getExample() {
		return example;
	}
	
	public void setExample(Object example) {
		this.example = example;
	}
	
	public boolean isDeprecated() {
		return deprecated;
	}
	
	public void setDeprecated(boolean deprecated) {
		this.deprecated = deprecated;
	}
}
