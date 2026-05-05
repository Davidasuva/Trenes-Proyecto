import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import server.model.user.AbstractUser;
import server.model.user.Admin;
import server.model.user.Passenger;
import server.model.user.Worker;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Worker y Admin - Usuarios de alto privilegio")
class WorkerAndAdminTest {

    @Test
    @DisplayName("Worker tiene tipo 2")
    void testWorkerType() {
        Worker worker = new Worker("W001", "worker@mail.com", "Carlos",
                "Pérez", "pwd", "C.C", "Calle 1");
        assertEquals(2, worker.getType());
    }

    @Test
    @DisplayName("Worker hereda atributos de AbstractUser correctamente")
    void testWorkerAttributes() {
        Worker worker = new Worker("W002", "w@mail.com", "Luis",
                "Ruiz", "abc", "Pasaporte", "Av 2");
        assertEquals("W002", worker.getId());
        assertEquals("Luis", worker.getName());
        assertEquals("Ruiz", worker.getLastName());
    }

    @Test
    @DisplayName("Admin tiene tipo 3")
    void testAdminType() {
        // Admin constructor recibe 8 argumentos (el último se ignora, siempre es tipo 3)
        Admin admin = new Admin("A001", "admin@mail.com", "Sara",
                "Lopez", "admin123", "C.C", "Calle Admin", 99);
        assertEquals(3, admin.getType());
    }

    @Test
    @DisplayName("Admin hereda atributos de AbstractUser correctamente")
    void testAdminAttributes() {
        Admin admin = new Admin("A002", "a@mail.com", "Pedro",
                "Garcia", "secret", "C.C", "Dir 5", 0);
        assertEquals("A002", admin.getId());
        assertEquals("Pedro", admin.getName());
    }

    @Test
    @DisplayName("Worker y Admin son instancias de AbstractUser")
    void testPolymorphism() {
        AbstractUser worker = new Worker("W003", "x@x.com", "X", "Y", "p", "C.C", "A");
        AbstractUser admin = new Admin("A003", "z@z.com", "Z", "W", "p", "C.C", "B", 0);
        assertInstanceOf(AbstractUser.class, worker);
        assertInstanceOf(AbstractUser.class, admin);
    }

    @Test
    @DisplayName("equals entre Worker y Passenger con mismo id retorna true (compara por id)")
    void testEqualsAcrossTypes() {
        AbstractUser worker = new Worker("999", "w@mail.com", "W", "X", "p", "C.C", "A");
        AbstractUser passenger = new Passenger("999", "p@mail.com", "P", "Q", "p", "C.C", "B");
        assertEquals(worker, passenger);
    }

    @Test
    @DisplayName("Workers con ids diferentes no son iguales")
    void testWorkerNotEquals() {
        Worker w1 = new Worker("W001", "a@a.com", "A", "B", "p", "C.C", "C");
        Worker w2 = new Worker("W002", "a@a.com", "A", "B", "p", "C.C", "C");
        assertNotEquals(w1, w2);
    }

    @Test
    @DisplayName("Worker addPhoneNumber agrega un número correctamente")
    void testWorkerAddPhone() {
        Worker worker = new Worker("W005", "w@w.com", "W", "W", "p", "C.C", "A");
        assertTrue(worker.addPhoneNumber("3101234567"));
        assertFalse(worker.getPhoneNumbers().isEmpty());
    }

    @Test
    @DisplayName("compareTo entre Worker y Passenger con mismo id retorna 0")
    void testCompareToSameId() {
        Worker worker = new Worker("ABC", "w@w.com", "W", "W", "p", "C.C", "A");
        Passenger passenger = new Passenger("ABC", "p@p.com", "P", "P", "p", "C.C", "A");
        assertEquals(0, worker.compareTo(passenger));
    }
}
