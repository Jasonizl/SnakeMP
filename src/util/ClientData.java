package util;

import jdk.nashorn.internal.ir.debug.JSONWriter;
import jdk.nashorn.internal.parser.JSONParser;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jason on 15.08.2017.
 */
public class ClientData {

    /**
     * - send data in json format
     * -
     */


    public List<SocketChannel> client = new ArrayList<>();
    private Charset msgCharset = null;
    private CharsetDecoder decoder;
    private ByteBuffer recvBuf;

    private String buffer, state, msg;

    public enum State {INIT, MOVE, MSG, QUIT}


    public ClientData() {
        recvBuf = ByteBuffer.allocate(1024);
        msgCharset = Charset.forName("US-ASCII");
        decoder = msgCharset.newDecoder();


    }

    public void sendMessage() {

    }

    public void recvMessage(int i) {
        recvBuf.clear();
        try {
           client.get(i).read(recvBuf);
        } catch (IOException e) {
            e.printStackTrace();
        }
        recvBuf.flip();

        CharBuffer charBuf = null;

        try {
            charBuf = decoder.decode(recvBuf);
        } catch(CharacterCodingException e) {
            e.printStackTrace();
        }

        buffer = charBuf.toString();
        if(!buffer.isEmpty()) {
            // http://theoryapp.com/parse-json-in-java/
            // https://stackoverflow.com/questions/2591098/how-to-parse-json-in-java
            state = buffer.substring(0,4); // read from json
            msg = buffer.substring(4); // read from json
        }

    }

    public void process(int i) {
        recvMessage(i);
    }

}
