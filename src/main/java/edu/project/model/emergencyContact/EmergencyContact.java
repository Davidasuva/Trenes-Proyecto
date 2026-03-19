package edu.project.model.emergencyContact;

import edu.uva.app.linkedlist.singly.singly.LinkedList;

public class EmergencyContact {
    String idEmergencyContact;
    String name;
    String lastName;
    String typeIdentification;
    LinkedList<String> phoneNumbers;

    public EmergencyContact(String idEmergencyContact, String name, String lastName, String typeIdentification) {
        this.idEmergencyContact = idEmergencyContact;
        this.name = name;
        this.lastName = lastName;
        this.typeIdentification = typeIdentification;
        phoneNumbers = new LinkedList<>();
    }
    public boolean addNumber(String phoneNumber){
        return phoneNumbers.add(phoneNumber);
    }

    public boolean removeNumber(String phoneNumber){
        return phoneNumbers.remove(phoneNumber);
    }

    public String getIdEmergencyContact() {
        return idEmergencyContact;
    }

    public void setIdEmergencyContact(String idEmergencyContact) {
        this.idEmergencyContact = idEmergencyContact;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getTypeIdentification() {
        return typeIdentification;
    }

    public void setTypeIdentification(String typeIdentification) {
        this.typeIdentification = typeIdentification;
    }

    public LinkedList<String> getPhoneNumbers() {
        return phoneNumbers;
    }

    public void setPhoneNumbers(LinkedList<String> phoneNumbers) {
        this.phoneNumbers = phoneNumbers;
    }
}
