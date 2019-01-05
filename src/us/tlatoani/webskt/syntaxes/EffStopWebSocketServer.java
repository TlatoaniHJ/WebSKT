package us.tlatoani.webskt.syntaxes;

import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import org.bukkit.event.Event;
import us.tlatoani.webskt.WebSocketManager;

import java.util.Optional;

/**
 * Created by Tlatoani on 5/5/17.
 */
public class EffStopWebSocketServer extends Effect {
    private Expression<Number> portExpr;
    private Optional<Expression<Number>> timeoutExpr;

    @Override
    protected void execute(Event event) {
        Optional<Integer> port = Optional.ofNullable(portExpr.getSingle(event)).map(Number::intValue);
        int timeout = timeoutExpr.map(expr -> expr.getSingle(event)).map(Number::intValue).orElse(0);
        port.ifPresent(integer -> WebSocketManager.stopServer(integer, timeout));
    }

    @Override
    public String toString(Event event, boolean b) {
        return "stop websocket server at port " + portExpr + timeoutExpr.map(expr -> " with timeout" + expr).orElse("");
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        portExpr = (Expression<Number>) expressions[0];
        timeoutExpr = Optional.ofNullable((Expression<Number>) expressions[1]);
        return true;
    }
}
