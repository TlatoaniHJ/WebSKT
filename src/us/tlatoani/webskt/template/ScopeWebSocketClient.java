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
import us.tlatoani.webskt.constructor.WebSKTConstructor;

import java.util.Optional;

/**
 * Created by Tlatoani on 5/4/17.
 */
public class ScopeWebSocketClient extends MundoEventScope {
    private WebSocketClientFunctionality clientFunctionality;
    private boolean sync;

    @Override
    public void afterInit() {
        clientFunctionality.load();
        Logging.debug(this, "registered: " + clientFunctionality);
    }

    @Override
    public void unregister(Trigger trigger) {
        clientFunctionality.unload();
        Logging.debug(this, "unregistered");
    }

    @Override
    public void unregisterAll() {
        WebSocketManager.clearClientFunctionalities();
    }

    @Override
    public boolean init(Literal<?>[] literals, int i, SkriptParser.ParseResult parseResult) {
        clientFunctionality = WebSocketManager.getClientFunctionality(((Literal<String>) literals[0]).getSingle());
        SectionNode topNode = (SectionNode) SkriptLogger.getNode();
        Logging.debug(this, "init()ing");
        if (clientFunctionality.isLoaded()) {
            Skript.warning("You seem to have two 'websocket client' instances with the id \"" + clientFunctionality.id + "\" in your code."
                    + " If you do, note that only one of them will be used."
                    + " If you don't, you can ignore this warning.");
            clientFunctionality.unload();
        }
        boolean result = loadFunctionality(topNode);
        if (result) {
            clientFunctionality.registerConstructors();
        } else {
            clientFunctionality.unload();
        }
        ScopeUtil.removeSubNodes(topNode);
        return result;
    }

    private boolean loadFunctionality(SectionNode topNode) {
        nodeLoop: for (Node node : topNode) {
            SkriptLogger.setNode(node);
            Logging.debug(this, "Current node: " + node.getKey());
            if (!(node instanceof SectionNode)) {
                Skript.error("'websocket client' should only have sections directly under it!");
                return false;
            }
            SectionNode subNode = (SectionNode) node;
            if (subNode.getKey().startsWith("constructor ")) {
                String quotedSyntax = subNode.getKey().substring(12);
                if (!(quotedSyntax.startsWith("\"") && quotedSyntax.endsWith("\""))) {
                    Skript.error("The constructor syntax is not quoted properly!");
                    return false;
                }
                String unquotedSyntax = quotedSyntax.substring(1, quotedSyntax.length() - 1);
                Optional<WebSKTConstructor> constructorOptional =
                        WebSKTConstructor.from(unquotedSyntax, clientFunctionality, subNode);
                if (!constructorOptional.isPresent()) {
                    return false;
                }
                clientFunctionality.addConstructor(constructorOptional.get());
                continue nodeLoop;
            }
            if (subNode.getKey().equals("websocket variables") || subNode.getKey().equals("websocket vars")) {
                for (Node varKeyNode : subNode) {
                    SkriptLogger.setNode(varKeyNode);
                    if (varKeyNode instanceof SectionNode) {
                        Skript.error("The 'websocket variables' section should only be used to list names of websocket variables, you can't have a subsection there.");
                        return false;
                    } else if (!clientFunctionality.addWebSocketVariableKey(varKeyNode.getKey())) {
                        return false;
                    }
                }
                continue nodeLoop;
            }
            for (Subsection subsection : clientFunctionality) {
                Kleenean result = subsection.trySectionNode(subNode);
                if (result == Kleenean.TRUE) {
                    continue nodeLoop;
                } else if (result == Kleenean.FALSE) {
                    return false;
                }
            }
            Skript.error("The only sections allowed under 'websocket client' are 'on open', "
                    + "'on handshake', 'on close', 'on message', and 'on error'!");
            return false;
        }
        return true;
    }

    @Override
    public String toString(Event event, boolean b) {
        return "websocket " + " client \"" + clientFunctionality.id + "\"";
    }
}
