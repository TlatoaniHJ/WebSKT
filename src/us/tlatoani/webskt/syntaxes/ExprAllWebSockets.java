package us.tlatoani.webskt.syntaxes;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.iterator.EmptyIterator;
import org.bukkit.event.Event;
import org.java_websocket.WebSocket;
import org.java_websocket.server.WebSocketServer;
import us.tlatoani.mundocore.util.SkriptUtil;
import us.tlatoani.webskt.WebSocketManager;
import us.tlatoani.webskt.events.WebSocketServerEvent;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

/**
 * Created by Tlatoani on 5/5/17.
 */
public class ExprAllWebSockets extends SimpleExpression<WebSocket> {
    private Optional<Expression<Number>> portExpr;

    private Optional<Collection<WebSocket>> getList(Event event) {
        Optional<WebSocketServer> server = portExpr
                .map(expr -> Optional.ofNullable(expr.getSingle(event)))
                .map(port -> port.map(Number::intValue).<WebSocketServer>map(WebSocketManager::getServer))
                .orElseGet(() -> Optional.of(((WebSocketServerEvent) event).getWebSocketServer()));
        return server.map(s -> s.getConnections());
    }

    @Override
    protected WebSocket[] get(Event event) {
        return getList(event).map(list -> list.toArray(new WebSocket[0])).orElse(new WebSocket[0]);
    }

    @Override
    public Iterator<WebSocket> iterator(Event event) {
        return getList(event).map(Collection::iterator).orElseGet(EmptyIterator::new);
    }

    @Override
    public boolean isSingle() {
        return false;
    }

    @Override
    public Class<? extends WebSocket> getReturnType() {
        return WebSocket.class;
    }

    @Override
    public String toString(Event event, boolean b) {
        return "all websockets" + portExpr.map(expr -> " of server at port " + expr).orElse("");
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        portExpr = Optional.ofNullable((Expression<Number>) expressions[0]);
        if (!portExpr.isPresent()) {
            if (SkriptUtil.isAssignableFromCurrentEvent(WebSocketServerEvent.class)) {
                return true;
            }
            Skript.error("'all websockets' can only be used under 'websocket server'!");
            return false;
        }
        return true;
    }
}
