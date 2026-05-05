import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import server.model.carriage.CarriageLoad;
import server.model.luggage.Luggage;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Luggage - Maleta de equipaje")
class LuggageTest {

    private Luggage luggage;

    @BeforeEach
    void setUp() {
        luggage = new Luggage(1, 30);
    }

    @Test
    @DisplayName("Constructor asigna id y peso correctamente")
    void testConstructor() {
        assertEquals(1, luggage.getId());
        assertEquals(30, luggage.getWeight());
    }

    @Test
    @DisplayName("setWeight actualiza el peso")
    void testSetWeight() {
        luggage.setWeight(50);
        assertEquals(50, luggage.getWeight());
    }

    @Test
    @DisplayName("setCarriage asigna el vagón correctamente")
    void testSetCarriage() {
        CarriageLoad carriage = new CarriageLoad(10, 5000);
        luggage.setCarriage(carriage);
        assertEquals(carriage, luggage.getCarriage());
    }

    @Test
    @DisplayName("equals retorna true para misma maleta (mismo id)")
    void testEqualsTrue() {
        Luggage other = new Luggage(1, 70);
        assertEquals(luggage, other);
    }

    @Test
    @DisplayName("equals retorna false para distinto id")
    void testEqualsFalse() {
        Luggage other = new Luggage(2, 30);
        assertNotEquals(luggage, other);
    }

    @Test
    @DisplayName("equals retorna false comparando con null")
    void testEqualsNull() {
        assertNotEquals(null, luggage);
    }

    @Test
    @DisplayName("equals retorna false comparando con otro tipo")
    void testEqualsOtherType() {
        assertNotEquals("not a luggage", luggage);
    }

    @Test
    @DisplayName("compareTo retorna 0 para igual id")
    void testCompareToEqual() {
        Luggage other = new Luggage(1, 999);
        assertEquals(0, luggage.compareTo(other));
    }

    @Test
    @DisplayName("compareTo retorna negativo cuando id es menor")
    void testCompareToLess() {
        Luggage other = new Luggage(5, 10);
        assertTrue(luggage.compareTo(other) < 0);
    }

    @Test
    @DisplayName("compareTo retorna positivo cuando id es mayor")
    void testCompareToGreater() {
        Luggage smaller = new Luggage(0, 10);
        assertTrue(luggage.compareTo(smaller) > 0);
    }

    @Test
    @DisplayName("getCarriage retorna null si no fue asignado")
    void testGetCarriageNull() {
        assertNull(luggage.getCarriage());
    }

    @Test
    @DisplayName("getTicket retorna null inicialmente")
    void testGetTicketNull() {
        assertNull(luggage.getTicket());
    }
}
