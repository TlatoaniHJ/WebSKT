package us.tlatoani.webskt;

import us.tlatoani.mundocore.base.Logging;
import us.tlatoani.webskt.classes.WebSKTServer;
import us.tlatoani.webskt.constructor.ExprConstructedWebSocket;
import us.tlatoani.webskt.template.WebSocketClientFunctionality;
import us.tlatoani.webskt.template.WebSocketServerFunctionality;

import java.util.HashMap;
import java.util.Map;

public class WebSocketManager {
    private static final Map<String, WebSocketClientFunctionality> clientFunctionalities = new HashMap<>();
    private static final Map<String, WebSocketServerFunctionality> serverFunctionalities = new HashMap<>();
    private static final Map<Integer, WebSKTServer> servers = new HashMap<>();

    public static WebSocketClientFunctionality getClientFunctionality(String id) {
        return clientFunctionalities.computeIfAbsent(id, __ -> new WebSocketClientFunctionality(id));
    }

    public static void clearClientFunctionalities() {
        clientFunctionalities.values().forEach(WebSocketClientFunctionality::unload);
    }

    public static WebSocketServerFunctionality getServerFunctionality(String id) {
        return serverFunctionalities.computeIfAbsent(id, __ -> new WebSocketServerFunctionality(id));
    }

    public static void clearServerFunctionalities() {
        serverFunctionalities.values().forEach(WebSocketServerFunctionality::unload);
    }

    public static WebSKTServer getServer(int port) {
        return servers.get(port);
    }

    public static WebSKTServer startServer(int port, String id) {
        WebSKTServer server = new WebSKTServer(getServerFunctionality(id), port);
        if (servers.computeIfAbsent(port, __ -> server) != server) {
            throw new IllegalArgumentException("There is already a WebSocketServer put at the port " + port);
        }
        server.start();
        return server;
    }

    public static void stopServer(int port, int timeout) {
        WebSKTServer server = servers.get(port);
        if (server != null) {
            try {
                server.stop(timeout);
                servers.remove(port);
            } catch (InterruptedException e) {
                Logging.reportException(WebSKT.class, e);
            }
        }
    }

    public static void stopAllServers(int timeout) {
        for (WebSKTServer server : servers.values()) {
            try {
                server.stop(timeout);
            } catch (InterruptedException e) {
                Logging.reportException(WebSKT.class, e);
            }
        }
        servers.clear();
    }
}
