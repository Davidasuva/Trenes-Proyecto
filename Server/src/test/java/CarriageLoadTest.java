import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import server.model.carriage.CarriageLoad;
import server.model.luggage.Luggage;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CarriageLoad - Vagón de carga")
class CarriageLoadTest {

    private CarriageLoad carriage;

    @BeforeEach
    void setUp() {
        carriage = new CarriageLoad(1, 5000);
    }

    @Test
    @DisplayName("Constructor inicializa con peso y cuenta en cero")
    void testConstructorInitialState() {
        assertEquals(1, carriage.getId());
        assertEquals(5000, carriage.getMaxCapacity());
        assertEquals(0, carriage.getActualWeight());
        assertEquals(0, carriage.getLuggageCount());
    }

    @Test
    @DisplayName("addLuggage agrega maleta válida exitosamente")
    void testAddLuggageValid() {
        Luggage luggage = new Luggage(1, 50);
        assertTrue(carriage.addLuggage(luggage));
        assertEquals(1, carriage.getLuggageCount());
        assertEquals(50, carriage.getActualWeight());
    }

    @Test
    @DisplayName("addLuggage rechaza maleta que supera 80 kg")
    void testAddLuggageOverweight() {
        Luggage heavyLuggage = new Luggage(1, 81);
        assertFalse(carriage.addLuggage(heavyLuggage));
        assertEquals(0, carriage.getLuggageCount());
    }

    @Test
    @DisplayName("addLuggage rechaza maleta justo en el límite de 80 kg - se acepta")
    void testAddLuggageExactlyLimit() {
        Luggage luggage = new Luggage(1, 80);
        assertTrue(carriage.addLuggage(luggage));
    }

    @Test
    @DisplayName("addLuggage rechaza cuando el vagón está lleno (MAX=2)")
    void testAddLuggageWhenFull() {
        carriage.addLuggage(new Luggage(1, 30));
        carriage.addLuggage(new Luggage(2, 30));
        Luggage extra = new Luggage(3, 20);
        assertFalse(carriage.addLuggage(extra));
        assertEquals(2, carriage.getLuggageCount());
    }

    @Test
    @DisplayName("addLuggage asigna el vagón a la maleta")
    void testAddLuggageSetsCarriage() {
        Luggage luggage = new Luggage(1, 40);
        carriage.addLuggage(luggage);
        assertEquals(carriage, luggage.getCarriage());
    }

    @Test
    @DisplayName("hasMoreCapacity retorna true cuando hay espacio")
    void testHasMoreCapacityTrue() {
        assertTrue(carriage.hasMoreCapacity());
    }

    @Test
    @DisplayName("hasMoreCapacity retorna false cuando está lleno")
    void testHasMoreCapacityFalse() {
        carriage.addLuggage(new Luggage(1, 30));
        carriage.addLuggage(new Luggage(2, 30));
        assertFalse(carriage.hasMoreCapacity());
    }

    @Test
    @DisplayName("hasMoreCapacity retorna true con una maleta (espacio = 1)")
    void testHasMoreCapacityPartial() {
        carriage.addLuggage(new Luggage(1, 30));
        assertTrue(carriage.hasMoreCapacity());
    }

    @Test
    @DisplayName("getLuggages no es null al crear el vagón")
    void testGetLuggagesNotNull() {
        assertNotNull(carriage.getLuggages());
    }

    @Test
    @DisplayName("MAX_LUGGAGES_PER_WAGON es 2")
    void testMaxLuggagesConstant() {
        assertEquals(2, CarriageLoad.MAX_LUGGAGES_PER_WAGON);
    }

    @Test
    @DisplayName("getActualWeight acumula el peso de varias maletas")
    void testActualWeightAccumulates() {
        carriage.addLuggage(new Luggage(1, 30));
        carriage.addLuggage(new Luggage(2, 25));
        assertEquals(55, carriage.getActualWeight());
    }

    @Test
    @DisplayName("equals compara por id")
    void testEquals() {
        CarriageLoad same = new CarriageLoad(1, 9999);
        assertEquals(carriage, same);
    }

    @Test
    @DisplayName("compareTo retorna 0 para mismo id")
    void testCompareTo() {
        CarriageLoad same = new CarriageLoad(1, 100);
        assertEquals(0, carriage.compareTo(same));
    }
}
