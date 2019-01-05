package us.tlatoani.webskt.classes;

import org.java_websocket.WebSocketAdapter;
import org.java_websocket.WebSocketImpl;
import org.java_websocket.drafts.Draft;
import org.java_websocket.server.DefaultWebSocketServerFactory;
import us.tlatoani.mundocore.base.Logging;

import java.util.List;

public class WebSKTServerFactory extends DefaultWebSocketServerFactory {

    @Override
    public WebSocketImpl createWebSocket(WebSocketAdapter adapter, Draft draft) {
        Logging.debug(this, "Creating WebSocketImpl for adapter = " + adapter);
        if (adapter instanceof WebSKTServer) {
            return new WebSKTServerConnection((WebSKTServer) adapter, draft);
        } else {
            return super.createWebSocket(adapter, draft);
        }
    }

    @Override
    public WebSocketImpl createWebSocket(WebSocketAdapter adapter, List<Draft> drafts) {
        Logging.debug(this, "Creating WebSocketImpl for adapter = " + adapter);
        if (adapter instanceof WebSKTServer) {
            return new WebSKTServerConnection((WebSKTServer) adapter, drafts);
        } else {
            return super.createWebSocket(adapter, drafts);
        }
    }
}
