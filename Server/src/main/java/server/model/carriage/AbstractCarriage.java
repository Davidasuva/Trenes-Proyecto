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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AbstractCarriage)){
            return false;
        }
        AbstractCarriage c = (AbstractCarriage) o;
        return this.id == c.getId();
    }
}
