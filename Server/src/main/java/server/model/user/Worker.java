package server.model.user;

public class Worker extends AbstractHighPermissionUser {
    public Worker(String id, String mail, String name, String lastName, String password, String typeIdetification, String adress, int type) {
        super(id, mail, name, lastName, password, typeIdetification, adress, type);
    }
}
