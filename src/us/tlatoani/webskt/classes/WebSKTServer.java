package us.tlatoani.webskt.classes;

import ch.njol.skript.Skript;
import org.bukkit.Bukkit;
import org.java_websocket.WebSocket;
import org.java_websocket.drafts.Draft;
import org.java_websocket.exceptions.InvalidDataException;
import org.java_websocket.framing.CloseFrame;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.handshake.ServerHandshakeBuilder;
import org.java_websocket.server.WebSocketServer;
import us.tlatoani.mundocore.base.Logging;
import us.tlatoani.webskt.WebSKT;
import us.tlatoani.webskt.events.*;
import us.tlatoani.webskt.template.WebSocketServerFunctionality;
import us.tlatoani.webskt.variables.VariablesMapManipulator;

import java.net.InetSocketAddress;

/**
 * Created by Tlatoani on 5/5/17.
 */
public class WebSKTServer extends WebSocketServer {
    public final WebSocketServerFunctionality functionality;
    public final VariablesMapManipulator vmm;

    public WebSKTServer(WebSocketServerFunctionality functionality, int port) {
        super(new InetSocketAddress(port));
        this.functionality = functionality;
        this.vmm = new VariablesMapManipulator(functionality.getServerVariableKeys(), null);
        setWebSocketFactory(new WebSKTServerFactory());
        Logging.debug(this, functionality.toString());
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        functionality.onOpen.run(new WebSocketOpenEvent.Server(this, conn, handshake));
    }

    @Override
    public ServerHandshakeBuilder onWebsocketHandshakeReceivedAsServer(WebSocket conn, Draft draft, ClientHandshake request) throws InvalidDataException {
        Logging.debug(this, "handshake received as server = " + this);
        ServerHandshakeBuilder response = super.onWebsocketHandshakeReceivedAsServer(conn, draft, request);
        WebSocketHandshakeEvent.Server event = new WebSocketHandshakeEvent.Server(this, conn, request, response);
        functionality.onHandshake.run(event);
        if (!event.allowed) {
            Logging.debug(this, "connection disallowed, conn = " + conn);
            throw new InvalidDataException(CloseFrame.REFUSE);
        }
        Logging.debug(this, "connection allowed, conn = " + conn);
        return response;
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        functionality.onClose.run(new WebSocketCloseEvent.Server(this, conn, code, reason, remote));
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        functionality.onMessage.run(new WebSocketMessageEvent.Server(this, conn, message));
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        functionality.onError.run(new WebSocketErrorEvent.Server(this, conn, ex));
        if (!functionality.onError.isPresent() && WebSKT.PRINT_WEBSOCKET_ERRORS.getCurrentValue()) {
            Logging.info("An websocket error has occured within WebSKT");
            Logging.info("If you are unsure of why this error is occurring, please create an issue on WebSKT's GitHub page: " + WebSKT.getGitHubLink());
            Logging.info("Bukkit/Spigot version: " + Bukkit.getVersion());
            Logging.info("Skript version: " + Skript.getVersion());
            Logging.info("WebSKT version: " + WebSKT.version());
            if (conn.getRemoteSocketAddress() == null) {
                Logging.info("Error in websocket server \"" + functionality.id + "\", local port = " + getAddress().getPort());
            } else {
                Logging.info("Error in websocket server \"" + functionality.id + "\", local port = " + getAddress().getPort() +
                        ", remote host = " + conn.getRemoteSocketAddress().getHostName() + ", remote port = " + conn.getRemoteSocketAddress().getPort());
            }
            ex.printStackTrace();
        }
    }

    @Override
    public void onStart() {
        functionality.onStart.run(new WebSocketServerStartEvent(this));
    }

    @Override
    public void stop(int timeout) throws InterruptedException {
        super.stop(timeout);
        functionality.onStop.run(new WebSocketServerStopEvent(this));
    }

    @Override
    public String toString() {
        return "websocket server \"" + functionality.id
                + "\" at port " + getAddress().getPort()
                + " with hashcode " + Integer.toHexString(hashCode());
    }
}
