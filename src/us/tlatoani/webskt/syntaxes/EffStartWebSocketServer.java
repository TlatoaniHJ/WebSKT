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
public class EffStartWebSocketServer extends Effect {
    private Expression<String> idExpr;
    private Expression<Number> portExpr;

    @Override
    protected void execute(Event event) {
        String id = idExpr.getSingle(event);
        Optional<Integer> port = Optional.ofNullable(portExpr.getSingle(event)).map(Number::intValue);
        if (id != null && port.isPresent()) {
            WebSocketManager.startServer(port.get(), id);
        }
    }

    @Override
    public String toString(Event event, boolean b) {
        return "start websocket server " + idExpr + " at port " + portExpr;
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        idExpr = (Expression<String>) expressions[0];
        portExpr = (Expression<Number>) expressions[1];
        return true;
    }
}
