package server.model.carriage;
import server.model.luggage.Luggage;
import edu.uva.app.stack.array.Stack;
public class CarriageLoad extends AbstractCarriage{
    private Stack<Luggage> luggages;
    private int maxCapacity;
    private int actualWeight;

    public CarriageLoad(int id, int maxCapacity) {
        super(id);
        this.maxCapacity = maxCapacity;
        luggages=new Stack<>(maxCapacity);
    }


    public int getActualWeight() {
        return actualWeight;
    }

    public boolean addLuggage(Luggage luggage){
        if(luggage.getWeight()>80){
            return false;
        }else{
            actualWeight+=luggage.getWeight();
            luggage.setCarriage(this);
            return luggages.push(luggage);
        }
    }


    public boolean hasMoreCapacity(){
        return actualWeight < maxCapacity;
    }

    public Stack<Luggage> getLuggages() {
        return luggages;
    }

    public int getMaxCapacity() {
        return maxCapacity;
    }
}
