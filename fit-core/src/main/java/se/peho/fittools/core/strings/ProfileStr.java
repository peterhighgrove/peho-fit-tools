package se.peho.fittools.core.strings;

import com.garmin.fit.Sport;
import com.garmin.fit.SubSport;

public class ProfileStr {
    private String name = null;

    public ProfileStr(String profileName, Sport sport, SubSport subSport) {
        if (profileName != null && !profileName.isEmpty()) {
            this.name = profileName
                .replace(",", ".")
                .replace("-(bike)", "")
                .replace("-bike", "")
                ;
        } else if (sport != null) {
            name = sport.toString().toLowerCase();
            if (subSport != null) {
                name += "-" + subSport.toString().toLowerCase();
                name = name
                    .replace("fitness_equipment-", "")
                    ;
            }
        }
        System.out.println("  Extracted info: Profile=" + profileName
            + ", Sport=" + sport.toString()
            + ", SubSport=" + subSport.toString()
            + " => FormattedProfileName='" + name + "'"
        );
    }
            
    public String get() {
        return name;
    }

    public static String get(String profileName, Sport sport, SubSport subSport) {
        return new ProfileStr(profileName, sport, subSport).get();
    }

}
