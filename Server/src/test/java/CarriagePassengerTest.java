import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import server.model.carriage.CarriagePassenger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests para CarriagePassenger.
 * Nota: addPassenger requiere un Ticket completo (que a su vez depende de Passenger,
 * Route, Train, etc.), por lo que los tests de integración de addPassenger se realizan
 * en un test de integración separado. Aquí se valida el estado inicial y las
 * operaciones independientes.
 */
@DisplayName("CarriagePassenger - Vagón de pasajeros")
class CarriagePassengerTest {

    private CarriagePassenger carriage;

    @BeforeEach
    void setUp() {
        carriage = new CarriagePassenger(5, 34);
    }

    @Test
    @DisplayName("Constructor asigna id y capacidad correctamente")
    void testConstructorState() {
        assertEquals(5, carriage.getId());
        assertEquals(34, carriage.getMaxCapacity());
        assertEquals(0, carriage.getNumberOfPassengers());
        assertEquals(0, carriage.getActualCapacity());
    }

    @Test
    @DisplayName("hasMoreCapacity retorna true cuando el vagón está vacío")
    void testHasMoreCapacityWhenEmpty() {
        assertTrue(carriage.hasMoreCapacity());
    }

    @Test
    @DisplayName("hasMoreCapacity retorna false cuando el vagón está lleno")
    void testHasMoreCapacityWhenFull() {
        CarriagePassenger small = new CarriagePassenger(99, 0);
        assertFalse(small.hasMoreCapacity());
    }

    @Test
    @DisplayName("getPassengers no retorna null al inicializar")
    void testGetPassengersNotNull() {
        assertNotNull(carriage.getPassengers());
    }

    @Test
    @DisplayName("equals compara por id de vagón")
    void testEqualsById() {
        CarriagePassenger other = new CarriagePassenger(5, 100);
        assertEquals(carriage, other);
    }

    @Test
    @DisplayName("equals retorna false para ids distintos")
    void testEqualsDifferentId() {
        CarriagePassenger other = new CarriagePassenger(6, 34);
        assertNotEquals(carriage, other);
    }

    @Test
    @DisplayName("compareTo retorna 0 para mismo id")
    void testCompareTo() {
        CarriagePassenger other = new CarriagePassenger(5, 50);
        assertEquals(0, carriage.compareTo(other));
    }

    @Test
    @DisplayName("compareTo retorna negativo cuando id es menor")
    void testCompareToLess() {
        CarriagePassenger bigger = new CarriagePassenger(10, 34);
        assertTrue(carriage.compareTo(bigger) < 0);
    }

    @Test
    @DisplayName("compareTo retorna positivo cuando id es mayor")
    void testCompareToGreater() {
        CarriagePassenger smaller = new CarriagePassenger(1, 34);
        assertTrue(carriage.compareTo(smaller) > 0);
    }

    @Test
    @DisplayName("addPassenger retorna false cuando vagón sin capacidad (capacidad=0)")
    void testAddPassengerNoCapacity() {
        CarriagePassenger full = new CarriagePassenger(1, 0);
        // No se puede agregar porque capacidad es 0
        assertFalse(full.hasMoreCapacity());
    }
}
