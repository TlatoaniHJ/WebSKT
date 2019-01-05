package us.tlatoani.webskt.syntaxes;

import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import us.tlatoani.webskt.events.WebSocketEvent;
import org.java_websocket.WebSocket;
import org.bukkit.event.Event;

/**
 * Created by Tlatoani on 5/5/17.
 */
public class EffWebSocketSendMessage extends Effect {
    private Expression<WebSocket> webSocketExpr;
    private Expression<String> messageExpr;

    @Override
    protected void execute(Event event) {
        String[] messages = messageExpr.getArray(event);
        for (WebSocket webSocket : webSocketExpr.getArray(event)) {
            for (String message : messages) {
                webSocket.send(message);
            }
        }
    }

    @Override
    public String toString(Event event, boolean b) {
        return "websocket send " + messageExpr + " through " + webSocketExpr;
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        messageExpr = (Expression<String>) expressions[0];
        webSocketExpr = (Expression<WebSocket>) expressions[1];
        return true;
    }
}
