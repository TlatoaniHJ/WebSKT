package us.tlatoani.webskt.template;

import ch.njol.skript.Skript;
import ch.njol.skript.config.Node;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.Trigger;
import ch.njol.skript.log.SkriptLogger;
import ch.njol.util.Kleenean;
import org.bukkit.event.Event;
import us.tlatoani.mundocore.base.Logging;
import us.tlatoani.mundocore.event_scope.MundoEventScope;
import us.tlatoani.mundocore.event_scope.ScopeUtil;
import us.tlatoani.webskt.WebSocketManager;

/**
 * Created by Tlatoani on 5/4/17.
 */
public class ScopeWebSocketServer extends MundoEventScope {
    private WebSocketServerFunctionality serverFunctionality;
    private boolean sync;

    @Override
    protected void afterInit() {
        serverFunctionality.load();
        Logging.debug(this, "registered: " + serverFunctionality);
    }

    @Override
    public void unregister(Trigger trigger) {
        serverFunctionality.unload();
        Logging.debug(this, "unregistered");
    }

    @Override
    public void unregisterAll() {
        WebSocketManager.clearServerFunctionalities();
    }

    @Override
    public boolean init(Literal<?>[] literals, int i, SkriptParser.ParseResult parseResult) {
        serverFunctionality = WebSocketManager.getServerFunctionality(((Literal<String>) literals[0]).getSingle());
        SectionNode topNode = (SectionNode) SkriptLogger.getNode();
        Logging.debug(this, "init()ing");
        if (serverFunctionality.isLoaded()) {
            Skript.warning("You seem to have two 'websocket server' instances with the id \"" + serverFunctionality.id + "\" in your code."
                    + " If you do, note that only one of them will be used."
                    + " If you don't, you can ignore this warning.");
            serverFunctionality.unload();
        }
        boolean result = loadFunctionality(topNode);
        if (!result) {
            serverFunctionality.unload();
        }
        ScopeUtil.removeSubNodes(topNode);
        return result;
    }

    private boolean loadFunctionality(SectionNode topNode) {
        nodeLoop: for (Node node : topNode) {
            SkriptLogger.setNode(node);
            Logging.debug(this, "Current node: " + node.getKey());
            if (!(node instanceof SectionNode)) {
                Skript.error("'websocket server' should only have sections directly under it!");
                return false;
            }
            SectionNode subNode = (SectionNode) node;
            if (subNode.getKey().equals("server variables") || subNode.getKey().equals("server vars")) {
                for (Node varKeyNode : subNode) {
                    SkriptLogger.setNode(varKeyNode);
                    if (varKeyNode instanceof SectionNode) {
                        Skript.error("The 'server variables' section should only be used to list names of server variables, you can't have a subsection there.");
                        return false;
                    } else if (!serverFunctionality.addServerVariableKey(varKeyNode.getKey())) {
                        return false;
                    }
                }
                continue nodeLoop;
            }
            if (subNode.getKey().equals("websocket variables") || subNode.getKey().equals("websocket vars")) {
                for (Node varKeyNode : subNode) {
                    SkriptLogger.setNode(varKeyNode);
                    if (varKeyNode instanceof SectionNode) {
                        Skript.error("The 'websocket variables' section should only be used to list names of websocket variables, you can't have a subsection there.");
                        return false;
                    } else if (!serverFunctionality.addWebSocketVariableKey(varKeyNode.getKey())) {
                        return false;
                    }
                }
                continue nodeLoop;
            }
            for (Subsection subsection : serverFunctionality) {
                Kleenean result = subsection.trySectionNode(subNode);
                if (result == Kleenean.TRUE) {
                    continue nodeLoop;
                } else if (result == Kleenean.FALSE) {
                    return false;
                }
            }
            Skript.error("The only sections allowed under 'websocket server' are 'on start', 'on stop', "
                    + "'on open', 'on handshake, 'on close', 'on message', and 'on error'!");
            return false;
        }
        return true;
    }

    @Override
    public String toString(Event event, boolean b) {
        return "websocket " + " server \"" + serverFunctionality.id + "\"";
    }
}
