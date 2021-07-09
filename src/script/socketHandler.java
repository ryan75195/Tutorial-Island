package script;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class socketHandler {

    private Socket s;
    TutorialIsland m;

    public socketHandler(Socket s, TutorialIsland m) {
        this.m = m;
        this.s = s;
    }


    private String encodeMessage(String message) {
        int length = message.length();
        int bits = Integer.toString(length).length();
        return new String(("00000".substring(bits, 5) + length + ":" + message + '\n').getBytes(), StandardCharsets.UTF_8);
    }

    /* message format:
     * -- Farmer -- 0,Script,Amount,Stage
     * --  Mule  -- 1,AccountName, Location, World, isGiving
     * --  other -- 2,
     *
     *
     * */

    public boolean isConnected(){
        return s.isConnected();
    }

    public void getFunctions() throws IOException, InterruptedException {
        String allrequests = sendRequest("getRequests");
        System.out.println(allrequests);
    }

    public String sendRequest(String Message) throws IOException, InterruptedException {
        String data = null;

        if (s.isConnected()) {
            DataOutputStream d = new DataOutputStream(s.getOutputStream());
//            String encoded = encodeMessage(Message);
//            m.log(encoded);
//            m.log(encoded);
            String newMsg = "";
            for(char i : Message.toCharArray()){

                if(i == ' '){
                    newMsg += (char)20;
                }else{
                    newMsg += i;
                }
            }
            String encoded = new String((newMsg + '\n').getBytes(),StandardCharsets.UTF_8);
            m.log("sending " + encoded);
            d.writeChars(encoded);

//            d.writeChars(encoded);
            m.log("Sent");
            d.flush();
            BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
            data = getDecodedMessage(in,5);
//            m.log("[Server] Message received from server: " + data);

        }

        return data;
    }


    public ArrayList<String> getNameAndPID(String list){
        ArrayList<String> ret = new ArrayList<>();
        String buffer = "";
        boolean valuable = false;
        for(char i : list.toCharArray()){
            if(i == 39){
                if(!buffer.isEmpty()){
                    ret.add(buffer);
                    buffer = "";
                }
                valuable = !valuable;
            }else if(valuable){
                buffer += i;
            }
        }

        for(String s : ret){
//            m.log(s);
        }

        return ret;
    }

    private String getDecodedMessage(BufferedReader in, int StartBytes) throws IOException {
        String msg = "";
        String buffer;
        String message = "";
        String lengthStr = "";
        int len = -1;
//        m.log(1);

//            while (true) {
                if((buffer = in.readLine()) != null) {
                    for(char c : buffer.toCharArray()){

                        if(c != '\n'){
                            msg += c;
                        }
                        else{
                            return msg;
                        }
//                        if(lengthStr.length() < StartBytes) {
//                            lengthStr += c;
//                        }else{
//                            if(len != -1){
//                                msg += c;
//                                len --;
//                            }else{
//                                len = Integer.parseInt(lengthStr);
//                                msg += c;
//                                len --;
//                            }
//
//                            if(len > 0){
//                                break;
//                            }
//                        }
//                    }

//                    if(msg.length() == len){
//                        break;
//                    }
//                    m.log(4);
//                    msg += buffer;
//                    if(!in.ready()){
//                        break;
//                    }
                        if((buffer == null)){
                            break;
                        }
                    }


            }

//        m.log(3);
        message = msg;

//        m.log(4);

        return message;
    }

    private String getDecodedMessageTest(BufferedReader in, int StartBytes) throws IOException {
        m.log(1);
        String buffer = "";
        String message = "";
        String[] ret = new String[2];
        int length = getMessageLen(in, StartBytes);
        m.log(2);
        int remaining = length;
        m.log(4);
        while (remaining > 0 && (buffer = in.readLine()) != null) {
            m.log(5);
            m.log(buffer);
            message = buffer.substring(StartBytes + 1, StartBytes + 1 + length);
            m.log(message);
            if (message.length() >= length) {
                m.log(6);
                break;
            }
        }
        m.log(7);
        ret[0] = message;
        ret[1] = Integer.toString(length);

        return ret[0];
    }

    private int getMessageLen(BufferedReader in, int startBytes) throws IOException {
        String msg = "";
        String buffer = "";
        while ((buffer = in.readLine()) != null && msg.length() < startBytes) {
            msg = buffer;
            msg = msg.substring(0, startBytes);
            if (msg.length() >= startBytes) {
                break;
            }
        }
        int length = Integer.parseInt(msg);
        m.log(2);
        return length;
    }


}



