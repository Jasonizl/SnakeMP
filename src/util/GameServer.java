package util;

import com.sun.org.apache.bcel.internal.generic.Select;
import com.sun.security.ntlm.Server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Created by Jason on 15.08.2017.
 */
public class GameServer {

    private ServerSocketChannel serverSocket;
    private Selector serverSelector;
    private ClientData clientData;

    private String hostname;
    private int port;
    private boolean isRunning = true;


    public GameServer(String hostname, int port) {
        this.hostname = hostname;
        this.port = port;
        this.clientData = new ClientData();
        initSocket();
    }

    /**
     * default socket initialization
     */
    private void initSocket() {
        try {
            serverSelector = Selector.open();
            serverSocket = ServerSocketChannel.open();
            serverSocket.configureBlocking(false);
            serverSocket.socket().bind(new InetSocketAddress(hostname, port));
            serverSocket.register(serverSelector, SelectionKey.OP_ACCEPT);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void serverLoop() {
        try {
            while(isRunning) {
                if(serverSelector.select() == 0) {
                    continue;
                }

                Set<SelectionKey> selectedKeys = serverSelector.selectedKeys();
                Iterator<SelectionKey> iter = selectedKeys.iterator();

                while(iter.hasNext()) {
                    SelectionKey key = iter.next();

                    // if someone wants to connect
                    if(key.isAcceptable() && clientData.client.size() < 3) {
                        ServerSocketChannel socket = (ServerSocketChannel) key.channel();
                        SocketChannel client = socket.accept();

                        client.configureBlocking(false);
                        client.register(serverSelector, SelectionKey.OP_READ, clientData.client.size());
                        clientData.client.add(client);
                    }
                    // if someone wants to send us something
                    if(key.isReadable()) {

                    }
                    iter.remove();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public boolean isRunning() {
        return isRunning;
    }

    public void setRunning(boolean running) {
        isRunning = running;
    }
}
