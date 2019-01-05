package us.tlatoani.webskt.events;

import org.java_websocket.WebSocket;
import us.tlatoani.webskt.classes.WebSKTServer;

/**
 * Created by Tlatoani on 5/5/17.
 */
public class WebSocketErrorEvent extends WebSocketEvent {
    public final Exception error;

    public WebSocketErrorEvent(WebSocket webSocket, Exception error) {
        super(webSocket);
        this.error = error;
    }

    public static class Server extends WebSocketErrorEvent implements WebSocketServerEvent {
        public final WebSKTServer server;

        public Server(WebSKTServer server, WebSocket webSocket, Exception error) {
            super(webSocket, error);
            this.server = server;
        }

        @Override
        public WebSKTServer getWebSocketServer() {
            return server;
        }
    }
}
