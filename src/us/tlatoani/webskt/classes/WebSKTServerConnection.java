package us.tlatoani.webskt.classes;

import org.java_websocket.WebSocketImpl;
import org.java_websocket.drafts.Draft;
import us.tlatoani.webskt.variables.VariablesMapManipulator;

import java.util.List;

public class WebSKTServerConnection extends WebSocketImpl {
    public final WebSKTServer server;
    public final VariablesMapManipulator vmm;

    public WebSKTServerConnection(WebSKTServer server, List<Draft> drafts) {
        super(server, drafts);
        this.server = server;
        this.vmm = new VariablesMapManipulator(
                server.functionality.getWebSocketVariableKeys(),
                server.vmm
        );
    }

    public WebSKTServerConnection(WebSKTServer server, Draft draft) {
        super(server, draft);
        this.server = server;
        this.vmm = new VariablesMapManipulator(
                server.functionality.getWebSocketVariableKeys(),
                server.vmm
        );
    }

    @Override
    public String toString() {
        if (getRemoteSocketAddress() == null) {
            return "connection of websocket server \"" + server.functionality.id
                    + "\" at port " + server.getAddress().getPort()
                    + " with hashcode " + Integer.toHexString(hashCode());
        } else {
            return "connection of websocket server \"" + server.functionality.id
                    + "\" at port " + server.getAddress().getPort()
                    + " connected to host \"" + getRemoteSocketAddress().getHostName()
                    + "\" port " + getRemoteSocketAddress().getPort()
                    + " with hashcode " + Integer.toHexString(hashCode());
        }
    }
}
