import java.net.*;
import java.io.*;
import java.util.*;


public class Client  {
  private ObjectInputStream sInput;
  private ObjectOutputStream sOutput;
  private Socket socket;
  private ClientGUI cg;
  private String server, benutzer;
  private int port;
  Client(String server, int port, String benutzer) {
    this(server, port, benutzer, null);
  }
  
  
  Client(String server, int port, String benutzer, ClientGUI cg) {
    this.server = server;
    this.port = port;
    this.benutzer = benutzer;
    this.cg = cg;
  }
  

  public boolean start() {
    try {
      socket = new Socket(server, port);
    } 
    
    catch(Exception ec) {
      display("Verbindung zum Server konnte nicht hergestellt werden: " + ec);
      return false;
    }
    
    String msg = "Verbindung angenommen " + socket.getInetAddress() + ":" + socket.getPort();
    display(msg);
    

    try
    {
      sInput  = new ObjectInputStream(socket.getInputStream());
      sOutput = new ObjectOutputStream(socket.getOutputStream());
    }
    
    
    catch (IOException eIO) {
      display("Exception erstellt neuen Input/output Streams: " + eIO);
      return false;
    }
    
     
    new ListenFromServer().start();
    

    try
    {
      sOutput.writeObject(benutzer);
    }
    
    
    catch (IOException eIO) {
      display("Exception führt Login durch : " + eIO);
      disconnect();
      return false;
    }
    return true;
  }

  
  private void display(String msg) {
    if(cg == null)
      System.out.println(msg); 
    else
      cg.append(msg + "\n");  
  }
  

  void sendMessage(ChatMessage msg) {
    try {
      sOutput.writeObject(msg);
    }
    catch(IOException e) {
      display("Exception sendet an den Server: " + e);
    }
  }

  
  private void disconnect() {
    try { 
      if(sInput != null) sInput.close();
    }
    catch(Exception e) {} 
    try {
      if(sOutput != null) sOutput.close();
    }
    catch(Exception e) {} 
        try{
      if(socket != null) socket.close();
    }
    catch(Exception e) {} 
    if(cg != null)
      cg.connectionFailed();
  }
 
  
  public static void main(String[] args) {
    int portNumber = 4242;
    String serverAddress = "Lokaler Host";
    String benutzer = "Unbekannt";

    switch(args.length) {
      case 3:
        serverAddress = args[2];
      case 2:
        try {
          portNumber = Integer.parseInt(args[1]);
        }
        catch(Exception e) {
          System.out.println("Ungültige Port Nummer.");
          System.out.println("Nutzung lautet: > java Client [benutzer] [portNumber] [serverAddress]");
          return;
        }
        
      
      case 1: 
        benutzer = args[0];
      case 0:
        break;
      default:
        System.out.println("Nutzung lautet: > java Client [benutzer] [portNumber] {serverAddress]");
      return;
    }
    Client client = new Client(serverAddress, portNumber, benutzer);
    if(!client.start())
    return;
      
    
    Scanner scan = new Scanner(System.in);
    
    
    while(true) {
      System.out.print("> ");
      String msg = scan.nextLine();
      if(msg.equalsIgnoreCase("LOGOUT")) {
        client.sendMessage(new ChatMessage(ChatMessage.LOGOUT, ""));
        break;
      }
      
      
      else if(msg.equalsIgnoreCase("Wer ist da")) {
        client.sendMessage(new ChatMessage(ChatMessage.WHOISIN, ""));       
      }
      else {   
        client.sendMessage(new ChatMessage(ChatMessage.MESSAGE, msg));
      }
    }
    

    client.disconnect();  
  }
  class ListenFromServer extends Thread {

    public void run() {
      while(true) {
        try {
          String msg = (String) sInput.readObject();
          if(cg == null) {
            System.out.println(msg);
            System.out.print("> ");
          }
          else {
            cg.append(msg);
          }
        }
        catch(IOException e) {
          display("Server hat die Verbdingung getrennt: " + e);
          if(cg != null) 
            cg.connectionFailed();
          break;
        }

        catch(ClassNotFoundException e2) {
        }
      }
    }
  }
}
