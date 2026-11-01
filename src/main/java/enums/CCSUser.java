package enums;

import utils.ConfigReader;

/**
 * Users of the CCS project. Credentials come from config.properties (or the matching
 * -D property / env var, see ConfigReader.get) - never hardcode them in tests.
 */
public enum CCSUser {
    ENTERPRISE("ccs.enterprise.username", "ccs.enterprise.password");

    private final String usernameKey;
    private final String passwordKey;

    CCSUser(String usernameKey, String passwordKey) {
        this.usernameKey = usernameKey;
        this.passwordKey = passwordKey;
    }

    public String getUsername() {
        return ConfigReader.get(usernameKey);
    }

    public String getPassword() {
        return ConfigReader.get(passwordKey);
    }
}
