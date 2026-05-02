package server.model;

import server.model.carriage.CarriageService;
import server.model.history.History;
import server.model.luggage.LuggageService;
import server.model.route.RouteService;
import server.model.ticket.TicketService;
import server.model.train.TrainService;
import server.model.user.Admin;
import server.model.user.UserService;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class ServerModel {

    private final String  ip;
    private final int     port;
    private final String  serviceName;
    private final String  ticketUri;
    private final String  userUri;
    private final String  trainUri;
    private final String routeUri;
    private final String luggageUri;
    private final String carriageUri;

    private TicketService ticketService;
    private TrainService trainService;
    private RouteService routeService;
    private LuggageService luggageService;
    private CarriageService carriageService;
    private UserService userService;
    private Registry      registry;
    private final History history;

    public ServerModel(String ip, int port, String serviceName) {
        this.ip          = ip;
        this.port        = port;
        this.serviceName = serviceName;
        this.ticketUri   = "//" + ip + ":" + port + "/" + serviceName;
        this.userUri     = "//" + ip + ":" + port + "/" + serviceName + "-users";
        this.trainUri     = "//" + ip + ":" + port + "/" + serviceName + "-trains";
        this.routeUri     = "//" + ip + ":" + port + "/" + serviceName + "-routes";
        this.luggageUri = "//" + ip + ":" + port + "/" + serviceName + "-luggages";
        this.carriageUri   = "//" + ip + ":" + port + "/" + serviceName + "-carriages";
        this.history     = new History();

        history.addAction("ServerModel listo — URI base: " + ticketUri);
    }

    public boolean deploy() {
        try {
            history.addAction("Iniciando despliegue en " + ip + ":" + port + "...");
            System.setProperty("java.rmi.server.hostname", ip);

            ticketService = new TicketService();
            userService   = new UserService();
            trainService  = new TrainService();
            routeService  = new RouteService();
            luggageService = new LuggageService();
            carriageService  = new CarriageService();
            routeService.setTicketService(ticketService);
            trainService.setCarriageService(carriageService);
            userService.register(new Admin(
                    "0", "Admin@project.com", "Admin123",
                    "Admin", "1234", "C.C", "cr 29#92-49", 3
            ));
            registry = LocateRegistry.createRegistry(port);
            Naming.rebind(trainUri,trainService);
            Naming.rebind(routeUri,routeService);
            Naming.rebind(luggageUri,luggageService);
            Naming.rebind(carriageUri,carriageService);
            Naming.rebind(ticketUri, ticketService);
            Naming.rebind(userUri,   userService);

            history.addAction("TicketService activo en: " + ticketUri);
            history.addAction("UserService   activo en: " + userUri);
            history.addAction("TrainService   activo en: " + trainUri);
            history.addAction("RouteService   activo en: " + routeUri);
            history.addAction("LuggageService   activo en: " + luggageUri);
            history.addAction("CarriageService activo en: "+carriageUri);
            return true;
        } catch (Exception e) {
            history.addAction("Error al desplegar: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public void stop() {
        tryUnbind(ticketUri);
        tryUnbind(userUri);
        tryUnbind(trainUri);
        tryUnbind(routeUri);
        tryUnbind(luggageUri);
        tryUnbind(carriageUri);
        tryUnexport(ticketService, "TicketService");
        tryUnexport(userService,   "UserService");
        tryUnexport(trainService,"TrainService");
        tryUnexport(routeService, "RouteService");
        tryUnexport(luggageService, "LuggageService");
        tryUnexport(carriageService, "CarriageService");
        tryUnexport(registry,      "Registry (puerto " + port + ")");
    }

    private void tryUnbind(String uri) {
        try{
            Naming.unbind(uri);
            history.addAction("Binding eliminado: "+uri);
        } catch (Exception e) {
            history.addAction("Aviso unbind ("+uri+"): "+e.getMessage());
        }
    }

    private void tryUnexport(java.rmi.Remote obj, String name){
        if(obj == null){return;}
        try{
            UnicastRemoteObject.unexportObject(obj, true);
            history.addAction(name+" desexportado");
        }catch(Exception e){
            history.addAction("Aviso unexport "+name+": "+e.getMessage());
        }
    }

    public History getHistory() {
        return history;
    }

    public TicketService getTicketService() {
        return ticketService;
    }
    public UserService getUserService() {
        return userService;
    }

    public TrainService getTrainService() {
        return trainService;
    }

    public RouteService getRouteService() {
        return routeService;
    }

    public LuggageService getLuggageService() {
        return luggageService;
    }

    public CarriageService getCarriageService() {
        return carriageService;
    }
}