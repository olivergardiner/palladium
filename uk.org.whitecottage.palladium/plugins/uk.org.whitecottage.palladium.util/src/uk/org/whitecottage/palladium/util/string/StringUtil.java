package uk.org.whitecottage.palladium.util.string;

public interface StringUtil {
	public static String upperCamelCase(String s) {
		s = s.trim();
		if (s.isEmpty()) {
			return s;
		}

		String[] parts = s.split(" ");
		StringBuilder result = new StringBuilder();
		
		for (int i=0; i<parts.length; i++) {
			result.append(parts[i].substring(0, 1).toUpperCase() + parts[i].substring(1));
		}
		
		return result.toString();
	}
	
	public static String lowerCamelCase(String s) {
		s = s.trim();
		if (s.isEmpty()) {
			return s;
		} else if (s.length() == 1) {
			return s.toLowerCase();
		}
		
		String[] parts = s.split("\\s");
		StringBuilder result = new StringBuilder();
		result.append(parts[0].substring(0, 1).toLowerCase() + parts[0].substring(1));
		
		for (int i=1; i<parts.length; i++) {
			result.append(parts[i].substring(0, 1).toUpperCase() + parts[i].substring(1));
		}
		
		return result.toString();
	}
}
