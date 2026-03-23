package server.model.carriage;
import java.io.Serializable;
public abstract class AbstractCarriage implements Serializable {
    private static final long serialVersionUID = 4L;

    private int id;

    public AbstractCarriage(int id) {
        this.id = id;
    }
    public int getId() {
        return id;
    }

}
