package edu.project.model.user;

public interface User {

    Boolean login(String username, String password);
    Boolean signUp(String name, String lastName, String mail, String typeIdentification, String id, String adress);
}
