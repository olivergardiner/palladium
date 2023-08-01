package uk.org.whitecottage.palladium.util.profile;

import org.eclipse.emf.common.util.EList;
import org.eclipse.uml2.uml.Package;
import org.eclipse.uml2.uml.Profile;

public class ProfileUtil {
	private ProfileUtil() {
		throw new IllegalStateException("Utility class");
	}
	
	public static Profile getProfile(Package pkg) {
		Profile profile = pkg.getAppliedProfile("ldml");

		EList<Profile> appliedProfiles = pkg.getAllAppliedProfiles();
		for (Profile p: appliedProfiles) {
			String pn = p.getQualifiedName();
			if (pn.equals("ldml")) {
				return p;
			}
		}
				
		return profile;
	}
}
