package us.tlatoani.webskt.syntaxes;

import us.tlatoani.mundocore.property_expression.MundoPropertyExpression;
import org.java_websocket.WebSocket;

/**
 * Created by Tlatoani on 9/3/17.
 */
public class ExprWebSocketState extends MundoPropertyExpression<WebSocket, WebSocket.READYSTATE> {
    @Override
    public WebSocket.READYSTATE convert(WebSocket webSocket) {
        return webSocket.getReadyState();
    }
}
