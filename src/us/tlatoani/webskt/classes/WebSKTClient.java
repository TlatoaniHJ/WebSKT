package us.tlatoani.webskt.classes;

import ch.njol.skript.Skript;
import org.bukkit.Bukkit;
import org.java_websocket.WebSocket;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.handshake.ServerHandshake;
import us.tlatoani.mundocore.base.Logging;
import us.tlatoani.webskt.WebSKT;
import us.tlatoani.webskt.events.*;
import us.tlatoani.webskt.template.WebSocketClientFunctionality;
import us.tlatoani.webskt.variables.VariablesMapManipulator;

import java.net.URI;
import java.util.Map;

/**
 * Created by Tlatoani on 5/5/17.
 */
public class WebSKTClient extends WebSocketClient {
    public final WebSocketClientFunctionality functionality;
    public final VariablesMapManipulator vmm;

    public WebSKTClient(WebSocketClientFunctionality functionality, URI serverURI) {
        super(serverURI);
        this.functionality = functionality;
        this.vmm = new VariablesMapManipulator(functionality.getWebSocketVariableKeys(), null);
    }

    public WebSKTClient(WebSocketClientFunctionality functionality, URI serverURI, Map<String, String> headers) {
        super(serverURI, headers);
        this.functionality = functionality;
        this.vmm = new VariablesMapManipulator(functionality.getWebSocketVariableKeys(), null);
    }

    public WebSKTClient(WebSocketClientFunctionality functionality, URI serverURI, Map<String, String> headers, VariablesMapManipulator vmm) {
        super(serverURI, headers);
        this.functionality = functionality;
        this.vmm = vmm;
    }

    @Override
    public void onWebsocketHandshakeReceivedAsClient(WebSocket conn, ClientHandshake request, ServerHandshake response) {
        functionality.onHandshake.run(new WebSocketHandshakeEvent.Client(this, request, response));
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        functionality.onOpen.run(new WebSocketOpenEvent.Client(this, handshakedata));
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        functionality.onClose.run(new WebSocketCloseEvent(this, code, reason, remote));
    }

    @Override
    public void onMessage(String message) {
        functionality.onMessage.run(new WebSocketMessageEvent(this, message));
    }

    @Override
    public void onError(Exception ex) {
        functionality.onError.run(new WebSocketErrorEvent(this, ex));
        if (!functionality.onError.isPresent() && WebSKT.PRINT_WEBSOCKET_ERRORS.getCurrentValue()) {
            Logging.info("An websocket error has occured within WebSKT");
            Logging.info("If you are unsure of why this error is occurring, please create an issue on WebSKT's GitHub link: " + WebSKT.getGitHubLink());
            Logging.info("Bukkit/Spigot version: " + Bukkit.getVersion());
            Logging.info("Skript version: " + Skript.getVersion());
            Logging.info("WebSKT version: " + WebSKT.version());
            if (getRemoteSocketAddress() == null) {
                Logging.info("Error in websocket client \"" + functionality.id + "\"");
            } else {
                Logging.info("Error in websocket client \"" + functionality.id +
                        "\", remote host = " + getRemoteSocketAddress().getHostName() + ", remote port = " + getRemoteSocketAddress().getPort());
            }
            ex.printStackTrace();
        }
    }

    @Override
    public String toString() {
        if (getRemoteSocketAddress() == null) {
            return "websocket client \"" + functionality.id
                    + "\" with hashcode " + hashCode();
        } else {
            return "websocket client \"" + functionality.id
                    + "\" connected to host \"" + getRemoteSocketAddress().getHostName()
                    + "\" port " + getRemoteSocketAddress().getPort()
                    + " with hashcode " + Integer.toHexString(hashCode());
        }
    }
}
