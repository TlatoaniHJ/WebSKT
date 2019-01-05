package us.tlatoani.webskt.template;

import ch.njol.skript.Skript;
import com.google.common.collect.Iterators;
import us.tlatoani.mundocore.grouped_list.GroupedList;
import us.tlatoani.webskt.constructor.ExprConstructedWebSocket;
import us.tlatoani.webskt.constructor.WebSKTConstructor;
import us.tlatoani.webskt.events.*;

import java.util.*;

/**
 * Created by Tlatoani on 5/4/17.
 */
public class WebSocketClientFunctionality implements Iterable<Subsection> {
    public final String id;

    private boolean loaded = false;

    public final Subsection<WebSocketOpenEvent.Client> onOpen =
            new Subsection<>("open", "WebSocketClientOpen", WebSocketOpenEvent.Client.class);
    public final Subsection<WebSocketHandshakeEvent.Client> onHandshake =
            new Subsection<>("handshake", "WebSocketClientHandshake", WebSocketHandshakeEvent.Client.class);
    public final Subsection<WebSocketCloseEvent> onClose =
            new Subsection<>("close", "WebSocketClientClose", WebSocketCloseEvent.class);
    public final Subsection<WebSocketMessageEvent> onMessage =
            new Subsection<>("message", "WebSocketClientMessage", WebSocketMessageEvent.class);
    public final Subsection<WebSocketErrorEvent> onError =
            new Subsection<>("error", "WebSocketClientError", WebSocketErrorEvent.class);

    private Collection<WebSKTConstructor> constructors = new ArrayList<>();
    private GroupedList.Key constructorKey = null;

    public void addConstructor(WebSKTConstructor constructor) {
        constructors.add(constructor);
    }

    private Set<String> webSocketVariableKeys = new HashSet<>();

    public Set<String> getWebSocketVariableKeys() {
        return Collections.unmodifiableSet(webSocketVariableKeys);
    }

    public boolean addWebSocketVariableKey(String key) {
        if (key.contains("::") || key.contains("*")) {
            Skript.error("You can't have '::' or '*' in your websocket variable names.");
            return false;
        } else if (!webSocketVariableKeys.add(key)) {
            Skript.error("You already specified '" + key + "' as a websocket variable name!");
            return false;
        } else {
            webSocketVariableKeys.add(key);
            return true;
        }
    }

    public WebSocketClientFunctionality(String id) {
        this.id = id;
    }

    @Override
    public Iterator<Subsection> iterator() {
        return Iterators.forArray(onOpen, onHandshake, onClose, onMessage, onError);
    }

    public boolean isLoaded() {
        return loaded;
    }

    public void registerConstructors() {
        if (!constructors.isEmpty()) {
            constructorKey = ExprConstructedWebSocket.registerConstructors(constructors);
        }
    }

    public void load() {
        loaded = true;
        for (Subsection subsection : this) {
            subsection.load();
        }
        for (WebSKTConstructor constructor : constructors) {
            constructor.load();
        }
    }

    public void unload() {
        loaded = false;
        webSocketVariableKeys.clear();
        for (Subsection subsection : this) {
            subsection.unload();
        }
        constructors.clear();
        if (constructorKey != null) {
            ExprConstructedWebSocket.unregisterConstructors(constructorKey);
            constructorKey = null;
        }
    }

    public String toString() {
        StringJoiner stringJoiner = new StringJoiner(", ", "WebSocketClientFunctionality(", ")");
        for (Subsection subsection : this) {
            stringJoiner.add(subsection.toString());
        }
        return stringJoiner.toString();
    }
}
