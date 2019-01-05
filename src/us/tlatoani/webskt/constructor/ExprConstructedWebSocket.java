package us.tlatoani.webskt.constructor;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;
import org.java_websocket.WebSocket;
import us.tlatoani.mundocore.grouped_list.GroupedList;
import us.tlatoani.mundocore.reflective_registration.ModifiableSyntaxElementInfo;

import java.util.Collection;

public class ExprConstructedWebSocket extends SimpleExpression<WebSocket> {
    private static final ModifiableSyntaxElementInfo.Expression<ExprConstructedWebSocket, WebSocket> syntaxElementInfo =
            new ModifiableSyntaxElementInfo.Expression<>(ExprConstructedWebSocket.class, WebSocket.class, ExpressionType.COMBINED);
    private static final GroupedList<WebSKTConstructor> constructors = new GroupedList<>();
    private static boolean registered = false;

    private WebSKTConstructor constructor;
    private Expression[] expressions;
    private int mark;

    public static void registerSyntaxElementInfo() {
        syntaxElementInfo.register();
    }

    public static GroupedList.Key registerConstructors(Collection<WebSKTConstructor> constructors) {
        if (!registered) {
            syntaxElementInfo.register();
        }
        GroupedList.Key key = ExprConstructedWebSocket.constructors.addGroup(constructors);
        setPatterns();
        return key;
    }

    private static void setPatterns() {
        syntaxElementInfo.setPatterns(constructors.stream().map(constructor -> constructor.actualSyntax).toArray(String[]::new));
    }

    public static void unregisterConstructors(GroupedList.Key key) {
        constructors.removeGroup(key);
        setPatterns();
    }

    public static void unregisterAllContructors() {
        constructors.clear();
        syntaxElementInfo.setPatterns();
    }

    @Override
    protected WebSocket[] get(Event event) {
        WebSocket webSocket = constructor.construct(event, mark, expressions);
        return webSocket == null ? new WebSocket[0] : new WebSocket[]{webSocket};
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<? extends WebSocket> getReturnType() {
        return WebSocket.class;
    }

    @Override
    public String toString(@Nullable Event e, boolean debug) {
        return constructor.readableSyntax;
    }

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        constructor = constructors.get(matchedPattern);
        this.expressions = expressions;
        mark = parseResult.mark;
        return true;
    }
}
