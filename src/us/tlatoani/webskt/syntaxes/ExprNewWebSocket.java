package us.tlatoani.webskt.syntaxes;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import us.tlatoani.mundocore.base.Logging;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.Handshakedata;
import org.bukkit.event.Event;
import us.tlatoani.webskt.WebSocketManager;
import us.tlatoani.webskt.classes.WebSKTClient;
import us.tlatoani.webskt.template.WebSocketClientFunctionality;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Created by Tlatoani on 5/5/17.
 */
public class ExprNewWebSocket extends SimpleExpression<WebSocket> {
    private Expression<String> idExpr;
    private Expression<String> uriExpr;
    private Optional<Expression<Handshakedata>> handshakeExpr;

    @Override
    protected WebSocket[] get(Event event) {
        String id = idExpr.getSingle(event);
        if (id == null) {
            return new WebSocket[0];
        }
        WebSocketClientFunctionality functionality = WebSocketManager.getClientFunctionality(id);
        String uriStr = uriExpr.getSingle(event);
        URI uri;
        try {
            uri = new URI(uriStr);
        } catch (URISyntaxException e) {
            Skript.warning("Failed to parse '" + uriStr + "' as a URI: " + e.getMessage());
            Logging.debug(this, e);
            return new WebSocket[0];
        } catch (NullPointerException e) {
            Logging.debug(this, e);
            return new WebSocket[0];
        }
        Optional<Handshakedata> handshakeOptional = handshakeExpr.map(expr -> expr.getSingle(event));
        WebSKTClient webSocket = handshakeOptional.map(handshake -> {
            Map<String, String> headers = new HashMap<>();
            handshake.iterateHttpFields().forEachRemaining(name -> {
                headers.put(name, handshake.getFieldValue(name));
            });
            return new WebSKTClient(functionality, uri, headers);
        }).orElseGet(() -> new WebSKTClient(functionality, uri));
        webSocket.connect();
        return new WebSocket[]{webSocket};
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<? extends WebSocket> getReturnType() {
        return WebSKTClient.class;
    }

    @Override
    public String toString(Event event, boolean b) {
        return "websocket " + idExpr + " connected to uri " + uriExpr + handshakeExpr.map(expr -> " with request " + expr).orElse("");
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        idExpr = (Expression<String>) expressions[0];
        uriExpr = (Expression<String>) expressions[1];
        handshakeExpr = Optional.ofNullable((Expression<Handshakedata>) expressions[2]);
        return true;
    }
}
