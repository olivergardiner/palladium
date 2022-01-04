package uk.org.whitecottage.swagger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class PathItemObject {
	private String $ref;
	private String summary;
	private String description;
	private OperationObject get;
	private OperationObject put;
	private OperationObject post;
	private OperationObject delete;
	private OperationObject options;
	private OperationObject head;
	private OperationObject patch;
	private OperationObject trace;
	private List<Server> servers;	
	private List<Parameter> parameters;
	
	public PathItemObject() {
	}
	
	public String get$ref() {
		return $ref;
	}

	public void set$ref(String $ref) {
		this.$ref = $ref;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public OperationObject getGet() {
		return get;
	}

	public void setGet(OperationObject get) {
		this.get = get;
	}

	public OperationObject getPut() {
		return put;
	}

	public void setPut(OperationObject put) {
		this.put = put;
	}

	public OperationObject getPost() {
		return post;
	}

	public void setPost(OperationObject post) {
		this.post = post;
	}

	public OperationObject getDelete() {
		return delete;
	}

	public void setDelete(OperationObject delete) {
		this.delete = delete;
	}

	public OperationObject getOptions() {
		return options;
	}

	public void setOptions(OperationObject options) {
		this.options = options;
	}

	public OperationObject getHead() {
		return head;
	}

	public void setHead(OperationObject head) {
		this.head = head;
	}

	public OperationObject getPatch() {
		return patch;
	}

	public void setPatch(OperationObject patch) {
		this.patch = patch;
	}

	public OperationObject getTrace() {
		return trace;
	}

	public void setTrace(OperationObject trace) {
		this.trace = trace;
	}

	public List<Server> getServers() {
		return servers;
	}

	public void setServers(List<Server> servers) {
		this.servers = servers;
	}

	public List<Parameter> getParameters() {
		return parameters;
	}

	public void setParameters(List<Parameter> parameters) {
		this.parameters = parameters;
	}

	public boolean addServer(Server e) {
		if (servers == null) {
			servers = new ArrayList<>();
		}
		
		return servers.add(e);
	}

	public boolean addAllServers(Collection<? extends Server> c) {
		if (servers == null) {
			servers = new ArrayList<>();
		}
		
		return servers.addAll(c);
	}

	public boolean addParameter(Parameter e) {
		if (parameters == null) {
			parameters = new ArrayList<>();
		}
		
		return parameters.add(e);
	}

	public boolean addAllParameters(Collection<? extends Parameter> c) {
		if (parameters == null) {
			parameters = new ArrayList<>();
		}
		
		return parameters.addAll(c);
	}

}