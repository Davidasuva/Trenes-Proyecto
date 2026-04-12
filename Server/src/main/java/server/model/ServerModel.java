package server.model;

import server.model.history.History;
import server.model.ticket.TicketService;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class ServerModel {

    private final String  ip;
    private final int     port;
    private final String  serviceName;
    private final String  uri;

    private TicketService service;
    private Registry      registry;       // guardamos el registry para cerrarlo
    private final History history;

    public ServerModel(String ip, int port, String serviceName) {
        this.ip          = ip;
        this.port        = port;
        this.serviceName = serviceName;
        this.uri         = "//" + ip + ":" + port + "/" + serviceName;
        this.history     = new History();

        history.addAction("ServerModel listo — URI: " + this.uri);
    }

    public boolean deploy() {
        try {
            history.addAction("Iniciando despliegue en " + ip + ":" + port + "...");
            System.setProperty("java.rmi.server.hostname", ip);

            service  = new TicketService();
            registry = LocateRegistry.createRegistry(port);   // guardamos la referencia
            Naming.rebind(uri, service);

            history.addAction("Servicio RMI activo en " + uri);
            return true;
        } catch (Exception e) {
            history.addAction("Error al desplegar: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public void stop() {
        // 1. Desregistrar el nombre del binding
        try {
            Naming.unbind(uri);
            history.addAction("Binding '" + serviceName + "' eliminado.");
        } catch (Exception e) {
            history.addAction("Aviso unbind: " + e.getMessage());
        }

        // 2. Desexportar el servicio (cierra los sockets del objeto remoto)
        try {
            if (service != null) {
                UnicastRemoteObject.unexportObject(service, true);
                history.addAction("TicketService desexportado.");
            }
        } catch (Exception e) {
            history.addAction("Aviso unexport servicio: " + e.getMessage());
        }

        // 3. Cerrar el registry (libera el puerto)
        try {
            if (registry != null) {
                UnicastRemoteObject.unexportObject(registry, true);
                history.addAction("Registry cerrado — puerto " + port + " liberado.");
            }
        } catch (Exception e) {
            history.addAction("Aviso unexport registry: " + e.getMessage());
        }
    }

    public History getHistory() {
        return history;
    }
}