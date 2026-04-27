package server.model.train;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import edu.uva.app.linkedlist.singly.singly.LinkedList;
import edu.uva.model.iterator.Iterator;
import server.model.carriage.AbstractCarriage;
import server.model.carriage.CarriagePassenger;
import server.model.carriage.CarriageLoad;
import server.model.user.AbstractUser;

public class TrainService extends UnicastRemoteObject implements TrainInterface {
    private LinkedList<Train> trains=new LinkedList<>();
    // Contador global para IDs de vagones (auto-incremental)
    private int carriageIdCounter = 1;

    public TrainService() throws RemoteException {
        super();
    }

    // ── Límites de vagones por fabricante ─────────────────────────────────────
    private static final int MAX_VAGONES_MERCEDES = 28;
    private static final int MAX_VAGONES_ARNOLD   = 32;

    /**
     * Valida que el total de vagones (pasajeros + carga) no supere el límite
     * del fabricante y que sea al menos 1.
     * Lanza RemoteException con mensaje descriptivo si no se cumple.
     */
    private void validarVagones(String tipo, int vagonesPasajeros, int vagonesCarga) throws RemoteException {
        int total = vagonesPasajeros + vagonesCarga;
        if (total < 1) {
            throw new RemoteException("El tren debe tener al menos 1 vagón.");
        }
        boolean esMercedes = tipo != null && tipo.toLowerCase().contains("mercedes");
        boolean esArnold   = tipo != null && tipo.toLowerCase().contains("arnold");
        if (esMercedes && total > MAX_VAGONES_MERCEDES) {
            throw new RemoteException("Un tren Mercedes-Benz no puede superar los "
                    + MAX_VAGONES_MERCEDES + " vagones (actual: " + total + ").");
        }
        if (esArnold && total > MAX_VAGONES_ARNOLD) {
            throw new RemoteException("Un tren Arnold no puede superar los "
                    + MAX_VAGONES_ARNOLD + " vagones (actual: " + total + ").");
        }
        // Advertencia sobre la regla 1 carga por cada 2 pasajeros
        if (vagonesPasajeros > 0 && vagonesCarga < Math.ceil(vagonesPasajeros / 2.0)) {
            throw new RemoteException("Se requiere al menos 1 vagón de carga por cada 2 de pasajeros "
                    + "(pasajeros: " + vagonesPasajeros + ", carga mínima requerida: "
                    + (int) Math.ceil(vagonesPasajeros / 2.0) + ").");
        }
    }

    @Override
    public Train register(Train train, AbstractUser user) throws RemoteException {
        if(user.getType()==1){
            throw new RemoteException("Sin permisos");
        }
        if (getTrainById(train.getId()) != null) {
            throw new RemoteException("Ya existe un tren con el id: " + train.getId());
        }
        validarVagones(train.getType(), train.getCapacity(), train.getCargoWagons());

        // Crear automáticamente los vagones de pasajeros (capacidad 40 por vagón)
        for (int i = 0; i < train.getCapacity(); i++) {
            CarriagePassenger cp = new CarriagePassenger(carriageIdCounter++, 40);
            train.addCarriage(cp);
        }
        // Crear automáticamente los vagones de carga (capacidad máx = 2 maletas * 80kg = 160 kg)
        for (int i = 0; i < train.getCargoWagons(); i++) {
            CarriageLoad cl = new CarriageLoad(carriageIdCounter++, 160);
            train.addCarriage(cl);
        }

        trains.add(train);
        return train;
    }

    @Override
    public Train removeTrain(Train train, AbstractUser user) throws RemoteException {
        if(user.getType()==1){
            throw new RemoteException("Sin permisos");
        }
        if(train==null){
            return null;
        }
        trains.remove(train);
        return train;
    }
    @Override
    public Train getTrainById(int id) throws RemoteException {
        Iterator<Train> iterator = trains.iterator();
        while (iterator.hasNext()) {
            Train train = iterator.next();
            if (train.getId() == id) return train;
        }
        return null;
    }
    @Override
    public LinkedList<AbstractCarriage> seeCarriagesPerTrain(int trainId) throws RemoteException {
        Train train = getTrainById(trainId);
        if (train == null) {
            return null;
        }
        return train.getCarriages();
    }
    @Override
    public boolean addCarriageToTrain(int trainId, AbstractCarriage carriage) throws RemoteException {
        Train train = getTrainById(trainId);
        if (train == null) {
            return false;
        }
        return train.addCarriage(carriage);
    }
    @Override
    public boolean removeCarriageFromTrain(int trainId, AbstractCarriage carriage) throws RemoteException {
        Train train = getTrainById(trainId);
        if (train == null) {
            return false;
        }
        return train.removeCarriage(carriage);
    }
    @Override
    public boolean updateMileage(int trainId, int mileage) throws RemoteException {
        Train train = getTrainById(trainId);
        if (train == null){
            return false;
        }
        train.updateMileage(mileage);
        return true;
    }
    @Override
    public LinkedList<Train> getTrainsByType(String type) throws RemoteException {
        LinkedList<Train> result = new LinkedList<>();
        Iterator<Train> iterator = trains.iterator();
        while (iterator.hasNext()) {
            Train train = iterator.next();
            if (train.getType().equalsIgnoreCase(type)) {
                result.add(train);
            }
        }
        return result;
    }
    @Override
    public LinkedList<Train> getTrainsByCapacity(int minCapacity) throws RemoteException {
        LinkedList<Train> result = new LinkedList<>();
        Iterator<Train> iterator = trains.iterator();
        while (iterator.hasNext()) {
            Train train = iterator.next();
            if (train.getCapacity() >= minCapacity) {
                result.add(train);
            }
        }
        return result;
    }
    @Override
    public LinkedList<Train> getTrains() throws RemoteException {
        return trains;
    }
    @Override
    public LinkedList<Train> getTrainsByMileage(int maxMileage) throws RemoteException {
        LinkedList<Train> result = new LinkedList<>();
        Iterator<Train> iterator = trains.iterator();
        while (iterator.hasNext()) {
            Train train = iterator.next();
            if (train.getMileage() <= maxMileage) {
                result.add(train);
            }
        }
        return result;
    }

    @Override
    public boolean modifyTrain(int id, String name, String type, int capacity, int cargoWagons, AbstractUser user) throws RemoteException {
        if(user.getType()==1){
            throw new RemoteException("Sin permisos");
        }
        Train train = getTrainById(id);
        if(train==null){
            return false;
        }
        validarVagones(type, capacity, cargoWagons);
        train.setName(name);
        train.setType(type);
        train.setCapacity(capacity);
        train.setCargoWagons(cargoWagons);
        return true;
    }
}