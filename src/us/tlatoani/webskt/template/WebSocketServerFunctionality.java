package us.tlatoani.webskt.template;

import ch.njol.skript.Skript;
import com.google.common.collect.Iterators;
import us.tlatoani.webskt.events.*;

import java.util.*;

/**
 * Created by Tlatoani on 5/4/17.
 */
public class WebSocketServerFunctionality implements Iterable<Subsection> {
    public final String id;

    private boolean loaded = false;

    public final Subsection<WebSocketServerStartEvent> onStart =
            new Subsection<>("start", "WebSocketServerStart", WebSocketServerStartEvent.class);
    public final Subsection<WebSocketServerStopEvent> onStop =
            new Subsection<>("stop", "WebSocketServerStop", WebSocketServerStopEvent.class);
    public final Subsection<WebSocketOpenEvent.Server> onOpen =
            new Subsection<>("open", "WebSocketServerOpen", WebSocketOpenEvent.Server.class);
    public final Subsection<WebSocketHandshakeEvent.Server> onHandshake =
            new Subsection<>("handshake", "WebSocketServerHandshake", WebSocketHandshakeEvent.Server.class, true);
    public final Subsection<WebSocketCloseEvent.Server> onClose =
            new Subsection<>("close", "WebSocketServerClose", WebSocketCloseEvent.Server.class);
    public final Subsection<WebSocketMessageEvent.Server> onMessage =
            new Subsection<>("message", "WebSocketServerMessage", WebSocketMessageEvent.Server.class);
    public final Subsection<WebSocketErrorEvent.Server> onError =
            new Subsection<>("error", "WebSocketServerError", WebSocketErrorEvent.Server.class);

    private Set<String> serverVariableKeys = new HashSet<>();
    private Set<String> webSocketVariableKeys = new HashSet<>();

    public Set<String> getServerVariableKeys() {
        return Collections.unmodifiableSet(serverVariableKeys);
    }

    public Set<String> getWebSocketVariableKeys() {
        return Collections.unmodifiableSet(webSocketVariableKeys);
    }

    public boolean addServerVariableKey(String key) {
        if (key.contains("::") || key.contains("*")) {
            Skript.error("You can't have '::' or '*' in your server variable names.");
            return false;
        } else if (serverVariableKeys.contains(key)) {
            Skript.error("You already specified '" + key + "' as a server variable name!");
            return false;
        } else if (webSocketVariableKeys.contains(key)) {
            Skript.error("You already specified '" + key + "' as a websocket variable name!");
            return false;
        } else {
            serverVariableKeys.add(key);
            return true;
        }
    }

    public boolean addWebSocketVariableKey(String key) {
        if (key.contains("::") || key.contains("*")) {
            Skript.error("You can't have '::' or '*' in your websocket variable names.");
            return false;
        } else if (serverVariableKeys.contains(key)) {
            Skript.error("You already specified '" + key + "' as a server variable name!");
            return false;
        } else if (webSocketVariableKeys.contains(key)) {
            Skript.error("You already specified '" + key + "' as a websocket variable name!");
            return false;
        } else {
            webSocketVariableKeys.add(key);
            return true;
        }
    }

    public WebSocketServerFunctionality(String id) {
        this.id = id;
    }

    @Override
    public Iterator<Subsection> iterator() {
        return Iterators.forArray(onStart, onStop, onOpen, onHandshake, onClose, onMessage, onError);
    }

    public boolean isLoaded() {
        return loaded;
    }

    public void load() {
        loaded = true;
        for (Subsection subsection : this) {
            subsection.load();
        }
    }

    public void unload() {
        loaded = false;
        serverVariableKeys.clear();
        webSocketVariableKeys.clear();
        for (Subsection subsection : this) {
            subsection.unload();
        }
    }

    public String toString() {
        StringJoiner stringJoiner = new StringJoiner(", ", "WebSocketServerFunctionality(", ")");
        for (Subsection subsection : this) {
            stringJoiner.add(subsection.toString());
        }
        return stringJoiner.toString();
    }
}
