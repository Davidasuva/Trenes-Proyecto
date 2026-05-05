package server.model.route;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import edu.uva.app.bintree.avl.BinAVLTree;
import edu.uva.app.linkedlist.singly.singly.LinkedList;
import edu.uva.app.priorityQueue.PriorityQueue;
import edu.uva.app.queue.list.Queue;
import edu.uva.model.iterator.Iterator;
import server.model.carriage.AbstractCarriage;
import server.model.carriage.CarriagePassenger;
import server.model.ticket.Ticket;
import server.model.ticket.TicketService;
import server.model.train.Train;
import server.model.user.AbstractUser;
import java.time.LocalDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class RouteService extends UnicastRemoteObject implements RouteInterface {

    private BinAVLTree<Route> routes = new BinAVLTree<>();
    private RouteGraph routeGraph = new RouteGraph();
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private TicketService ticketService;

    public void setTicketService(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    public RouteService() throws RemoteException {
        super();
        scheduler.scheduleAtFixedRate(() -> {
            try { checkAndDeactivateExpiredRoutes(); } catch (Exception ignored) {}
        }, 30, 60, TimeUnit.SECONDS);
    }

    private void checkAndDeactivateExpiredRoutes() {
        LocalDateTime now = LocalDateTime.now();
        try {
            LinkedList<Route> all = routes.inorder();
            Iterator<Route> it = all.iterator();
            while (it.hasNext()) {
                Route r = it.next();
                if (r.isActive() && r.getDateArrival() != null && r.getDateArrival().isBefore(now)) {
                    r.setActive(false);
                    System.out.println("[Scheduler] Ruta desactivada automáticamente: " + r.getName()
                            + " (llegada: " + r.getDateArrivalStr() + ")");
                }
            }
        } catch (Exception e) {
            System.err.println("[Scheduler] Error revisando rutas: " + e.getMessage());
        }
    }

    public int getRouteStatus(Route route) {
        if (!route.isActive()) return 2;
        LocalDateTime now = LocalDateTime.now();
        if (route.getDateTravel() != null && now.isAfter(route.getDateTravel())) {
            if (route.getDateArrival() != null && now.isAfter(route.getDateArrival())) {
                return 2;
            }
            return 1;
        }
        return 0;
    }

    @Override
    public Route register(Route route) throws RemoteException {
        routes.insert(route);
        return route;
    }

    public String getRouteNameUsingTrain(Train train) throws RemoteException {
        LinkedList<Route> all = routes.inorder();
        Iterator<Route> it = all.iterator();
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
    public Route createRoute(int id, String name, Queue<Train> trains, LocalDateTime dateTravel, LocalDateTime dateArrival, Station origin, Station destiny, AbstractUser user) throws RemoteException {
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
        LinkedList<Route> all = routes.inorder();
        Iterator<Route> iterator = all.iterator();
        while (iterator.hasNext()) {
            Route route = iterator.next();
            if (route.getId() == id) return route;
        }
        return null;
    }

    @Override
    public boolean deactivateRoute(int id, AbstractUser user) throws RemoteException {
        if (user.getType() == 1) {
            throw new RemoteException("Sin permisos");
        }
        Route route = getRouteById(id);
        if (route == null) {
            return false;
        }
        route.setActive(false);
        return true;
    }

    @Override
    public boolean addTrainToRoute(int routeId, Train train, AbstractUser user) throws RemoteException {
        if (user.getType() == 1) {
            throw new RemoteException("Sin permisos");
        }
        Route route = getRouteById(routeId);
        if (route == null || !route.isActive()) return false;
        return route.addTrain(train);
    }

    @Override
    public Train removeTrainFromRoute(int routeId, AbstractUser user) throws RemoteException {
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
        return routes.inorder();
    }

    @Override
    public LinkedList<Route> getAvailableRoutes() throws RemoteException {
        LocalDateTime now = LocalDateTime.now();
        LinkedList<Route> availableRoutes = new LinkedList<>();
        LinkedList<Route> all = routes.inorder();
        Iterator<Route> iterator = all.iterator();
        while (iterator.hasNext()) {
            Route route = iterator.next();
            if (route.isActive()
                    && (route.getDateTravel() == null || now.isBefore(route.getDateTravel()))) {
                availableRoutes.add(route);
            }
        }
        return availableRoutes;
    }

    @Override
    public LinkedList<Route> getAllRoutesWithStatus() throws RemoteException {
        return routes.inorder();
    }

    @Override
    public LinkedList<Route> getRoutesPerDestiny(Station destiny) throws RemoteException {
        LinkedList<Route> destinyRoutes = new LinkedList<>();
        LinkedList<Route> all = routes.inorder();
        Iterator<Route> iterator = all.iterator();
        while (iterator.hasNext()) {
            Route route = iterator.next();
            if (route.getDestiny().equals(destiny)) {
                destinyRoutes.add(route);
            }
        }
        return destinyRoutes;
    }

    @Override
    public LinkedList<Route> getRoutesPerOrigin(Station origin) throws RemoteException {
        LinkedList<Route> originRoutes = new LinkedList<>();
        LinkedList<Route> all = routes.inorder();
        Iterator<Route> iterator = all.iterator();
        while (iterator.hasNext()) {
            Route route = iterator.next();
            if (route.getOrigin().equals(origin)) {
                originRoutes.add(route);
            }
        }
        return originRoutes;
    }

    @Override
    public LinkedList<Route> getRoutesPerDestinyAndOrigin(Station origin, Station destiny) throws RemoteException {
        LinkedList<Route> result = new LinkedList<>();
        LinkedList<Route> all = routes.inorder();
        Iterator<Route> iterator = all.iterator();
        while (iterator.hasNext()) {
            Route route = iterator.next();
            if (route.getOrigin().equals(origin) && route.getDestiny().equals(destiny)) {
                result.add(route);
            }
        }
        return result;
    }

    @Override
    public LinkedList<Route> getRoutesPerDistance(double min, double max) throws RemoteException {
        LinkedList<Route> distanceRoutes = new LinkedList<>();
        LinkedList<Route> all = routes.inorder();
        Iterator<Route> iterator = all.iterator();
        while (iterator.hasNext()) {
            Route route = iterator.next();
            if (route.getTotalDistance() <= max && route.getTotalDistance() >= min) {
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
        LocalDateTime now = LocalDateTime.now();
        if (route.getDateTravel() != null && route.getDateTravel().isBefore(now)) {
            throw new RemoteException(
                "No se puede activar: la hora de salida ya pasó. Edita la ruta y ajusta una hora futura.");
        }
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

        result.add("=== ORDEN DE ABORDAJE: " + route.getName() + " ===");
        result.add("(De atrás hacia adelante — Premium → Ejecutivo → Estándar)");
        result.add("");

        Queue<Train> cola = route.getTrains();
        if (cola == null || cola.isEmpty()) {
            result.add("No hay tren asignado a esta ruta.");
            return result;
        }
        Train tren = cola.peek();

        LinkedList<AbstractCarriage> carriages = tren.getCarriages();
        if (carriages == null || carriages.isEmpty()) {
            result.add("El tren no tiene vagones registrados.");
            return result;
        }

        LinkedList<CarriagePassenger> passengerCarriages = new LinkedList<>();
        Iterator<AbstractCarriage> cIt = carriages.iterator();
        while (cIt.hasNext()) {
            AbstractCarriage c = cIt.next();
            if (c instanceof CarriagePassenger) {
                passengerCarriages.add((CarriagePassenger) c);
            }
        }

        if (passengerCarriages.isEmpty()) {
            result.add("El tren no tiene vagones de pasajeros.");
            return result;
        }

        String[] categoriaNombres = { "Premium", "Ejecutivo", "Estándar" };
        int turno = 1;

        int totalVagones = 0;
        Iterator<CarriagePassenger> countIt = passengerCarriages.iterator();
        while (countIt.hasNext()) { countIt.next(); totalVagones++; }

        CarriagePassenger[] vagonesArr = new CarriagePassenger[totalVagones];
        Iterator<CarriagePassenger> fillIt = passengerCarriages.iterator();
        for (int i = 0; i < totalVagones && fillIt.hasNext(); i++) {
            vagonesArr[i] = fillIt.next();
        }

        for (int v = totalVagones - 1; v >= 0; v--) {
            CarriagePassenger vagon = vagonesArr[v];
            result.add("── Vagón " + vagon.getId() + " ──");

            PriorityQueue<Ticket> pq = vagon.getPassengers();

            if (pq == null || pq.isEmpty()) {
                result.add("  (Sin pasajeros registrados)");
                result.add("");
                continue;
            }

            LinkedList<Ticket> extraidos = new LinkedList<>();
            String catActual = null;

            while (!pq.isEmpty()) {
                Ticket t = pq.extract();
                if (t == null) break;
                extraidos.add(t);
                String cat = categoriaNombres[Math.min(t.getCategory(), 2)];
                if (!cat.equals(catActual)) {
                    catActual = cat;
                    result.add("  [" + cat + "]");
                }
                String nombre = t.getPassenger().getName() + " " + t.getPassenger().getLastName();
                result.add("    Turno " + turno + " → " + nombre);
                turno++;
            }

            Iterator<Ticket> reIt = extraidos.iterator();
            while (reIt.hasNext()) {
                Ticket t = reIt.next();
                pq.insert(t.getCategory(), t);
            }

            result.add("");
        }

        if (turno == 1) {
            result.add("No hay pasajeros registrados en esta ruta aún.");
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

    @Override
    public int getRouteStatus(int routeId) throws RemoteException {
        Route route = getRouteById(routeId);
        if (route == null) return 2;
        return getRouteStatus(route);
    }
}
