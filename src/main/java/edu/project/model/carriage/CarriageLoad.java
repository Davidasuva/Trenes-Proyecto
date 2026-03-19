package edu.project.model.carriage;

import edu.uva.app.stack.array.Stack;
import edu.project.model.luggage.Luggage;

public class CarriageLoad extends AbstractCarriage{

    private Stack<Luggage> luggages;
    private int maxCapacity;
    private int actualWeight;

    public CarriageLoad(int idCarriage, int maxCapacity) {
        super(idCarriage);
        this.maxCapacity = maxCapacity;
        this.actualWeight = 0;
        luggages= new Stack<>(maxCapacity);
    }

    public boolean addLuggage(Luggage luggage){
        if(actualWeight < maxCapacity){
            actualWeight++;
            return  luggages.push(luggage);
        }else{
            return false;
        }

    }
    public Luggage unloadLuggage(){
        if(actualWeight<=0){
            return null;
        }else{
            actualWeight--;
            return luggages.pop();
        }
    }

    public double getActualWeight() {
        return actualWeight;
    }


}
