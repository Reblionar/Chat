import java.io.*;

public class ChatMessage implements Serializable{
  
       protected static final long serialVersionUID = 111212220L;
       
       static final int WHOISIN = 0, MESSAGE = 1, LOGOUT = 2;
       private int type;
       private String nachricht;
  
       ChatMessage(int type, String nachricht){
                       this.type = type;
                       this.nachricht = nachricht;
       }
  
       int getType() {
           return type;
         }
       
       String getMessage(){
           return nachricht;
         }
  }
       
