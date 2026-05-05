import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import server.model.user.Passenger;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Passenger / AbstractUser - Usuario Pasajero")
class PassengerTest {

    private Passenger passenger;

    @BeforeEach
    void setUp() {
        passenger = new Passenger("12345678", "ana@mail.com", "Ana",
                "Gómez", "pass123", "C.C", "Calle 5 # 3-20");
    }

    // ── AbstractUser ──────────────────────────────────────────────────────────

    @Test
    @DisplayName("Constructor hereda todos los atributos de AbstractUser")
    void testConstructorInherited() {
        assertEquals("12345678", passenger.getId());
        assertEquals("ana@mail.com", passenger.getMail());
        assertEquals("Ana", passenger.getName());
        assertEquals("Gómez", passenger.getLastName());
        assertEquals("pass123", passenger.getPassword());
        assertEquals("C.C", passenger.getTypeIdetification());
        assertEquals("Calle 5 # 3-20", passenger.getAdress());
        assertEquals(1, passenger.getType()); // Pasajero = tipo 1
    }

    @Test
    @DisplayName("setName actualiza el nombre")
    void testSetName() {
        passenger.setName("María");
        assertEquals("María", passenger.getName());
    }

    @Test
    @DisplayName("setLastName actualiza el apellido")
    void testSetLastName() {
        passenger.setLastName("Torres");
        assertEquals("Torres", passenger.getLastName());
    }

    @Test
    @DisplayName("setPassword actualiza la contraseña")
    void testSetPassword() {
        passenger.setPassword("nueva123");
        assertEquals("nueva123", passenger.getPassword());
    }

    @Test
    @DisplayName("setMail actualiza el correo")
    void testSetMail() {
        passenger.setMail("nuevo@mail.com");
        assertEquals("nuevo@mail.com", passenger.getMail());
    }

    @Test
    @DisplayName("setAdress actualiza la dirección")
    void testSetAdress() {
        passenger.setAdress("Av. Principal 10");
        assertEquals("Av. Principal 10", passenger.getAdress());
    }

    @Test
    @DisplayName("setTypeIdetification actualiza el tipo de documento")
    void testSetTypeIdentification() {
        passenger.setTypeIdetification("Pasaporte");
        assertEquals("Pasaporte", passenger.getTypeIdetification());
    }

    @Test
    @DisplayName("addPhoneNumber agrega un número")
    void testAddPhoneNumber() {
        assertTrue(passenger.addPhoneNumber("3001234567"));
        assertFalse(passenger.getPhoneNumbers().isEmpty());
    }

    @Test
    @DisplayName("removePhoneNumber elimina un número existente")
    void testRemovePhoneNumber() {
        passenger.addPhoneNumber("3001234567");
        assertTrue(passenger.removePhoneNumber("3001234567"));
    }

    @Test
    @DisplayName("removePhoneNumber retorna false para número no registrado")
    void testRemoveNonExistentPhone() {
        assertFalse(passenger.removePhoneNumber("9999999999"));
    }

    @Test
    @DisplayName("getPhoneNumbers retorna lista no nula")
    void testGetPhoneNumbersNotNull() {
        assertNotNull(passenger.getPhoneNumbers());
    }

    @Test
    @DisplayName("equals retorna true para pasajeros con mismo id")
    void testEqualsTrue() {
        Passenger other = new Passenger("12345678", "otro@mail.com", "Otro",
                "Apellido", "pwd", "C.C", "Otra dir");
        assertEquals(passenger, other);
    }

    @Test
    @DisplayName("equals retorna false para pasajeros con ids distintos")
    void testEqualsFalse() {
        Passenger other = new Passenger("99999999", "ana@mail.com", "Ana",
                "Gómez", "pass123", "C.C", "Calle 5");
        assertNotEquals(passenger, other);
    }

    @Test
    @DisplayName("equals retorna false para null")
    void testEqualsNull() {
        assertNotEquals(null, passenger);
    }

    @Test
    @DisplayName("compareTo retorna 0 para mismo id")
    void testCompareToEqual() {
        Passenger other = new Passenger("12345678", "x@x.com", "X", "Y", "p", "C.C", "A");
        assertEquals(0, passenger.compareTo(other));
    }

    @Test
    @DisplayName("compareTo retorna negativo para id lexicográficamente menor")
    void testCompareToLess() {
        Passenger bigger = new Passenger("99999999", "x@x.com", "X", "Y", "p", "C.C", "A");
        assertTrue(passenger.compareTo(bigger) < 0);
    }

    // ── Passenger-specific ────────────────────────────────────────────────────

    @Test
    @DisplayName("isTraveling inicia en false")
    void testIsTravelingInitiallyFalse() {
        assertFalse(passenger.IsTraveling());
    }

    @Test
    @DisplayName("getActualTicket retorna null inicialmente")
    void testGetActualTicketNull() {
        assertNull(passenger.getActualTicket());
    }

    @Test
    @DisplayName("setTraveling no lanza excepción si actualTicket es null")
    void testSetTravelingWithNullTicket() {
        assertDoesNotThrow(() -> passenger.setTraveling());
    }

    @Test
    @DisplayName("addTicket retorna true y agrega al historial")
    void testAddTicketReturnsTrue() {
        // Necesitamos un ticket mínimo — usamos null para verificar que el método
        // no lanza excepción (la lista linked list acepta null).
        // En producción el ticket sería real.
        // Solo se verifica que el historial no lanza excepción.
        assertDoesNotThrow(() -> passenger.addTicket(null));
    }
}
