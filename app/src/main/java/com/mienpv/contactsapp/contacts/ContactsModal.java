package com.mienpv.contactsapp.contacts;

public class ContactsModal {

    // variables for our user name
    // and contact number.
    private String userName;
    private String contactNumber;
    private String emailName;

    // constructor
    public ContactsModal(String userName, String contactNumber, String emailName) {
        this.userName = userName;
        this.contactNumber = contactNumber;
        this.emailName = emailName;

    }

    // on below line we have
    // created getter and setter
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getEmailName() {
        return emailName;
    }

    public void setEmailName(String contactNumber) {
        this.emailName = emailName;
    }

}
