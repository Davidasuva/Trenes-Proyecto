import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import server.model.history.Action;
import server.model.history.History;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Action y History - Historial de acciones")
class ActionAndHistoryTest {

    // ── Action ────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("Action - getDescription retorna la descripción asignada")
    void testActionDescription() {
        Action action = new Action("Usuario inició sesión");
        assertEquals("Usuario inició sesión", action.getDescription());
    }

    @Test
    @DisplayName("Action - getTimestamp retorna cadena con formato de fecha")
    void testActionTimestampFormat() {
        Action action = new Action("Compra de tiquete");
        String ts = action.getTimestamp();
        assertNotNull(ts);
        // Formato: yyyy-MM-dd HH:mm:ss  — verificamos que contenga guiones y dos puntos
        assertTrue(ts.contains("-"), "Timestamp debe contener guiones de fecha");
        assertTrue(ts.contains(":"), "Timestamp debe contener dos puntos de hora");
        assertEquals(19, ts.length(), "Timestamp debe tener 19 caracteres");
    }

    @Test
    @DisplayName("Action - dos acciones diferentes tienen timestamps coherentes")
    void testActionTimestampOrder() throws InterruptedException {
        Action first = new Action("Primera acción");
        // Esperamos 1 ms para asegurar timestamps distintos
        Thread.sleep(1);
        Action second = new Action("Segunda acción");
        // La segunda acción no fue antes que la primera
        assertTrue(second.getTimestamp().compareTo(first.getTimestamp()) >= 0);
    }

    // ── History ───────────────────────────────────────────────────────────────

    private History history;

    @BeforeEach
    void setUp() {
        history = new History();
    }

    @Test
    @DisplayName("History - getLastAction retorna mensaje vacío cuando no hay acciones")
    void testGetLastActionEmpty() {
        String result = history.getLastAction();
        assertNotNull(result);
        assertTrue(result.contains("No actions yet"), "Debe indicar que no hay acciones");
    }

    @Test
    @DisplayName("History - addAction agrega acción y getLastAction la retorna")
    void testAddActionAndGetLast() {
        history.addAction("Login del admin");
        String last = history.getLastAction();
        assertNotNull(last);
        assertTrue(last.contains("Login del admin"), "La última acción debe contener la descripción");
    }

    @Test
    @DisplayName("History - getLastAction retorna la acción más reciente (LIFO)")
    void testGetLastActionLIFO() {
        history.addAction("Primera");
        history.addAction("Segunda");
        history.addAction("Tercera");
        String last = history.getLastAction();
        assertTrue(last.contains("Tercera"), "Debe retornar la última acción agregada");
    }

    @Test
    @DisplayName("History - múltiples addAction no lanza excepción")
    void testMultipleActionsNoException() {
        assertDoesNotThrow(() -> {
            for (int i = 0; i < 10; i++) {
                history.addAction("Acción " + i);
            }
        });
    }

    @Test
    @DisplayName("History - getLastAction incluye timestamp en el resultado")
    void testGetLastActionIncludesTimestamp() {
        history.addAction("Prueba timestamp");
        String last = history.getLastAction();
        // El formato es "yyyy-MM-dd HH:mm:ss: descripción"
        assertTrue(last.contains(":"), "El resultado debe incluir el separador de timestamp");
    }
}
