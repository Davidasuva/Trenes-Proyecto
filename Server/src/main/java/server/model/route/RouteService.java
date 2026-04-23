package server.model.route;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import edu.uva.app.linkedlist.singly.singly.LinkedList;
import edu.uva.app.queue.list.Queue;
import edu.uva.model.iterator.Iterator;
import server.model.train.Train;
import server.model.user.AbstractUser;

public class RouteService extends UnicastRemoteObject implements RouteInterface{

    private LinkedList<Route> routes=new LinkedList<>();
    private RouteGraph routeGraph=new RouteGraph();
    public RouteService() throws RemoteException {
        super();
    }

    @Override
    public Route register(Route route) throws RemoteException {
        routes.add(route);
        return route;
    }

    /**
     * Verifica si un tren ya está asignado a alguna ruta activa.
     * Retorna el nombre de la ruta que lo usa, o null si está libre.
     */
    public String getRouteNameUsingTrain(Train train) throws RemoteException {
        Iterator<Route> it = routes.iterator();
        while (it.hasNext()) {
            Route route = it.next();
            if (!route.isActive()) continue;
            Queue<Train> cola = route.getTrains();
            if (cola == null || cola.isEmpty()) continue;
            if (cola.peek().equals(train)) {
                return route.getName();
            }
        }
        return null;
    }

    @Override
    public Route createRoute(int id, String name, Queue<Train> trains, String dateTravel, String dateArrival, Station origin, Station destiny,AbstractUser user) throws RemoteException {
        if (user.getType() == 1) {
            throw new RemoteException("Sin permisos");
        }
        if (getRouteById(id) != null) {
            throw new RemoteException("Ya existe una ruta con el id: " + id);
        }
        if (routeGraph.getShortestDistance(origin, destiny) == -1) {
            throw new RemoteException("No existe camino entre " + origin.getName()
                    + " y " + destiny.getName());
        }
        Route route = new Route(id, name, trains, dateTravel, dateArrival,
                origin, destiny, routeGraph);
        return register(route);
    }

    @Override
    public Route getRouteById(int id) throws RemoteException {
        Iterator<Route> iterator = routes.iterator();
        while (iterator.hasNext()) {
            Route route = iterator.next();
            if (route.getId() == id) return route;
        }
        return null;
    }
    @Override
    public boolean deactivateRoute(int id,AbstractUser user) throws RemoteException {
        if (user.getType() == 1) {
            throw new RemoteException("Sin permisos");
        }
        Route route = getRouteById(id);
        if (route == null){
            return false;
        }
        route.setActive(false);
        return true;
    }
    @Override
    public boolean addTrainToRoute(int routeId, Train train,AbstractUser user) throws RemoteException {
        if (user.getType() == 1) {
            throw new RemoteException("Sin permisos");
        }
        Route route = getRouteById(routeId);
        if (route == null || !route.isActive()) return false;
        return route.addTrain(train);
    }

    @Override
    public Train removeTrainFromRoute(int routeId,AbstractUser user) throws RemoteException {
        if (user.getType() == 1) {
            throw new RemoteException("Sin permisos");
        }
        Route route = getRouteById(routeId);
        if (route == null) {
            return null;
        }
        return route.removeTrain();
    }

    @Override
    public LinkedList<Station> getShortestPath(Station origin, Station destiny) throws RemoteException {
        return routeGraph.getShortestPath(origin, destiny);
    }
    @Override
    public LinkedList<Station> getStations() throws RemoteException {
        return routeGraph.getStations();
    }

    @Override
    public Queue<Train> seeTrainsPerRoute(int routeId) throws RemoteException {
        Route route = getRouteById(routeId);
        if (route == null) {
            return null;
        }
        return route.getTrains();
    }

    @Override
    public LinkedList<Route> getRoutes() throws RemoteException {
        return routes;
    }

    @Override
    public LinkedList<Route> getAvailableRoutes() throws RemoteException {
        LinkedList<Route> availableRoutes=new LinkedList<>();
        Iterator<Route> iterator=routes.iterator();
        while(iterator.hasNext()){
            Route route=iterator.next();
            if(route.isActive()){
                availableRoutes.add(route);
            }
        }
        return availableRoutes;
    }

