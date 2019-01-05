package us.tlatoani.webskt.events;

import org.java_websocket.WebSocket;
import us.tlatoani.webskt.classes.WebSKTServer;

/**
 * Created by Tlatoani on 5/5/17.
 */
public class WebSocketCloseEvent extends WebSocketEvent {
    public final int code;
    public final String reason;
    public final boolean remote;

    public WebSocketCloseEvent(WebSocket webSocket, int code, String reason, boolean remote) {
        super(webSocket);
        this.code = code;
        this.reason = reason;
        this.remote = remote;
    }

    public static class Server extends WebSocketCloseEvent implements WebSocketServerEvent {
        public final WebSKTServer server;

        public Server(WebSKTServer server, WebSocket webSocket, int code, String reason, boolean remote) {
            super(webSocket, code, reason, remote);
            this.server = server;
        }

        @Override
        public WebSKTServer getWebSocketServer() {
            return server;
        }
    }
}
