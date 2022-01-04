package uk.org.whitecottage.palladium.datavault;

import java.util.HashMap;
import java.util.Map;

public class Preferences {
	protected Map<String, Object> preferenceValues;
	
	public static final String APPLY_FOREIGN_KEYS = "applyForeignKeys";
	public static final String APPLY_FOREIGN_KEYS_RD_DV = "applyForeignKeysReferenceDataInDatavault";
	public static final String APPLY_FOREIGN_KEYS_RD_RD = "applyForeignKeysReferenceDataInReferenceData";
	public static final String CREATE_TYPES_DV = "createDatavaultTypes";
	public static final String CREATE_TYPES_RD = "createReferenceDataTypes";
	public static final String USE_CV_TABLE = "useCVTable";
	public static final String USE_NATURAL_KEYS_RD = "useNaturalKeysReferenceData";
	public static final String USE_ENUM_TABLE = "useEnumerationTable";
	
	public Preferences() {
		preferenceValues = new HashMap<>();
		preferenceValues.put(APPLY_FOREIGN_KEYS, true);
		preferenceValues.put(APPLY_FOREIGN_KEYS_RD_DV, true);
		preferenceValues.put(APPLY_FOREIGN_KEYS_RD_RD, true);
		preferenceValues.put(CREATE_TYPES_DV, true);
		preferenceValues.put(CREATE_TYPES_RD, true);
		preferenceValues.put(USE_CV_TABLE, true);
		preferenceValues.put(USE_NATURAL_KEYS_RD, true);
		preferenceValues.put(USE_ENUM_TABLE, true);
	}

	public boolean isPreference(String key) {
		return (Boolean) preferenceValues.get(key);
	}
	
	public void setPreference(String key, boolean preference) {
		preferenceValues.put(key, Boolean.valueOf(preference));
	}
	
	public Map<String, Object> getPreferences() {
		return preferenceValues;
	}
}
