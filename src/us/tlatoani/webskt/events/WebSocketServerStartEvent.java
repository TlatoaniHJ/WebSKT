package us.tlatoani.webskt.events;

import us.tlatoani.mundocore.base_event.BaseEvent;
import us.tlatoani.webskt.classes.WebSKTServer;

/**
 * Created by Tlatoani on 5/5/17.
 */
public class WebSocketServerStartEvent extends BaseEvent implements WebSocketServerEvent {
    public final WebSKTServer webSocketServer;

    public WebSocketServerStartEvent(WebSKTServer webSocketServer) {
        this.webSocketServer = webSocketServer;
    }

    @Override
    public WebSKTServer getWebSocketServer() {
        return webSocketServer;
    }
}