    @Override
    public LinkedList<Route> getRoutesPerDestiny(Station destiny) throws RemoteException {
        LinkedList<Route> destinyRoutes=new LinkedList<>();
        Iterator<Route> iterator=routes.iterator();
        while(iterator.hasNext()){
            Route route=iterator.next();
            if(route.getDestiny().equals(destiny)){
                destinyRoutes.add(route);
            }
        }
        return destinyRoutes;
    }

    @Override
    public LinkedList<Route> getRoutesPerOrigin(Station origin) throws RemoteException {
        LinkedList<Route> originRoutes=new LinkedList<>();
        Iterator<Route> iterator=routes.iterator();
        while(iterator.hasNext()){
            Route route=iterator.next();
            if(route.getOrigin().equals(origin)){
                originRoutes.add(route);
            }
        }
        return originRoutes;
    }

    @Override
    public LinkedList<Route> getRoutesPerDestinyAndOrigin(Station origin, Station destiny) throws RemoteException {
        LinkedList<Route> originAndDestinyRoutes=new LinkedList<>();
        Iterator<Route> iterator=routes.iterator();
        while(iterator.hasNext()){
            Route route=iterator.next();
            if(route.getOrigin().equals(origin)&&route.getDestiny().equals(destiny)){
                originAndDestinyRoutes.add(route);
            }
        }
        return originAndDestinyRoutes;
    }

    @Override
    public LinkedList<Route> getRoutesPerDistance(double min, double max) throws RemoteException {
        LinkedList<Route> distanceRoutes=new LinkedList<>();
        Iterator<Route> iterator=routes.iterator();
        while(iterator.hasNext()){
            Route route=iterator.next();
            if(route.getTotalDistance()<=max&&route.getTotalDistance()>=min){
                distanceRoutes.add(route);
            }
        }
        return distanceRoutes;
    }

    @Override
    public boolean activateRoute(int id, AbstractUser user) throws RemoteException {
        if (user.getType() == 1) {
            throw new RemoteException("Sin permisos");
        }
        Route route = getRouteById(id);
        if (route == null) return false;
        route.setActive(true);
        return true;
    }
    @Override
    public boolean existsPath(Station origin, Station destiny) throws RemoteException {
        return routeGraph.getShortestDistance(origin, destiny) != -1;
    }
    public LinkedList<String> getBoardingOrder(int routeId) throws RemoteException {
        Route route = getRouteById(routeId);
        LinkedList<String> result = new LinkedList<>();
        if (route == null) return result;

        // Obtener pasajeros ordenados por categoría desde los vagones de pasajeros
        // Los vagones ya usan PriorityQueue por categoría. Generamos el orden teórico:
        // vagón N → vagón N-1 → ... → vagón 1, dentro de cada vagón: premium, ejecutivo, estándar

        result.add("=== ORDEN DE ABORDAJE: " + route.getName() + " ===");
        result.add("(De atrás hacia adelante — Premium → Ejecutivo → Estándar)");
        result.add("");

        // Categorías por nombre
        String[] categorias = { "Premium (4 lugares)", "Ejecutivo (8 lugares)", "Estándar (22 lugares)" };
        int[] capacidades   = { 4, 8, 22 };

        // Obtenemos la cantidad de vagones de pasajeros del tren asignado
        int vagonesPasajeros = 0;
        try {
            Queue<Train> cola = route.getTrains();
            if (!cola.isEmpty()) {
                Train tren = cola.peek();
                vagonesPasajeros = tren.getCapacity(); // vagones de pasajeros
            }
        } catch (Exception e) {
            vagonesPasajeros = 1;
        }

        if (vagonesPasajeros == 0){
            vagonesPasajeros = 1;
        }

        int turno = 1;
        // De atrás hacia adelante: vagón más alto al más bajo
        for (int v = vagonesPasajeros; v >= 1; v--) {
            result.add("── Vagón " + v + " ──");
            for (int cat = 0; cat < categorias.length; cat++) {
                result.add("  " + categorias[cat] + ":");
                for (int lugar = capacidades[cat]; lugar >= 1; lugar--) {
                    result.add("    Turno " + turno + " → Vagón " + v
                            + ", " + categorias[cat].split(" ")[0] + ", lugar " + lugar);
                    turno++;
                }
            }
            result.add("");
        }
        return result;
    }



    @Override
    public boolean publicateRoute(int id, AbstractUser user) throws RemoteException {
        if (user.getType() != 3) {
            throw new RemoteException("Solo Administradores");
        }
        Route route = getRouteById(id);
        if (route == null) {
            return false;
        }
        route.setActive(true);
        return true;

    }
}
