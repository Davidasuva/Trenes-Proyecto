package edu.project.model.user;

public abstract class AbstractHighPermissionUser extends AbstractUser{
    public AbstractHighPermissionUser(String idUser, String name, String lastName, String typeIdentification, String adress, String password) {
        super(idUser, name, lastName, typeIdentification, adress, password);
    }
}
