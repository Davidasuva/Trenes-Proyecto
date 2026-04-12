package server.model.user;

import server.model.route.Route;
import server.model.train.Train;

public abstract class AbstractHighPermissionUser extends AbstractUser {


    public AbstractHighPermissionUser(String id, String mail, String name, String lastName, String password, String typeIdetification, String adress, int type) {
        super(id, mail, name, lastName, password, typeIdetification, adress, type);
    }

    public boolean addTrain(Train train) {
        return false;
    }
    public boolean removeTrain(Train train) {
        return false;
    }
    public boolean modifyTrain(Train train) {
        return false;
    }

    public boolean addRoute(Route route) {
        return false;
    }
    public boolean removeRoute(Route route) {
        return false;
    }
    public boolean modifyRoute(Route route) {
        return false;
    }

}
