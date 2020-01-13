package bgu.spl.net.impl.stomp;

import bgu.spl.net.api.messages.AbstractFrame;
import bgu.spl.net.impl.User;
import bgu.spl.net.srv.ConnectionHandler;
import bgu.spl.net.srv.Connections;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class Database {
    //fields
    private static Database instance=null;

    ConcurrentHashMap<Integer, ConnectionHandler> connectionid_connectionHandler;
    ConcurrentHashMap<Integer, User> connectionid_user;
    ConcurrentHashMap<String, User> username_user;

    AtomicInteger nextMessageId;
    Connections connections;

    private Database(){
        connectionid_connectionHandler = new ConcurrentHashMap<>();
        connectionid_user = new ConcurrentHashMap<>();
        username_user = new ConcurrentHashMap<>();
        nextMessageId = new AtomicInteger(1);
    }

    public void setConnections(Connections connections) {
        this.connections = connections;
    }
    public static Database getInstance(){
        if(instance == null){
            instance = new Database();
        }
       return instance;
    }
    public int login(String username, String password, ConnectionHandler connectionHandler,Integer connectionid){
        Integer returnCode=null;

        if(username_user.containsKey(username))
        {
            User user = username_user.get(username);
            if(!user.isLoggin())
            {
               if(user.getPassword().equals(password))
                {
                    user.setConnectionId(connectionid);
                    connectionid_user.put(connectionid, user);
                    connections.addNewconnection(connectionid,connectionHandler);
                    user.login();
                    returnCode=0;
                }
               else//password was incorrect
                   {returnCode=2;
                   disconnect(connectionid);
                   }
            }
            else//user is already logged in
                {returnCode=1;
                disconnect(connectionid);}
        }
        else//user is not register
            {
                register(username,password, connectionHandler, connectionid);
                returnCode=0;
            }
        return returnCode;
    }
    public void addNewConnection(Integer connectionid, ConnectionHandler connectionHandler){
        connectionid_connectionHandler.put(connectionid,connectionHandler);
    }
    public void register(String username, String password, ConnectionHandler connectionhandler, Integer connectionid){
        User newUser = new User(username, password);
        newUser.login();
        username_user.put(username,newUser);
        connectionid_user.put(connectionid, newUser);
        newUser.setConnectionId(connectionid);
        connections.addNewconnection(connectionid,connectionhandler);
        return;


    }
    public void disconnect(Integer connectionid){
        connectionid_user.get(connectionid).logout();
        //todo: check if we need to remove the connectionHandler and which one (the prev or the the current)
        connectionid_connectionHandler.remove(connectionid, connectionid_connectionHandler.get(connectionid));
        connectionid_user.remove(connectionid, connectionid_user.get(connectionid));
        connections.disconnect(connectionid);
    }
    public void subscribe(String channel, String subscriptionid, Integer connectionid){
        connectionid_user.get(connectionid).addChannel(channel,subscriptionid);
        connections.subscribe(channel,subscriptionid , connectionid);
    }
    public void unsubscribe(String subscriptionid, Integer connectionid)
    {
        connectionid_user.get(connectionid).removeChannel(subscriptionid);
        connections.unsubscribe(subscriptionid,connectionid);
    }
    public User getUserbyConnectionId(Integer connectionid){
        return connectionid_user.get(connectionid);
    }
    public Integer getnextMesaageID(){
        return nextMessageId.getAndIncrement();
    }
}
