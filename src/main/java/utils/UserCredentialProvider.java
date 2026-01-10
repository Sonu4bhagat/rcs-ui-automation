package utils;

import utils.ConfigReader;
import enums.UserRole;

public class UserCredentialProvider {

    public static String getEmail(UserRole role) {
        switch (role) {
            case SUPERADMIN:
                return ConfigReader.get("superadmin.email");
            case ENTERPRISE:
                return ConfigReader.get("enterprise.email");
            case RESELLER:
                return ConfigReader.get("reseller.email");
            default:
                throw new IllegalArgumentException("Unknown user role");
        }
    }

    public static String getPassword(UserRole role) {
        switch (role) {
            case SUPERADMIN:
                return ConfigReader.get("superadmin.password");
            case ENTERPRISE:
                return ConfigReader.get("enterprise.password");
            case RESELLER:
                return ConfigReader.get("reseller.password");
            default:
                throw new IllegalArgumentException("Unknown user role");
        }
    }
}
