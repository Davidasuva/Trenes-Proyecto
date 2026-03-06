package edu.project.model;

import java.rmi.RemoteException;

public interface Ticket_Interface {
    Ticket register(String id) throws RemoteException;
}
