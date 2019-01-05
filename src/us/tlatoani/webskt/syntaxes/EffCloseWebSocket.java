package us.tlatoani.webskt.syntaxes;

import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import org.java_websocket.WebSocket;
import org.java_websocket.framing.CloseFrame;
import org.bukkit.event.Event;

import java.util.Optional;

/**
 * Created by Tlatoani on 5/5/17.
 */
public class EffCloseWebSocket extends Effect {
    private Expression<WebSocket> webSocketExpr;
    private Optional<Expression<String>> messageExpr;

    @Override
    protected void execute(Event event) {
        WebSocket webSocket = webSocketExpr.getSingle(event);
        Optional<String> message = messageExpr.map(expr -> expr.getSingle(event));
        if (webSocket == null) {
            return;
        }
        if (message.isPresent()) {
            webSocket.close(CloseFrame.NORMAL, message.get());
        } else {
            webSocket.close();
        }
    }

    @Override
    public String toString(Event event, boolean b) {
        return "close websocket " + webSocketExpr + messageExpr.map(expr -> " with message " + expr).orElse("");
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        webSocketExpr = (Expression<WebSocket>) expressions[0];
        messageExpr = Optional.ofNullable((Expression<String>) expressions[1]);
        return true;
    }
}
