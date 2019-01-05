package us.tlatoani.webskt.syntaxes;

import ch.njol.skript.ScriptLoader;
import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import us.tlatoani.mundocore.util.SkriptUtil;
import us.tlatoani.webskt.events.WebSocketServerEvent;
import org.bukkit.event.Event;

/**
 * Created by Tlatoani on 5/5/17.
 */
public class ExprWebSocketServerPort extends SimpleExpression<Number> {

    @Override
    protected Number[] get(Event event) {
        return new Number[]{((WebSocketServerEvent) event).getWebSocketServer().getPort()};
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<? extends Number> getReturnType() {
        return Integer.class;
    }

    @Override
    public String toString(Event event, boolean b) {
        return "websocket port";
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        if (SkriptUtil.isAssignableFromCurrentEvent(WebSocketServerEvent.class)) {
            return true;
        }
        Skript.error("'websocket port' can only be used under 'websocket server'!");
        return false;
    }
}
