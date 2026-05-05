import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import server.model.carriage.CarriageLoad;
import server.model.carriage.CarriagePassenger;
import server.model.luggage.Luggage;
import server.model.ticket.Ticket;
import server.model.user.Passenger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests para Ticket.
 * Route y Train requieren RemoteException e infraestructura RMI compleja,
 * así que se pasan como null donde no afectan la lógica probada.
 */
@DisplayName("Ticket - Tiquete de viaje")
class TicketTest {

    private Passenger passenger;
    private CarriageLoad carriageLoad;
    private CarriagePassenger carriagePassenger;
    private Ticket ticket;

    @BeforeEach
    void setUp() {
        passenger = new Passenger("P001", "p@mail.com", "Juan", "Díaz",
                "pwd", "C.C", "Calle 1");
        carriageLoad = new CarriageLoad(10, 5440);
        carriagePassenger = new CarriagePassenger(20, 34);

        ticket = new Ticket("TKT-001", passenger, null, null,
                carriageLoad, carriagePassenger, 0, true, "01/05/2025 08:00");
    }

    @Test
    @DisplayName("Constructor asigna todos los campos básicos")
    void testConstructorFields() {
        assertEquals("TKT-001", ticket.getId());
        assertEquals(passenger, ticket.getPassenger());
        assertEquals(carriagePassenger, ticket.getCarriagePassenger());
        assertEquals(carriageLoad, ticket.getCarriageLoad());
        assertEquals(0, ticket.getCategory());
        assertTrue(ticket.Status());
        assertEquals("01/05/2025 08:00", ticket.getDateBuy());
    }

    @Test
    @DisplayName("Constructor agrega automáticamente el ticket al historial del pasajero")
    void testTicketAutoAddedToPassenger() {
        // El historial del pasajero ya tiene el ticket (agregado en el constructor)
        assertFalse(passenger.historyTickets.isEmpty());
    }

    @Test
    @DisplayName("verificateLuggage retorna true para maleta <= 80 kg")
    void testVerificateLuggageValid() {
        Luggage luggage = new Luggage(1, 80);
        assertTrue(ticket.verificateLuggage(luggage));
    }

    @Test
    @DisplayName("verificateLuggage retorna false para maleta > 80 kg")
    void testVerificateLuggageOverweight() {
        Luggage luggage = new Luggage(1, 81);
        assertFalse(ticket.verificateLuggage(luggage));
    }

    @Test
    @DisplayName("addLuggage agrega maleta válida al ticket")
    void testAddLuggageSuccess() {
        Luggage luggage = new Luggage(1, 50);
        assertTrue(ticket.addLuggage(luggage));
        assertEquals(1, ticket.getLuggage().size());
    }

    @Test
    @DisplayName("addLuggage rechaza maleta > 80 kg")
    void testAddLuggageOverweight() {
        Luggage heavy = new Luggage(1, 85);
        assertFalse(ticket.addLuggage(heavy));
        assertEquals(0, ticket.getLuggage().size());
    }

    @Test
    @DisplayName("addLuggage permite máximo 2 maletas")
    void testAddLuggageMaxTwo() {
        ticket.addLuggage(new Luggage(1, 30));
        ticket.addLuggage(new Luggage(2, 30));
        Luggage third = new Luggage(3, 20);
        assertFalse(ticket.addLuggage(third));
        assertEquals(2, ticket.getLuggage().size());
    }

    @Test
    @DisplayName("setCategory actualiza la categoría")
    void testSetCategory() {
        ticket.setCategory(2);
        assertEquals(2, ticket.getCategory());
    }

    @Test
    @DisplayName("setPrice asigna el precio")
    void testSetPrice() {
        ticket.setPrice(150000);
        assertEquals(150000, ticket.getPrice());
    }

    @Test
    @DisplayName("setContactName asigna el nombre de contacto")
    void testSetContactName() {
        ticket.setContactName("Contacto");
        assertEquals("Contacto", ticket.getContactName());
    }

    @Test
    @DisplayName("setContactLastName asigna el apellido de contacto")
    void testSetContactLastName() {
        ticket.setContactLastName("Apellido");
        assertEquals("Apellido", ticket.getContactLastName());
    }

    @Test
    @DisplayName("setContactPhone asigna el teléfono de contacto")
    void testSetContactPhone() {
        ticket.setContactPhone("3001234567");
        assertEquals("3001234567", ticket.getContactPhone());
    }

    @Test
    @DisplayName("setCarriageLoad cambia el vagón de carga asignado")
    void testSetCarriageLoad() {
        CarriageLoad newLoad = new CarriageLoad(99, 5000);
        ticket.setCarriageLoad(newLoad);
        assertEquals(newLoad, ticket.getCarriageLoad());
    }

    @Test
    @DisplayName("setCarriagePassenger cambia el vagón de pasajeros asignado")
    void testSetCarriagePassenger() {
        CarriagePassenger newPassenger = new CarriagePassenger(88, 34);
        ticket.setCarriagePassenger(newPassenger);
        assertEquals(newPassenger, ticket.getCarriagePassenger());
    }

    @Test
    @DisplayName("compareTo retorna 0 para mismo id")
    void testCompareToEqual() {
        Ticket other = new Ticket("TKT-001", passenger, null, null,
                carriageLoad, carriagePassenger, 1, false, "02/05/2025 10:00");
        assertEquals(0, ticket.compareTo(other));
    }

    @Test
    @DisplayName("compareTo retorna negativo para id lexicográficamente menor")
    void testCompareToLess() {
        Ticket other = new Ticket("TKT-999", passenger, null, null,
                carriageLoad, carriagePassenger, 1, false, "01/01/2025");
        assertTrue(ticket.compareTo(other) < 0);
    }

    @Test
    @DisplayName("compareTo retorna positivo para id lexicográficamente mayor")
    void testCompareToGreater() {
        Ticket other = new Ticket("TKT-000", passenger, null, null,
                carriageLoad, carriagePassenger, 1, false, "01/01/2025");
        assertTrue(ticket.compareTo(other) > 0);
    }

    @Test
    @DisplayName("getLuggage retorna arreglo no nulo")
    void testGetLuggageNotNull() {
        assertNotNull(ticket.getLuggage());
    }

    @Test
    @DisplayName("getContactName retorna null si no fue asignado")
    void testContactNameNullInitially() {
        assertNull(ticket.getContactName());
    }
}
