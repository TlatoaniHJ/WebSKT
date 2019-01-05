package us.tlatoani.webskt.constructor;

import ch.njol.skript.ScriptLoader;
import ch.njol.skript.Skript;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.TriggerItem;
import ch.njol.skript.lang.Variable;
import org.bukkit.event.Event;
import org.java_websocket.WebSocket;
import us.tlatoani.mundocore.base.Logging;
import us.tlatoani.mundocore.event_scope.ScopeUtil;
import us.tlatoani.mundosyntax.*;
import us.tlatoani.mundosyntax.SyntaxPiece.MarkSpecificInformation;
import us.tlatoani.webskt.classes.WebSKTClient;
import us.tlatoani.webskt.events.WebSocketClientConstructorEvent;
import us.tlatoani.webskt.template.WebSocketClientFunctionality;
import us.tlatoani.webskt.variables.VariablesMapManipulator;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class WebSKTConstructor {
    public final SyntaxPiece syntax;
    public final WebSocketClientFunctionality functionality;

    public final String actualSyntax;
    public final String readableSyntax;
    private final VariableCollective variableCollective;
    private final Map<Integer, SyntaxPiece.MarkSpecificInformation> markSpecificInformationMap = new HashMap<>();

    private final SectionNode sectionNode;
    private TriggerItem triggerItem = null;

    private WebSKTConstructor(SyntaxPiece syntax, WebSocketClientFunctionality functionality, SectionNode sectionNode) {
        this.syntax = syntax;
        this.actualSyntax = syntax.actualSyntax(0);
        this.readableSyntax = syntax.readableSyntax();
        this.variableCollective = syntax.getVariables();
        this.functionality = functionality;
        this.sectionNode = sectionNode;
    }

    public static Optional<WebSKTConstructor> from(String syntax, WebSocketClientFunctionality functionality, SectionNode sectionNode) {
        SyntaxPiece syntaxPiece;
        try {
            syntaxPiece = SyntaxParser.parse(syntax);
        } catch (IllegalArgumentException e) {
            Logging.debug(WebSKTConstructor.class, e);
            Skript.error("Invalid constructor syntax!");
            return Optional.empty();
        } catch (Exception e) {
            Logging.reportException(WebSKTConstructor.class, e);
            Skript.error("An error occurred while validating the constructor syntax");
            return Optional.empty();
        }
        WebSKTConstructor constructor = new WebSKTConstructor(syntaxPiece, functionality, sectionNode);
        for (String variable : constructor.variableCollective) {
            if (syntaxPiece.getVariableUsage(variable) == VariableUsage.CONFLICTING) {
                Skript.error("Constructor syntax allows conflicting specification of the variable '" + variable + "'!");
                return Optional.empty();
            }
        }
        return Optional.of(constructor);
    }

    public void load() {
        ScriptLoader.setCurrentEvent("WebSocketClientConstructor", WebSocketClientConstructorEvent.class);
        triggerItem = ScopeUtil.loadSectionNodeOrDummy(sectionNode, null);
    }

    private MarkSpecificInformation getMarkSpecificInformation(int mark) {
        return markSpecificInformationMap.computeIfAbsent(mark, __ -> {
            MarkSpecificInformation result = new MarkSpecificInformation();
            syntax.setInformation(result, mark, 0);
            return result;
        });
    }

    public WebSocket construct(Event event, int mark, Expression[] expressions) {
        if (triggerItem == null) {
            throw new IllegalStateException("The triggerItem should have been loaded before WebSKTConstructor#construct() was called");
        }
        WebSocketClientConstructorEvent constructorEvent = new WebSocketClientConstructorEvent();

        VariablesMapManipulator vmm = new VariablesMapManipulator(functionality.getWebSocketVariableKeys(), null);
        VariablesMapManipulator.setParentVariables(constructorEvent, vmm);

        MarkSpecificInformation markSpecificInformation = getMarkSpecificInformation(mark);
        markSpecificInformation.exprIndexes.forEach((variable, ix) -> {
            ExpressionConstraints expressionConstraints = variableCollective.getExpression(variable);
            Expression expression = expressions[ix];
            for (ExpressionConstraints.Type type : expressionConstraints.types) {
                if (type.classInfo.getC().isAssignableFrom(expression.getReturnType()) && (expression.isSingle() || !type.isSingle)) {
                    if (type.isSingle) {
                        constructorEvent.setLocalVariable(variable, expression.getSingle(event));
                    } else {
                        Object[] array = expression.getArray(event);
                        for (int i = 1; i <= array.length; i++) {
                            constructorEvent.setLocalVariable(variable + Variable.SEPARATOR + i, array[i - 1]);
                        }
                    }
                    break;
                }
            }
        });
        markSpecificInformation.markVarValues.forEach((variable, val) -> constructorEvent.setLocalVariable(variable, val + 1));

        TriggerItem.walk(triggerItem, constructorEvent);

        if (constructorEvent.serverURI == null) {
            return null;
        }

        Map<String, String> headers = new HashMap<>();
        constructorEvent.request.iterateHttpFields().forEachRemaining(name -> {
            headers.put(name, constructorEvent.request.getFieldValue(name));
        });

        WebSKTClient result = new WebSKTClient(functionality, constructorEvent.serverURI, headers, vmm);
        result.connect();
        return result;
    }
}
