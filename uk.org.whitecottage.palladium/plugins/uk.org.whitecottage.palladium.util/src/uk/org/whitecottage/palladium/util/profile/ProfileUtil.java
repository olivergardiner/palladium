package uk.org.whitecottage.palladium.util.profile;

import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.Profile;

public class ProfileUtil {
	private ProfileUtil() {
		throw new IllegalStateException("Utility class");
	}
	
	public static Profile getProfile(Model model) {
		Profile profile = model.getAppliedProfile("pldm.profile", true);
		
		if (profile != null) {
			return profile;
		}
		
		profile = model.getAppliedProfile("ldml", true);
		
		return profile;
	}
}
