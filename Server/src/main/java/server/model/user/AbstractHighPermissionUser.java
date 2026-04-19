package server.model.user;

import server.model.route.Route;
import server.model.train.Train;

public abstract class AbstractHighPermissionUser extends AbstractUser {


    public AbstractHighPermissionUser(String id, String mail, String name, String lastName, String password, String typeIdetification, String adress, int type) {
        super(id, mail, name, lastName, password, typeIdetification, adress, type);
    }
}
