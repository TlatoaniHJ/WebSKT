package us.tlatoani.webskt.constructor;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.coll.CollectionUtils;
import ch.njol.util.Kleenean;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;
import us.tlatoani.mundocore.base.Logging;
import us.tlatoani.mundocore.util.SkriptUtil;
import us.tlatoani.webskt.events.WebSocketClientConstructorEvent;

import java.net.URI;
import java.net.URISyntaxException;

public class ExprServerURI extends SimpleExpression<String> {

    @Override
    protected String[] get(Event event) {
        return new String[]{((WebSocketClientConstructorEvent) event).serverURI.toString()};
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<? extends String> getReturnType() {
        return String.class;
    }

    @Override
    public String toString(@Nullable Event e, boolean debug) {
        return "server uri";
    }

    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        if (SkriptUtil.isAssignableFromCurrentEvent(WebSocketClientConstructorEvent.class)) {
            return true;
        }
        Skript.error("'server uri' can only be used under 'constructor \"...\"'!");
        return false;
    }

    @Override
    public void change(Event event, Object[] delta, Changer.ChangeMode mode) {
        if (delta[0] == null) {
            return;
        }
        try {
            ((WebSocketClientConstructorEvent) event).serverURI = new URI((String) delta[0]);
        } catch (URISyntaxException e) {
            Skript.warning("Failed to parse '" + delta[0] + "' as a URI: " + e.getMessage());
            Logging.debug(this, e);
        }
    }

    @Override
    public Class<?>[] acceptChange(Changer.ChangeMode mode) {
        if (mode == Changer.ChangeMode.SET) {
            return CollectionUtils.array(String.class);
        }
        return null;
    }
}
