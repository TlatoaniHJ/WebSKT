package us.tlatoani.webskt.events;

import org.java_websocket.WebSocket;
import us.tlatoani.webskt.classes.WebSKTServer;

/**
 * Created by Tlatoani on 5/5/17.
 */
public class WebSocketMessageEvent extends WebSocketEvent {
    public final String message;

    public WebSocketMessageEvent(WebSocket webSocket, String message) {
        super(webSocket);
        this.message = message;
    }

    public static class Server extends WebSocketMessageEvent implements WebSocketServerEvent {
        public final WebSKTServer server;

        public Server(WebSKTServer server, WebSocket webSocket, String message) {
            super(webSocket, message);
            this.server = server;
        }

        @Override
        public WebSKTServer getWebSocketServer() {
            return server;
        }
    }
}
