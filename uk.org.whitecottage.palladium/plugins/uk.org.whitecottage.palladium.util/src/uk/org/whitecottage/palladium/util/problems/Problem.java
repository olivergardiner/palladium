package uk.org.whitecottage.palladium.util.problems;

public class Problem {
	protected String description = "";

	public enum Severity {
		INFO,
		WARNING,
		ERROR
	};
	
	protected Severity severity = Severity.WARNING;
	protected String path;
	
	public Problem(String description, Severity severity, String path) {
		this.description = description;
		this.severity = severity;
		this.path = path;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Severity getSeverity() {
		return severity;
	}

	public void setSeverity(Severity severity) {
		this.severity = severity;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
}
