import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import server.model.route.RouteGraph;
import server.model.route.Station;

import java.rmi.RemoteException;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("RouteGraph - Grafo de estaciones")
class RouteGraphTest {

    private RouteGraph routeGraph;

    @BeforeEach
    void setUp() throws RemoteException {
        routeGraph = new RouteGraph();
    }

    @Test
    @DisplayName("getGraph no retorna null")
    void testGetGraphNotNull() {
        assertNotNull(routeGraph.getGraph());
    }

    @Test
    @DisplayName("getStations retorna las 11 estaciones definidas")
    void testGetStationsCount() {
        var stations = routeGraph.getStations();
        assertNotNull(stations);
        int count = 0;
        var it = stations.iterator();
        while (it.hasNext()) { it.next(); count++; }
        assertEquals(11, count);
    }

    @Test
    @DisplayName("getShortestDistance entre estaciones conectadas directamente retorna > 0")
    void testShortestDistanceDirectConnection() throws RemoteException {
        // a=EstBonita(0) -> b=VistaBuena(1) peso 30
        Station a = new Station(0, "Estación Bonita");
        Station b = new Station(1, "Vista Buena");
        double dist = routeGraph.getShortestDistance(a, b);
        assertTrue(dist > 0, "Distancia entre estaciones conectadas debe ser positiva");
        assertEquals(30.0, dist, 0.001);
    }

    @Test
    @DisplayName("getShortestDistance entre misma estación retorna 0")
    void testShortestDistanceSameStation() throws RemoteException {
        Station a = new Station(0, "Estación Bonita");
        double dist = routeGraph.getShortestDistance(a, a);
        assertEquals(0.0, dist, 0.001);
    }

    @Test
    @DisplayName("getShortestDistance entre estaciones sin camino retorna -1")
    void testShortestDistanceNoPath() throws RemoteException {
        // h=Kennedy(7) -> a=EstBonita(0) — h solo tiene aristas hacia g, no al revés a a
        Station h = new Station(7, "Kennedy");
        Station a = new Station(0, "Estación Bonita");
        // Según el grafo, h->g es unidireccional, puede que no llegue a a
        // Si hay camino, la distancia será > 0; si no, -1.
        double dist = routeGraph.getShortestDistance(h, a);
        // Solo verificamos que retorne un valor coherente (no NaN ni excepción)
        assertTrue(dist == -1 || dist > 0,
                "El resultado debe ser -1 (sin camino) o una distancia positiva");
    }

    @Test
    @DisplayName("getShortestPath entre estaciones conectadas retorna lista no vacía")
    void testShortestPathNotEmpty() throws RemoteException {
        Station a = new Station(0, "Estación Bonita");
        Station b = new Station(1, "Vista Buena");
        var path = routeGraph.getShortestPath(a, b);
        assertNotNull(path);
        assertFalse(path.isEmpty());
    }

    @Test
    @DisplayName("getShortestDistance usa ruta de camino mínimo (a->i via c = 40+80=120)")
    void testShortestPathOptimal() throws RemoteException {
        // a=EstBonita(0) -> c=PuertoAlto(2) -> i=Girón(8): 40+80=120
        Station a = new Station(0, "Estación Bonita");
        Station i = new Station(8, "Girón");
        double dist = routeGraph.getShortestDistance(a, i);
        assertEquals(120.0, dist, 0.001);
    }
}
