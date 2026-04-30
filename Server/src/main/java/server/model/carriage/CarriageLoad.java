package server.model.carriage;
import server.model.luggage.Luggage;
import edu.uva.app.stack.array.Stack;
public class CarriageLoad extends AbstractCarriage{
    private Stack<Luggage> luggages;
    private int maxCapacity;
    private int actualWeight;
    private int luggageCount;
    public static final int MAX_LUGGAGES_PER_WAGON = 2;

    public CarriageLoad(int id, int maxCapacity) {
        super(id);
        this.maxCapacity = maxCapacity;
        luggages=new Stack<>(MAX_LUGGAGES_PER_WAGON);
        luggageCount = 0;
    }


    public int getActualWeight() {
        return actualWeight;
    }

    public int getLuggageCount() {
        return luggageCount;
    }

    public boolean addLuggage(Luggage luggage){
        if(luggage.getWeight() > 80){
            return false;
        }
        if(luggageCount >= MAX_LUGGAGES_PER_WAGON){
            return false;
        }
        actualWeight += luggage.getWeight();
        luggage.setCarriage(this);
        boolean pushed = luggages.push(luggage);
        if(pushed) luggageCount++;
        return pushed;
    }


    public boolean hasMoreCapacity(){
        return luggageCount < MAX_LUGGAGES_PER_WAGON;
    }

    public Stack<Luggage> getLuggages() {
        return luggages;
    }

    public int getMaxCapacity() {
        return maxCapacity;
    }
}
