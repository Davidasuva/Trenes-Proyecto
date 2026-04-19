package client.model;

import java.rmi.Naming;
import edu.uva.app.linkedlist.singly.singly.LinkedList;
import server.model.observer.Subject;
import server.model.route.Route;
import server.model.route.RouteInterface;
import server.model.ticket.Ticket;
import server.model.ticket.TicketInterface;
import server.model.user.AbstractUser;
import server.model.user.Passenger;
import server.model.user.User;

public class ClientModel extends Subject {
    private final String uri;
    private final String userUri;
    private final String routeUri;

    private TicketInterface ticketService;
    private User userService;
    private RouteInterface routeService;

    private String logger;
    private Passenger currentPassenger;

    public ClientModel(String ip, int port, String serviceName) {
        this.uri="//"+ip+":"+port+"/"+serviceName;
        this.userUri="//"+ip+":"+port+"/"+serviceName+"-users";
        this.routeUri="//"+ip+":"+port+"/"+serviceName+"-routes";
    }

    public boolean connect(){
        try{
            ticketService=(TicketInterface) Naming.lookup(uri);
            userService=(User) Naming.lookup(userUri);
            routeService=(RouteInterface) Naming.lookup(routeUri);
            log("Conectando al servidor:"+uri);
            return true;
        } catch (Exception e) {
            log("No se pudo conectar a: "+uri+" - "+e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean connect(String email, String password){
            try{
                AbstractUser user=userService.userPerEmailAndPassword(email,password,null);

                if(user==null){
                    log("Credenciales incorrectas.");
                    return false;
                }
                if(!(user instanceof Passenger)){
                    log("Esta aplicación es solo para pasajeros.");
                    return false;
                }

                currentPassenger=(Passenger) user;
                log("Bienvenido"+currentPassenger.getName()+"!");
                return true;
            }catch(Exception e){
                log("Error en login"+e.getMessage());
                e.printStackTrace();
                return false;
            }
    }

    public boolean registerPassenger(String id, String mail,String name, String lastName, String password, String typeIdetification, String adress){
        try{
            Passenger p=new Passenger(id,mail,name,lastName,password,typeIdetification,adress);
            AbstractUser registered =userService.register(p);
            currentPassenger=(Passenger) registered;
            log("Cuenta registrada con exito, Bienvenido: "+ currentPassenger.getName());
            return true;
        }catch(Exception e){
            log("Error al registrar: "+e.getMessage());
            e.printStackTrace();
            return false;

        }
    }

    public LinkedList<Route> getAvailableRoutes(){
        try{
            LinkedList<Route> routes= routeService.getAvailableRoutes();
            log("Se encontraron: "+routes.size()+ " rutas disponibles");
            return routes;
        }catch(Exception e){
            log("Error al obtener rutas: "+e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public Passenger getCurrentPassenger() {
        return currentPassenger;
    }

    public String getLogger() {
        return logger;
    }

    public boolean isLoggedIn(){
        return currentPassenger!=null;
    }

    public Ticket buyTicket(Route route, int category){
        if(currentPassenger==null){
            log("Debe iniciar sesión para poder comprar un ticket.");
            return null;
        }

        try{
            Ticket ticket=new Ticket(
                    "",
                    currentPassenger,
                    route,
                    route.getTrains().peek(),
                    route.getTrains().peek().getCarriages().peek() instanceof server.model.carriage.CarriageLoad
                            ? (server.model.carriage.CarriageLoad) route.getTrains().peek().getCarriages().peek()
                            : null,
                    null,
                    category,
                    true,
                    java.time.LocalDate.now().toString()
                    );
            Ticket registered=ticketService.register(ticket);
            log("Ticket comprado! ID: "+registered.getId()+" |Ruta: "+registered.getRoute().getName()+" |Categoría: "+ registered.getCategory());
            return registered;
        }catch(Exception e){
            log("Error al comprar un ticket: "+e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public void logout(){
        currentPassenger=null;
        log("Sesión cerrada. ");
    }

    private void log(String msg){
        this.logger=msg;
        this.notifyObservers();
    }
}