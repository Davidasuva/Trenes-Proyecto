package server.model.user;

import server.model.route.Route;
import server.model.train.Train;

public abstract class AbstractHighPermissionUser {
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
