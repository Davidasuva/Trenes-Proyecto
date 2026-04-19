package server.model.route;

import edu.uva.app.graph.Vertex;
import edu.uva.app.graph.matrixGraph;
import edu.uva.app.linkedlist.singly.singly.LinkedList;

import java.io.Serializable;
import java.rmi.RemoteException;

public class RouteGraph implements Serializable {
    private static final long serialVersionUID = 1L;
    private matrixGraph<Station> graph;

    private Station a=new Station(0,"Estación Bonita");
    private Station b = new Station(1, "Vista Buena");
    private Station c = new Station(2, "Puerto Alto");
    private Station d = new Station(3, "Barracas");
    private Station e = new Station(4, "Manas");
    private Station f = new Station(5, "Madrid");
    private Station g = new Station(6, "Estación Colonia");
    private Station h = new Station(7, "Kennedy");
    private Station i = new Station(8, "Girón");
    private Station j = new Station(9, "PiedeCosta");
    private Station k = new Station(10, "Estación Colombia");

    public RouteGraph() throws RemoteException {
        graph=new matrixGraph<>(11);
        loadStations();
        loadRoutes();
    }

    private void loadStations(){
        graph.addVortex(a);
        graph.addVortex(b);
        graph.addVortex(c);
        graph.addVortex(d);
        graph.addVortex(e);
        graph.addVortex(f);
        graph.addVortex(g);
        graph.addVortex(h);
        graph.addVortex(i);
        graph.addVortex(j);
        graph.addVortex(k);
    }

    private void loadRoutes(){
        try{
            graph.addEdgeWithWeight(a,b,30);
            graph.addEdgeWithWeight(b,a,30);

            graph.addEdgeWithWeight(a,c,40);
            graph.addEdgeWithWeight(c,a,40);

            graph.addEdgeWithWeight(a,d,50);
            graph.addEdgeWithWeight(d,a,50);

            graph.addEdgeWithWeight(a,f,50);
            graph.addEdgeWithWeight(f,a,50);

            graph.addEdgeWithWeight(c,i,80);
            graph.addEdgeWithWeight(i,c,80);

            graph.addEdgeWithWeight(c, j, 120);
            graph.addEdgeWithWeight(j, c, 120);

            graph.addEdgeWithWeight(c, k, 110);
            graph.addEdgeWithWeight(k, c, 110);

            graph.addEdgeWithWeight(d, e, 20);
            graph.addEdgeWithWeight(e, d, 20);

            graph.addEdgeWithWeight(e, f, 65);
            graph.addEdgeWithWeight(f, e, 65);

            graph.addEdgeWithWeight(f, g, 80);
            graph.addEdgeWithWeight(g, f, 80);

            graph.addEdgeWithWeight(g, h, 30);
            graph.addEdgeWithWeight(h, g, 30);

            graph.addEdgeWithWeight(g, i, 145);
            graph.addEdgeWithWeight(i, g, 145);

            graph.addEdgeWithWeight(h, f, 30);
            graph.addEdgeWithWeight(f, h, 30);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public LinkedList<Station> getShortestPath(Station origin, Station destiny){
        return graph.dijkstra(origin,destiny);
    }

    public double getShortestDistance(Station origin, Station destination){
        return graph.dijkstraWeight(origin,destination);
    }

    public matrixGraph<Station> getGraph(){
        return graph;
    }

    public LinkedList<Station> getStations(){
        LinkedList<Station> stations=new LinkedList<>();
        Vertex[] vertexs=graph.getVertexs();
        for(int i=0;i<graph.numberVertex();i++){
            if(vertexs[i]!=null){
                stations.add((Station) vertexs[i].get());
            }
        }
        return stations;
    }
}
