import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.*;
public class Server {
  private static int einzelID;
  private ArrayList<ClientThread> al;
  private ServerGUI sg;
  private SimpleDateFormat sdf;
  private int port;
  private boolean weiter;
  
  public Server(int port) {
    this(port, null);

    }

  public Server(int port, ServerGUI sg) {
    this.sg = sg;
    this.port = port;
    sdf = new SimpleDateFormat("HH:mm:ss");
    al = new ArrayList<ClientThread>();
    }
  
  public void start() {
    weiter = true;
    try{
      ServerSocket serverSocket = new ServerSocket(port);
      
        while(weiter){
        display("Server wartet auf Benutzer auf diesem Port: " + port + ".");
        Socket socket = serverSocket.accept();
        if(!weiter)
          break;
                 ClientThread t = new ClientThread(socket); 
                al.add(t);                                  
                t.start();
            }

            try {
             serverSocket.close();
                for(int i = 0; i < al.size(); ++i) {
                    ClientThread tc = al.get(i);
                    try {
                    tc.sInput.close();
                    tc.sOutput.close();
                    tc.socket.close();
                    }
                    catch(IOException ioE) {
          
                    }
                }

            }

            catch(Exception e) {
                display("Server und Client werden geschlossen " + e);
            }

        }
        catch (IOException e) {
            String msg = sdf.format(new Date()) + " Ausnahme des Server-Sockets " + e + "\n";
            display(msg);

        }

    }      
    protected void stop() {
        weiter = false;

        try {
            new Socket("lokaler Host", port);
        }
    
        catch(Exception e) {
    
        }

    }
  
    private void display(String msg) {
        String time = sdf.format(new Date()) + " " + msg;

        if(sg == null)
              System.out.println(time);

        else
            sg.appendEvent(time + "\n");
    }
  
    private synchronized void broadcast(String message) {
        String time = sdf.format(new Date());
        String messageLf = time + " " + message + "\n";
        if(sg == null)
            System.out.print(messageLf);
        else
            sg.appendRoom(messageLf);     

        for(int i = al.size(); --i >= 0;) {
            ClientThread ct = al.get(i);
            if(!ct.writeMsg(messageLf)) {
                al.remove(i);
                display("Benutzer getrennt " + ct.benutzer + " aus der Liste entfernt.");
            }
        }
    }
  
    synchronized void remove(int id) {

        for(int i = 0; i < al.size(); ++i) {
            ClientThread ct = al.get(i);
            if(ct.id == id) {
                al.remove(i);
                return;
            }

        }

    }

    public static void main(String[] args) {
        int portNumber = 4242;

        switch(args.length) {

            case 1:

                try {
                    portNumber = Integer.parseInt(args[0]);
                }

                catch(Exception e) {
                    System.out.println("ungültige Port Nummer.");
                    System.out.println("Nutzung ist: > java Server [portNumber]");
                   return;

                }

            case 0:
                break;
        
            default:
                System.out.println("Nutzung ist: > java Server [portNumber]");
                return;                 
        }
        Server server = new Server(portNumber);
        server.start();
    }

    class ClientThread extends Thread {
        Socket socket;
        ObjectInputStream sInput;
        ObjectOutputStream sOutput;
        int id;
        String benutzer;
        ChatMessage cm;
        String date;
        ClientThread(Socket socket) {
            id = ++einzelID;
            this.socket = socket;
            System.out.println("Thread versucht Objekte zu erstellen");
            try{
                sOutput = new ObjectOutputStream(socket.getOutputStream());
                sInput  = new ObjectInputStream(socket.getInputStream());
                benutzer = (String) sInput.readObject();
                display(benutzer + " hat sich verbunden.");

            }

            catch (IOException e) {
                display("Exception versucht Objekte zu erstellen " + e);
                return;
            }
      
            catch (ClassNotFoundException e) {

            }
            date = new Date().toString() + "\n";
        }

        public void run() {
            boolean weiter = true;
            while(weiter) {

                try {
                    cm = (ChatMessage) sInput.readObject();
                }

                catch (IOException e) {
                    display(benutzer + " Exception lesen des Streams: " + e);
                    break;             

                }

                catch(ClassNotFoundException e2) {
                    break;
                }
                String message = cm.getMessage();
                switch(cm.getType()) {
                 
                case ChatMessage.MESSAGE:
                    broadcast(benutzer + ": " + message);
                    break;

                case ChatMessage.LOGOUT:
                    display(benutzer + " hat sich mit einer Nachricht getrennt");
                    weiter = false;
                    break;

                case ChatMessage.WHOISIN:
                    writeMsg("Liste der aktuellen Benutzer: " + sdf.format(new Date()) + "\n");

                    for(int i = 0; i < al.size(); ++i) {
                        ClientThread ct = al.get(i);
                        writeMsg((i+1) + ") " + ct.benutzer + " seit " + ct.date);

                    }
                    break;
                }
            }
            remove(id);
            close();

        }

      private void close() {

            try {

                if(sOutput != null) sOutput.close();

            }

            catch(Exception e) {}

            try {

                if(sInput != null) sInput.close();

            }

            catch(Exception e) {};

            try {

                if(socket != null) socket.close();

            }

            catch (Exception e) {}

        }

 

        private boolean writeMsg(String msg) {

            if(!socket.isConnected()) {
                close();
                return false;

            }

            try {
                sOutput.writeObject(msg);

            }

            catch(IOException e) {
                display("senden fehlgeschlagen an Benutzer: " + benutzer);
                display(e.toString());

            }
            return true;

        }

    }

}

