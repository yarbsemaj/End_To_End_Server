import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

/**
 * Created by yarbs on 01/04/2017.
 */
public class GUI {
    private JButton startServerButton;
    private JLabel serverStaus;
    private JTextPane incomingPane;
    private JTextPane outgoingPane;
    private JPanel panel;
    private boolean serverRunning = false;
    private Server receive;
    public static GUI i;

    public GUI() {
        i = this;
        startServerButton.addActionListener(e -> {
            if(!serverRunning) {
                serverStaus.setText("Running");
                startServerButton.setText("Stop Server");
                serverRunning=true;
                receive = new Server(25000);
                Thread t1 = new Thread(receive);
                t1.start();
            }else{
                serverStaus.setText("Stopped");
                startServerButton.setText("Start Server");
                serverRunning=false;
                try {
                    receive.stop();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });
    }

    public void addOutgoing(String message){
        outgoingPane.setText(outgoingPane.getText()+message+"\n");
    }

    public void addIncoming(String message){
        incomingPane.setText(incomingPane.getText()+message+"\n");
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Server");
        frame.setContentPane(new GUI().panel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
