package uk.org.whitecottage.swagger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class OpenAPISchema {
	private String openapi = "3.0.0";
	private Info info;
	private List<Server> servers;
	private Map<String, PathItemObject> paths;
	private Components components;
	private SecurityRequirement security;
	private List<Tag> tags;
	private ExternalDocumentation externalDocs;
	
	public OpenAPISchema() {
		info = new Info();
		paths = new HashMap<>();
	}

	public String getOpenapi() {
		return openapi;
	}

	public void setOpenapi(String openapi) {
		this.openapi = openapi;
	}

	public Info getInfo() {
		return info;
	}

	public List<Server> getServers() {
		return servers;
	}

	public void setServers(List<Server> servers) {
		this.servers = servers;
	}

	public Map<String, PathItemObject> getPaths() {
		return paths;
	}

	public Components getComponents() {
		return components;
	}

	public void setComponents(Components components) {
		this.components = components;
	}

	public SecurityRequirement getSecurity() {
		return security;
	}

	public void setSecurity(SecurityRequirement security) {
		this.security = security;
	}

	public List<Tag> getTags() {
		return tags;
	}

	public void setTags(List<Tag> tags) {
		this.tags = tags;
	}

	public ExternalDocumentation getExternalDocs() {
		return externalDocs;
	}

	public void setExternalDocs(ExternalDocumentation externalDocs) {
		this.externalDocs = externalDocs;
	}

	public PathItemObject putPath(String key, PathItemObject value) {
		return paths.put(key, value);
	}

	public void putAllPaths(Map<? extends String, ? extends PathItemObject> m) {
		paths.putAll(m);
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


	public List<String> put(String key, ArrayList<String> value) {
		if (security == null) {
			security = new SecurityRequirement();
		}
		
		return security.put(key, value);
	}


	public void putAll(Map<? extends String, ? extends ArrayList<String>> m) {
		if (security == null) {
			security = new SecurityRequirement();
		}
		
		security.putAll(m);
	}

	public boolean addTag(Tag e) {
		if (tags == null) {
			tags = new ArrayList<>();
		}
		
		return tags.add(e);
	}

	public boolean addAllTags(Collection<? extends Tag> c) {
		if (tags == null) {
			tags = new ArrayList<>();
		}
		
		return tags.addAll(c);
	}
}
