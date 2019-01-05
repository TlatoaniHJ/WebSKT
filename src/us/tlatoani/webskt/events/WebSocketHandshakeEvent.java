package us.tlatoani.webskt.events;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.handshake.ServerHandshake;
import org.java_websocket.handshake.ServerHandshakeBuilder;
import us.tlatoani.webskt.classes.WebSKTServer;

/**
 * Created by Tlatoani on 12/29/17.
 */
public class WebSocketHandshakeEvent extends WebSocketEvent {
    public final ClientHandshake request;
    public final ServerHandshake response;

    private WebSocketHandshakeEvent(WebSocket webSocket, ClientHandshake request, ServerHandshake response) {
        super(webSocket);
        this.request = request;
        this.response = response;
    }

    public static class Client extends WebSocketHandshakeEvent {

        public Client(WebSocket webSocket, ClientHandshake request, ServerHandshake response) {
            super(webSocket, request, response);
        }
    }

    public static class Server extends WebSocketHandshakeEvent implements WebSocketServerEvent {
        public final WebSKTServer server;
        public boolean allowed = true;

        public Server(WebSKTServer server, WebSocket webSocket, ClientHandshake request, ServerHandshakeBuilder response) {
            super(webSocket, request, response);
            this.server = server;
        }

        @Override
        public WebSKTServer getWebSocketServer() {
            return server;
        }

        //Used so that casting is not necessary for users of this class
        public ServerHandshakeBuilder getResponse() {
            return (ServerHandshakeBuilder) response;
        }
    }
}
