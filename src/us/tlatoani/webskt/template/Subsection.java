package us.tlatoani.webskt.template;

import ch.njol.skript.ScriptLoader;
import ch.njol.skript.Skript;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.lang.TriggerItem;
import ch.njol.util.Kleenean;
import org.bukkit.event.Event;
import org.java_websocket.WebSocket;
import us.tlatoani.mundocore.base.Logging;
import us.tlatoani.mundocore.base.Scheduling;
import us.tlatoani.mundocore.event_scope.ScopeUtil;
import us.tlatoani.webskt.WebSKT;
import us.tlatoani.webskt.classes.WebSKTClient;
import us.tlatoani.webskt.classes.WebSKTServerConnection;
import us.tlatoani.webskt.events.WebSocketEvent;
import us.tlatoani.webskt.events.WebSocketServerEvent;
import us.tlatoani.webskt.variables.VariablesMapManipulator;

import java.util.Optional;
import java.util.concurrent.CountDownLatch;

public class Subsection<E extends Event> {
    public final String syntax;
    public final String eventName;
    public final Class<E> eventClass;
    public final boolean shouldLock;

    private Optional<TriggerItem> triggerItem = Optional.empty();

    private Optional<SectionNode> sectionNode = Optional.empty();

    public Subsection(String syntax, String eventName, Class<E> eventClass) {
        this(syntax, eventName, eventClass, false);
    }

    public Subsection(String syntax, String eventName, Class<E> eventClass, boolean shouldLock) {
        this.syntax = syntax;
        this.eventName = eventName;
        this.eventClass = eventClass;
        this.shouldLock = shouldLock;
    }

    /**
     * Checks if {@code sectionNode} is applicable to this Subsection and returns a Kleenean describing the result.
     * If {@code sectionNode} is applicable and there was no preexisting {@link SectionNode} in place,
     * then {@link Kleenean#TRUE} is returned.
     * If {@code sectionNode} is applicable and there was a preexisting {@link SectionNode} in place,
     * then {@link Skript#error(String)} is called and {@link Kleenean#FALSE} is returned.
     * If {@code sectionNode} is not applicable, then {@link Kleenean#UNKNOWN} is returned.
     * @param sectionNode
     */
    public Kleenean trySectionNode(SectionNode sectionNode) {
        if (sectionNode.getKey().equals("on " + syntax)) {
            if (this.sectionNode.isPresent()) {
                Skript.error("You cannot have two 'on " + syntax + "' sections here!");
                return Kleenean.FALSE;
            }
            this.sectionNode = Optional.of(sectionNode);
            return Kleenean.TRUE;
        }
        return Kleenean.UNKNOWN;
    }

    public void load() {
        triggerItem = sectionNode.flatMap(sectionNode -> {
            ScriptLoader.setCurrentEvent(eventName, eventClass);
            return ScopeUtil.loadSectionNode(sectionNode, null);
        });
        sectionNode = Optional.empty();
    }

    public void unload() {
        triggerItem = Optional.empty();
        sectionNode = Optional.empty();
    }

    public boolean isPresent() {
        return triggerItem.isPresent();
    }

    public void run(E event) {
        if (!WebSKT.isEnabledAtomic()) {
            return;
        }
        Logging.debug(this, this + " called");
        triggerItem.ifPresent(t -> {
            VariablesMapManipulator parentVMM;
            if (event instanceof WebSocketEvent) {
                WebSocket webSocket = ((WebSocketEvent) event).webSocket;
                if (webSocket instanceof WebSKTClient) {
                    parentVMM = ((WebSKTClient) webSocket).vmm;
                } else if (webSocket instanceof WebSKTServerConnection) {
                    parentVMM = ((WebSKTServerConnection) webSocket).vmm;
                } else {
                    parentVMM = null;
                }
            } else if (event instanceof WebSocketServerEvent) {
                parentVMM = ((WebSocketServerEvent) event).getWebSocketServer().vmm;
            } else {
                parentVMM = null;
            }
            if (shouldLock) {
                CountDownLatch countDownLatch = new CountDownLatch(1);
                Scheduling.sync(() -> {
                    if (parentVMM != null) {
                        VariablesMapManipulator.setParentVariables(event, parentVMM);
                    }
                    TriggerItem.walk(t, event);
                    countDownLatch.countDown();
                });
                try {
                    countDownLatch.await();
                } catch (InterruptedException e) {
                    Logging.reportException(this, e);
                }
            } else {
                Scheduling.sync(() -> {
                    if (parentVMM != null) {
                        VariablesMapManipulator.setParentVariables(event, parentVMM);
                    }
                    TriggerItem.walk(t, event);
                });
            }
        });
    }

    @Override
    public String toString() {
        return "Subsection(syntax: on " + syntax + ", triggerItem: " + triggerItem + ")";
    }
}
