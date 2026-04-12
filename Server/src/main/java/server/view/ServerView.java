package server.view;
import java.awt.Color;
import java.awt.GridLayout;
import java.util.function.UnaryOperator;
import java.awt.GraphicsEnvironment;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import server.model.history.History;
import server.model.observer.Observer;
import server.model.observer.Subject;

public class ServerView extends Observer {

    private String tittle;
    private JFrame frame;
    private JButton button;
    private JPanel panelButton;
    private JPanel panelConsole;
    private JLabel console;

    public ServerView(String tittle, Subject subject) {
        super(subject);
        this.tittle = tittle;
        this.frame = new JFrame(tittle);
        this.button = new JButton("Deploy");
        this.panelButton = new JPanel();
        this.panelConsole = new JPanel();
        this.console = new JLabel("Status: Stopped.");
    }

    public void initComponents(UnaryOperator<Void> fn){
        console.setOpaque(false);
        console.setBackground(new Color(255, 255, 255));
        if(panelConsole.getComponentCount()==0){
            panelConsole.add(console);
        }
        panelConsole.setLayout(new GridLayout(1,1));
        if(panelButton.getComponentCount()==0){
            panelButton.setLayout(new GridLayout(3,3));
            startButton(fn);
        }

        if(GraphicsEnvironment.isHeadless()){
            return;
        }
        if(frame==null){
            frame=new JFrame(tittle);
        }
        frame.setSize(400,200);
        frame.setLayout(new GridLayout(2,1));
        frame.add(panelButton);
        frame.add(panelConsole);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public void startButton(UnaryOperator<Void> fn){
        button.addActionListener((e)->fn.apply(null));
        for(int i=0;i<6;i++){
            if(i==4){
                panelButton.add(button);
            }
            panelButton.add(new JLabel());
        }
    }

    public void startStatus(String status){
        button.setText(status);
        button.setEnabled(false);
        this.getHistory().addAction(status);
    }

    @Override
    public void update(){
        console.setText("Status: "+this.getHistory().getLastAction());
    }

    public History getHistory(){
        return (History) subject;
    }

    public void setMessage(String msg){
        this.getHistory().addAction(msg);
    }
}
