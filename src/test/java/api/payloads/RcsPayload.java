package api.payloads;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class RcsPayload {

    private String user;
    private String password;
    private String senderid;
    private String channel;
    private String DCS;
    private String flashsms;
    private String number;
    private String text;
    private String route;

    // OBD specific
    private String campaign_name;
    private String voice_file; // path or id

    // WABA specific
    private String template_name;
    private String language;
    private Map<String, String> components;

    // Constructors
    public RcsPayload() {
    }

    // Builder methods or Setters can be used

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSenderid() {
        return senderid;
    }

    public void setSenderid(String senderid) {
        this.senderid = senderid;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getDCS() {
        return DCS;
    }

    public void setDCS(String dCS) {
        DCS = dCS;
    }

    public String getFlashsms() {
        return flashsms;
    }

    public void setFlashsms(String flashsms) {
        this.flashsms = flashsms;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    // ... other getters and setters
}
