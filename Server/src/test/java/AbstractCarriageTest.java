import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import server.model.carriage.AbstractCarriage;
import server.model.carriage.CarriageLoad;
import server.model.carriage.CarriagePassenger;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("AbstractCarriage - Vagón base")
class AbstractCarriageTest {

    // Usamos CarriageLoad como implementación concreta de AbstractCarriage para los tests

    @Test
    @DisplayName("getId retorna el id asignado en el constructor")
    void testGetId() {
        AbstractCarriage carriage = new CarriageLoad(42, 1000);
        assertEquals(42, carriage.getId());
    }

    @Test
    @DisplayName("equals retorna true para misma instancia")
    void testEqualsSameInstance() {
        AbstractCarriage carriage = new CarriageLoad(1, 100);
        assertEquals(carriage, carriage);
    }

    @Test
    @DisplayName("equals retorna true para mismo id, distinto tipo de vagón")
    void testEqualsSameId() {
        AbstractCarriage load = new CarriageLoad(7, 1000);
        AbstractCarriage passenger = new CarriagePassenger(7, 30);
        assertEquals(load, passenger);
    }

    @Test
    @DisplayName("equals retorna false para ids distintos")
    void testEqualsDifferentIds() {
        AbstractCarriage a = new CarriageLoad(1, 1000);
        AbstractCarriage b = new CarriageLoad(2, 1000);
        assertNotEquals(a, b);
    }

    @Test
    @DisplayName("equals retorna false para null")
    void testEqualsNull() {
        AbstractCarriage carriage = new CarriageLoad(1, 100);
        assertNotEquals(null, carriage);
    }

    @Test
    @DisplayName("equals retorna false para objeto de otro tipo")
    void testEqualsOtherType() {
        AbstractCarriage carriage = new CarriageLoad(1, 100);
        assertNotEquals("string", carriage);
    }

    @Test
    @DisplayName("compareTo retorna 0 para mismo id")
    void testCompareToEqual() {
        AbstractCarriage a = new CarriageLoad(5, 100);
        AbstractCarriage b = new CarriagePassenger(5, 34);
        assertEquals(0, a.compareTo(b));
    }

    @Test
    @DisplayName("compareTo retorna negativo cuando el id es menor")
    void testCompareToLess() {
        AbstractCarriage a = new CarriageLoad(3, 100);
        AbstractCarriage b = new CarriageLoad(10, 100);
        assertTrue(a.compareTo(b) < 0);
    }

    @Test
    @DisplayName("compareTo retorna positivo cuando el id es mayor")
    void testCompareToGreater() {
        AbstractCarriage a = new CarriageLoad(10, 100);
        AbstractCarriage b = new CarriageLoad(3, 100);
        assertTrue(a.compareTo(b) > 0);
    }
}
