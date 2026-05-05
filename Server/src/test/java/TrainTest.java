import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import server.model.carriage.CarriageLoad;
import server.model.carriage.CarriagePassenger;
import server.model.train.Train;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Train - Tren")
class TrainTest {

    private Train train;

    @BeforeEach
    void setUp() {
        train = new Train(1, "ExpressoBTA", "Mercedes-Benz", 4, 2, 5000);
    }

    @Test
    @DisplayName("Constructor asigna todos los atributos correctamente")
    void testConstructor() {
        assertEquals(1, train.getId());
        assertEquals("ExpressoBTA", train.getName());
        assertEquals("Mercedes-Benz", train.getType());
        assertEquals(4, train.getCapacity());
        assertEquals(2, train.getCargoWagons());
        assertEquals(5000, train.getMileage());
    }

    @Test
    @DisplayName("Lista de vagones se inicializa vacía")
    void testCarriagesInitiallyEmpty() {
        assertNotNull(train.getCarriages());
        assertTrue(train.getCarriages().isEmpty());
    }

    @Test
    @DisplayName("addCarriage agrega vagón de pasajeros")
    void testAddPassengerCarriage() {
        CarriagePassenger cp = new CarriagePassenger(10, 34);
        assertTrue(train.addCarriage(cp));
        assertFalse(train.getCarriages().isEmpty());
    }

    @Test
    @DisplayName("addCarriage agrega vagón de carga")
    void testAddLoadCarriage() {
        CarriageLoad cl = new CarriageLoad(11, 5440);
        assertTrue(train.addCarriage(cl));
    }

    @Test
    @DisplayName("removeCarriage elimina un vagón existente")
    void testRemoveCarriage() {
        CarriagePassenger cp = new CarriagePassenger(20, 34);
        train.addCarriage(cp);
        assertTrue(train.removeCarriage(cp));
    }

    @Test
    @DisplayName("removeCarriage retorna false para vagón no existente")
    void testRemoveNonExistentCarriage() {
        CarriagePassenger cp = new CarriagePassenger(99, 34);
        assertFalse(train.removeCarriage(cp));
    }

    @Test
    @DisplayName("setName actualiza el nombre del tren")
    void testSetName() {
        train.setName("RegionalSur");
        assertEquals("RegionalSur", train.getName());
    }

    @Test
    @DisplayName("setType actualiza el tipo del tren")
    void testSetType() {
        train.setType("Arnold");
        assertEquals("Arnold", train.getType());
    }

    @Test
    @DisplayName("setCapacity actualiza el número de vagones de pasajeros")
    void testSetCapacity() {
        train.setCapacity(6);
        assertEquals(6, train.getCapacity());
    }

    @Test
    @DisplayName("setCargoWagons actualiza el número de vagones de carga")
    void testSetCargoWagons() {
        train.setCargoWagons(3);
        assertEquals(3, train.getCargoWagons());
    }

    @Test
    @DisplayName("updateMileage suma al kilometraje existente")
    void testUpdateMileage() {
        train.updateMileage(200);
        assertEquals(5200, train.getMileage());
    }

    @Test
    @DisplayName("updateMileage con 0 no cambia el kilometraje")
    void testUpdateMileageZero() {
        train.updateMileage(0);
        assertEquals(5000, train.getMileage());
    }

    @Test
    @DisplayName("equals retorna true para trenes con mismo id")
    void testEqualsTrue() {
        Train other = new Train(1, "OtroNombre", "Arnold", 2, 1, 100);
        assertEquals(train, other);
    }

    @Test
    @DisplayName("equals retorna false para trenes con ids diferentes")
    void testEqualsFalse() {
        Train other = new Train(2, "ExpressoBTA", "Mercedes-Benz", 4, 2, 5000);
        assertNotEquals(train, other);
    }

    @Test
    @DisplayName("equals retorna false para null")
    void testEqualsNull() {
        assertNotEquals(null, train);
    }

    @Test
    @DisplayName("equals retorna false para objeto de otro tipo")
    void testEqualsWrongType() {
        assertNotEquals("not a train", train);
    }

    @Test
    @DisplayName("compareTo retorna 0 para mismo id")
    void testCompareToEqual() {
        Train other = new Train(1, "X", "Y", 1, 1, 0);
        assertEquals(0, train.compareTo(other));
    }

    @Test
    @DisplayName("compareTo retorna negativo cuando id es menor")
    void testCompareToLess() {
        Train bigger = new Train(10, "X", "Y", 1, 1, 0);
        assertTrue(train.compareTo(bigger) < 0);
    }

    @Test
    @DisplayName("compareTo retorna positivo cuando id es mayor")
    void testCompareToGreater() {
        Train smaller = new Train(0, "X", "Y", 1, 1, 0);
        assertTrue(train.compareTo(smaller) > 0);
    }
}
