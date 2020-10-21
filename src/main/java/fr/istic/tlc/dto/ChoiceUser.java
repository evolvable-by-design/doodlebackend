package fr.istic.tlc.dto;

import java.util.List;

public class ChoiceUser {
    private List<Long> choices;
    private String mail;
    private String pref;
    private String username;

    public List<Long> getChoices() { return choices; }
    public void setChoices(List<Long> value) { this.choices = value; }

    public String getMail() { return mail; }
    public void setMail(String value) { this.mail = value; }

    public String getPref() { return pref; }
    public void setPref(String value) { this.pref = value; }

    public String getUsername() { return username; }
    public void setUsername(String value) { this.username = value; }
}
