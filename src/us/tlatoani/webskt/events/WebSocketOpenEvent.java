package us.tlatoani.webskt.events;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.Handshakedata;
import us.tlatoani.webskt.classes.WebSKTServer;

/**
 * Created by Tlatoani on 5/5/17.
 */
public class WebSocketOpenEvent {

    private WebSocketOpenEvent() {}

    public static class Client extends WebSocketEvent {
        public final Handshakedata response;

        public Client(WebSocket webSocket, Handshakedata response) {
            super(webSocket);
            this.response = response;
        }
    }

    public static class Server extends WebSocketEvent implements WebSocketServerEvent {
        public final WebSKTServer server;
        public final Handshakedata request;

        public Server(WebSKTServer server, WebSocket webSocket, Handshakedata request) {
            super(webSocket);
            this.server = server;
            this.request = request;
        }

        @Override
        public WebSKTServer getWebSocketServer() {
            return server;
        }
    }
}
