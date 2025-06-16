package Connection;

import java.net.Socket;

public abstract class Connection implements Runnable {

    String root;
    Socket clientSocket;
    int id;

    public void run(){};
    //public void start??
//make an interface, might need more functions that the server can call?
//    What methods do my connections need to have?
    public void get(){}
    public void put(){}


}
