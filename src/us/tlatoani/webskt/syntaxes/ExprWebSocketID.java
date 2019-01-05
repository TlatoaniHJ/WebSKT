package us.tlatoani.webskt.syntaxes;

import us.tlatoani.mundocore.property_expression.MundoPropertyExpression;
import us.tlatoani.mundocore.util.OptionalUtil;
import org.java_websocket.WebSocket;
import us.tlatoani.webskt.classes.WebSKTClient;

/**
 * Created by Tlatoani on 9/3/17.
 */
public class ExprWebSocketID extends MundoPropertyExpression<WebSocket, String> {

    @Override
    public String convert(WebSocket webSocket) {
        return OptionalUtil.cast(webSocket, WebSKTClient.class).map(client -> client.functionality.id).orElse(null);
    }
}
