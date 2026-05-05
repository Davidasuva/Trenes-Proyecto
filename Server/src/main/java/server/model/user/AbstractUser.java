package server.model.user;

import java.io.Serializable;
import edu.uva.app.linkedlist.singly.singly.LinkedList;

/**
 * Clase base abstracta que representa a cualquier usuario del sistema de trenes.
 * <p>
 * Define los atributos y comportamientos comunes a todos los tipos de usuario:
 * pasajeros, trabajadores y administradores. Implementa {@link Serializable}
 * para permitir la transferencia de objetos a través de RMI.
 * </p>
 *
 * <ul>
 *   <li>Tipo 1 → Pasajero ({@link Passenger})</li>
 *   <li>Tipo 2 → Trabajador / Personal ({@link Worker})</li>
 *   <li>Tipo 3 → Administrador ({@link Admin})</li>
 * </ul>
 *
 * @author Equipo ValidarTicket
 * @version 1.0
 * @see Passenger
 * @see Admin
 * @see Worker
 */
public abstract class AbstractUser implements Serializable, Comparable<AbstractUser> {
    private static final long serialVersionUID = 2L;

    private String id;
    private String name;
    private String mail;
    private String lastName;
    private String password;
    private String typeIdetification;
    private String adress;
    /** 1=Pasajero, 2=Personal, 3=Administrador */
    private int type;
    private LinkedList<String> phoneNumbers;

    /**
     * Construye un usuario con todos sus atributos básicos.
     *
     * @param id                 identificador único del usuario (número de documento)
     * @param mail               correo electrónico del usuario
     * @param name               nombre de pila
     * @param lastName           apellido
     * @param password           contraseña en texto plano
     * @param typeIdetification  tipo de documento (ej. "C.C", "Pasaporte")
     * @param adress             dirección de residencia
     * @param type               rol del usuario (1=Pasajero, 2=Personal, 3=Admin)
     */
    public AbstractUser(String id, String mail, String name, String lastName,
                        String password, String typeIdetification, String adress, int type) {
        this.id = id;
        this.mail = mail;
        this.name = name;
        this.lastName = lastName;
        this.password = password;
        this.typeIdetification = typeIdetification;
        this.adress = adress;
        this.type = type;
        phoneNumbers = new LinkedList<>();
    }

    /**
     * Retorna el identificador único del usuario.
     *
     * @return número de documento del usuario
     */
    public String getId() { return id; }

    /**
     * Retorna el nombre de pila del usuario.
     *
     * @return nombre del usuario
     */
    public String getName() { return name; }

    /**
     * Actualiza el nombre del usuario.
     *
     * @param name nuevo nombre de pila
     */
    public void setName(String name) { this.name = name; }

    /**
     * Retorna el apellido del usuario.
     *
     * @return apellido del usuario
     */
    public String getLastName() { return lastName; }

    /**
     * Actualiza el apellido del usuario.
     *
     * @param lastName nuevo apellido
     */
    public void setLastName(String lastName) { this.lastName = lastName; }

    /**
     * Retorna la contraseña del usuario.
     *
     * @return contraseña en texto plano
     */
    public String getPassword() { return password; }

    /**
     * Actualiza la contraseña del usuario.
     *
     * @param password nueva contraseña en texto plano
     */
    public void setPassword(String password) { this.password = password; }

    /**
     * Retorna el tipo de identificación del usuario.
     *
     * @return tipo de documento (ej. "C.C", "Pasaporte")
     */
    public String getTypeIdetification() { return typeIdetification; }

    /**
     * Actualiza el tipo de identificación.
     *
     * @param typeIdetification nuevo tipo de documento
     */
    public void setTypeIdetification(String typeIdetification) {
        this.typeIdetification = typeIdetification;
    }

    /**
     * Retorna la dirección de residencia del usuario.
     *
     * @return dirección del usuario
     */
    public String getAdress() { return adress; }

    /**
     * Actualiza la dirección de residencia.
     *
     * @param adress nueva dirección
     */
    public void setAdress(String adress) { this.adress = adress; }

    /**
     * Agrega un número de teléfono a la lista del usuario.
     *
     * @param phoneNumber número de teléfono a agregar
     * @return {@code true} si se agregó exitosamente, {@code false} en caso contrario
     */
    public boolean addPhoneNumber(String phoneNumber) {
        return phoneNumbers.add(phoneNumber);
    }

    /**
     * Elimina un número de teléfono de la lista del usuario.
     *
     * @param phoneNumber número de teléfono a eliminar
     * @return {@code true} si se eliminó exitosamente, {@code false} si no estaba registrado
     */
    public boolean removePhoneNumber(String phoneNumber) {
        return phoneNumbers.remove(phoneNumber);
    }

    /**
     * Retorna la lista de números de teléfono registrados.
     *
     * @return lista enlazada de teléfonos
     */
    public LinkedList<String> getPhoneNumbers() { return phoneNumbers; }

    /**
     * Retorna el tipo/rol del usuario en el sistema.
     *
     * @return 1 para Pasajero, 2 para Personal, 3 para Administrador
     */
    public int getType() { return type; }

    /**
     * Retorna el correo electrónico del usuario.
     *
     * @return correo electrónico
     */
    public String getMail() { return mail; }

    /**
     * Actualiza el correo electrónico del usuario.
     *
     * @param mail nuevo correo electrónico
     */
    public void setMail(String mail) { this.mail = mail; }

    /**
     * Compara dos usuarios por su identificador único.
     *
     * @param o objeto a comparar
     * @return {@code true} si los usuarios tienen el mismo ID
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AbstractUser)) return false;
        AbstractUser u = (AbstractUser) o;
        return this.id.equals(u.getId());
    }

    @Override
    public int compareTo(AbstractUser u) {
        return this.id.compareTo(u.getId());
    }
}
