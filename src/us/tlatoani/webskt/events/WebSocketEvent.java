package us.tlatoani.webskt.events;

import us.tlatoani.mundocore.base_event.BaseEvent;
import org.java_websocket.WebSocket;

/**
 * Created by Tlatoani on 5/5/17.
 */
public abstract class WebSocketEvent extends BaseEvent {
    public final WebSocket webSocket;

    public WebSocketEvent(WebSocket webSocket) {
        this.webSocket = webSocket;
    }
}
