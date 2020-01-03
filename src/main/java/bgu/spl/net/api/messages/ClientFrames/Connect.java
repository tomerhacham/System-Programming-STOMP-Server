package bgu.spl.net.api.messages.ClientFrames;

import bgu.spl.net.api.messages.AbstractFrame;
import javafx.util.Pair;

import java.util.LinkedList;
import java.util.List;

public class Connect extends AbstractFrame {
    public Connect(String version, String host, String login, String passcode) {
    super("CONNECT");
        Pair<String,String> accept_version_header = new Pair<>("accept-version:",version);
        Pair<String,String> host_header = new Pair<>("host:",host);
        Pair<String,String> login_header = new Pair<>("login:",login);
        Pair<String,String> passcode_header = new Pair<>("passcode:",passcode);

        List<Pair<String,String>> headers = new LinkedList<>();
        headers.add(accept_version_header);
        headers.add(host_header);
        headers.add(login_header);
        headers.add(passcode_header);

        super.setHeaders(headers);;
    }
}
