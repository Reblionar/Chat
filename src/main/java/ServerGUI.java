import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class ServerGUI extends JFrame implements ActionListener, WindowListener {
  
  // Anfang Attribute
  private static final long serialVersionUID = 1L;
  private JButton stopStart;
  private JTextArea chat, event;
  private JTextField tPortNumber;
  private Server server;
  // Ende Attribute

  ServerGUI(int port) {
    super("Chat Server");
    server = null; 
    
    JPanel north = new JPanel();
    north.add(new JLabel("Port Nummer: "));
    tPortNumber = new JTextField("  " + port);
    north.add(tPortNumber);
    
    stopStart = new JButton("Start");
    stopStart.addActionListener(this);
    north.add(stopStart);
    add(north, BorderLayout.NORTH);
    
    
    JPanel center = new JPanel(new GridLayout(2,1));
    chat = new JTextArea(80,80);
    chat.setEditable(false);
    appendRoom("Chat Raum.\n");
    center.add(new JScrollPane(chat));
    event = new JTextArea(80,80);
    event.setEditable(false);
    appendEvent("Ereignisprotokoll.\n");
    center.add(new JScrollPane(event)); 
    add(center);
    
    addWindowListener(this);
    setSize(400, 600);
    setVisible(true);
  }   
    // Anfang Komponenten
    // Ende Komponenten
  // Anfang Methoden

  void appendRoom(String str) {
    chat.append(str);
    chat.setCaretPosition(chat.getText().length() - 1);
  }
  void appendEvent(String str) {
    event.append(str);
    event.setCaretPosition(chat.getText().length() - 1);
    
  }
  
  public void actionPerformed(ActionEvent e) {
    if(server != null) {
      server.stop();
      server = null;
      tPortNumber.setEditable(true);
      stopStart.setText("Start");
      return;
    }
    
    int port;
    try {
      port = Integer.parseInt(tPortNumber.getText().trim());
    }
    catch(Exception er) {
      appendEvent("Ungültige IP");
      return;
    }
 
    server = new Server(port, this);

    new ServerRunning().start();
    stopStart.setText("Stop");
    tPortNumber.setEditable(false);
  }
  
  public static void main(String[] arg) {
    // start server default port 1500
    new ServerGUI(1500);
  }

  public void windowClosing(WindowEvent e) {
    // if my Server exist
    if(server != null) {
      try {
        server.stop();     
      }
      catch(Exception eClose) {
      }
      server = null;
    }
    dispose();
    System.exit(0);
  }
 
  public void windowClosed(WindowEvent e) {}
  public void windowOpened(WindowEvent e) {}
  public void windowIconified(WindowEvent e) {}
  public void windowDeiconified(WindowEvent e) {}
  public void windowActivated(WindowEvent e) {}
  public void windowDeactivated(WindowEvent e) {}
  // Ende Methoden

  class ServerRunning extends Thread {
  // Anfang Attribute1
  // Ende Attribute1
  // Anfang Methoden1
    public void run() {
      server.start();         
      
      stopStart.setText("Start");
      tPortNumber.setEditable(true);
      appendEvent("Server crashed\n");
      server = null;
    }
    // Anfang Komponenten1
    // Ende Komponenten1
  // Ende Methoden1
  }

}

