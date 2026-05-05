import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import server.model.route.Station;

import java.rmi.RemoteException;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Station - Estación de tren")
class StationTest {

    private Station station;

    @BeforeEach
    void setUp() throws RemoteException {
        station = new Station(1, "Bucaramanga");
    }

    @Test
    @DisplayName("Constructor asigna id y nombre correctamente")
    void testConstructor() throws RemoteException {
        assertEquals(1, station.getId());
        assertEquals("Bucaramanga", station.getName());
    }

    @Test
    @DisplayName("setName actualiza el nombre")
    void testSetName() {
        station.setName("Bogotá");
        assertEquals("Bogotá", station.getName());
    }

    @Test
    @DisplayName("equals retorna true para estaciones con mismo nombre")
    void testEqualsTrue() throws RemoteException {
        Station other = new Station(99, "Bucaramanga");
        assertEquals(station, other);
    }

    @Test
    @DisplayName("equals retorna false para estaciones con nombres distintos")
    void testEqualsFalse() throws RemoteException {
        Station other = new Station(1, "Medellín");
        assertNotEquals(station, other);
    }

    @Test
    @DisplayName("equals retorna true para misma instancia")
    void testEqualsSameInstance() {
        assertEquals(station, station);
    }

    @Test
    @DisplayName("equals retorna false para null")
    void testEqualsNull() {
        assertNotEquals(null, station);
    }

    @Test
    @DisplayName("equals retorna false para tipo distinto")
    void testEqualsOtherType() {
        assertNotEquals("Bucaramanga", station);
    }

    @Test
    @DisplayName("toString contiene id y nombre")
    void testToString() {
        String str = station.toString();
        assertTrue(str.contains("1"));
        assertTrue(str.contains("Bucaramanga"));
    }

    @Test
    @DisplayName("Estaciones con mismo nombre pero distintos ids son iguales (equals por nombre)")
    void testEqualsByNameIgnoresId() throws RemoteException {
        Station s1 = new Station(1, "Madrid");
        Station s2 = new Station(5, "Madrid");
        assertEquals(s1, s2);
    }

    @Test
    @DisplayName("Estaciones con distintos nombres y mismos ids son distintas")
    void testDifferentNameSameId() throws RemoteException {
        Station s1 = new Station(1, "Madrid");
        Station s2 = new Station(1, "Barcelona");
        assertNotEquals(s1, s2);
    }
}
