package us.tlatoani.webskt.syntaxes;

import us.tlatoani.mundocore.property_expression.MundoPropertyExpression;
import org.java_websocket.WebSocket;

import java.net.InetSocketAddress;
import java.util.Optional;

/**
 * Created by Tlatoani on 9/4/17.
 */
public class ExprWebSocketHost extends MundoPropertyExpression<WebSocket, String> {

    private InetSocketAddress getSocketAddress(WebSocket webSocket) {
        switch (getPropertyName()) {
            case "local host":
                return webSocket.getLocalSocketAddress();
            case "remote host":
            case "external host":
                return webSocket.getRemoteSocketAddress();
        }
        throw new IllegalStateException("Illegal getPropertyName() value: " + getPropertyName());
    }

    @Override
    public String convert(WebSocket webSocket) {
        return Optional.ofNullable(getSocketAddress(webSocket)).map(InetSocketAddress::getHostName).orElse(null);
    }
}
