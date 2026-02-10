package api.payloads;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class AdminPayload {

    // Common
    private String username;
    private String password;
    private String tId;

    // Customer Creation
    private String customerId;
    private String customerType;
    private String firstName;
    private String lastName;
    private String organizationName;
    private String accountManager;
    private String mobileNo;
    private String country;
    private Integer maxSvProfiles;
    private String isWhitelabeled;
    private String circleName;
    private String zoneName;

    // Wallet Creation
    private String walletName;
    private String walletMode; // SSW or UW
    private String walletType; // prepaid or postpaid
    private String billingCycle;

    // Wallet Credit/Debit
    private String action; // refill, deduct
    private Long amount;
    private Long creditLimit;
    private String reason;

    // Rate Cards & Service Accounts (Nested)
    private Map<String, Object> services; // Generic map to hold "sms", "obd", "rcs" objects dynamically if needed

    // License Updates
    private Map<String, Object> obd;
    private Map<String, Object> sms;
    private Map<String, Object> rcs;
    private Map<String, Object> ivr;
    private Map<String, Object> ccs;
    private Map<String, Object> waba;

    // Service Account Specific
    private Boolean isAccountActive;
    private String validityFrom;
    private String validityTo;
    private String trafficStartsAt;

    public AdminPayload() {
    }

    // Getters and Setters

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String gettId() {
        return tId;
    }

    public void settId(String tId) {
        this.tId = tId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getCustomerType() {
        return customerType;
    }

    public void setCustomerType(String customerType) {
        this.customerType = customerType;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public String getAccountManager() {
        return accountManager;
    }

    public void setAccountManager(String accountManager) {
        this.accountManager = accountManager;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Integer getMaxSvProfiles() {
        return maxSvProfiles;
    }

    public void setMaxSvProfiles(Integer maxSvProfiles) {
        this.maxSvProfiles = maxSvProfiles;
    }

    public String getIsWhitelabeled() {
        return isWhitelabeled;
    }

    public void setIsWhitelabeled(String isWhitelabeled) {
        this.isWhitelabeled = isWhitelabeled;
    }

    public String getCircleName() {
        return circleName;
    }

    public void setCircleName(String circleName) {
        this.circleName = circleName;
    }

    public String getZoneName() {
        return zoneName;
    }

    public void setZoneName(String zoneName) {
        this.zoneName = zoneName;
    }

    public String getWalletName() {
        return walletName;
    }

    public void setWalletName(String walletName) {
        this.walletName = walletName;
    }

    public String getWalletMode() {
        return walletMode;
    }

    public void setWalletMode(String walletMode) {
        this.walletMode = walletMode;
    }

    public String getWalletType() {
        return walletType;
    }

    public void setWalletType(String walletType) {
        this.walletType = walletType;
    }

    public String getBillingCycle() {
        return billingCycle;
    }

    public void setBillingCycle(String billingCycle) {
        this.billingCycle = billingCycle;
    }

    public Map<String, Object> getServices() {
        return services;
    }

    public void setServices(Map<String, Object> services) {
        this.services = services;
    }

    public Map<String, Object> getObd() {
        return obd;
    }

    public void setObd(Map<String, Object> obd) {
        this.obd = obd;
    }

    public Map<String, Object> getSms() {
        return sms;
    }

    public void setSms(Map<String, Object> sms) {
        this.sms = sms;
    }

    public Map<String, Object> getRcs() {
        return rcs;
    }

    public void setRcs(Map<String, Object> rcs) {
        this.rcs = rcs;
    }

    public Map<String, Object> getIvr() {
        return ivr;
    }

    public void setIvr(Map<String, Object> ivr) {
        this.ivr = ivr;
    }

    public Map<String, Object> getCcs() {
        return ccs;
    }

    public void setCcs(Map<String, Object> ccs) {
        this.ccs = ccs;
    }

    public Map<String, Object> getWaba() {
        return waba;
    }

    public void setWaba(Map<String, Object> waba) {
        this.waba = waba;
    }

    public Boolean getIsAccountActive() {
        return isAccountActive;
    }

    public void setIsAccountActive(Boolean isAccountActive) {
        this.isAccountActive = isAccountActive;
    }

    public String getValidityFrom() {
        return validityFrom;
    }

    public void setValidityFrom(String validityFrom) {
        this.validityFrom = validityFrom;
    }

    public String getValidityTo() {
        return validityTo;
    }

    public void setValidityTo(String validityTo) {
        this.validityTo = validityTo;
    }

    public String getTrafficStartsAt() {
        return trafficStartsAt;
    }

    public void setTrafficStartsAt(String trafficStartsAt) {
        this.trafficStartsAt = trafficStartsAt;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public Long getCreditLimit() {
        return creditLimit;
    }

    public void setCreditLimit(Long creditLimit) {
        this.creditLimit = creditLimit;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
