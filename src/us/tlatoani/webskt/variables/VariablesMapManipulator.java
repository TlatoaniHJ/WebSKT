package us.tlatoani.webskt.variables;

import ch.njol.skript.variables.Variables;
import org.bukkit.event.Event;
import us.tlatoani.mundocore.base.Logging;
import us.tlatoani.mundocore.reflection.Reflection;

import java.util.*;

public class VariablesMapManipulator {
    private final VariablesHashMap variablesHashMap;
    private final VariablesTreeMap variablesTreeMap;
    private final Object variablesMap;

    public static final Class<?> VARIABLES_MAP_CLASS =
            Reflection.getClass("ch.njol.skript.variables.VariablesMap");

    public static final Reflection.ConstructorInvoker VARIABLES_MAP_CONSTRUCTOR =
            Reflection.getConstructor(VARIABLES_MAP_CLASS);

    public static final Reflection.FieldAccessor<HashMap> VARIABLES_MAP_HASH_MAP =
            Reflection.getField(VARIABLES_MAP_CLASS, "hashMap", HashMap.class);
    public static final Reflection.FieldAccessor<TreeMap> VARIABLES_MAP_TREE_MAP =
            Reflection.getField(VARIABLES_MAP_CLASS, "treeMap", TreeMap.class);

    public static final Reflection.MethodInvoker VARIABLES_MAP_GET_VARIABLE =
            Reflection.getMethod(VARIABLES_MAP_CLASS, "getVariable", String.class);
    public static final Reflection.MethodInvoker VARIABLES_MAP_SET_VARIABLE =
            Reflection.getMethod(VARIABLES_MAP_CLASS, "setVariable", String.class, Object.class);

    private static Map localVariables = null;

    static {
        try {
            localVariables = (Map) Reflection.getStaticField(Variables.class, "localVariables");
        } catch (NoSuchFieldException | IllegalAccessException e) {
            Logging.reportException(VariablesMapManipulator.class, e);
        }
    }

    public VariablesMapManipulator(Set<String> allowedKeys, VariablesMapManipulator parent) {
        Optional<VariablesMapManipulator> parentOptional = Optional.ofNullable(parent);
        variablesHashMap = new VariablesHashMap(
                parentOptional.map(vmm -> vmm.variablesHashMap).orElse(null),
                allowedKeys);
        variablesTreeMap = new VariablesTreeMap(
                parentOptional.map(vmm -> vmm.variablesTreeMap).orElse(null),
                allowedKeys);
        variablesMap = VARIABLES_MAP_CONSTRUCTOR.invoke();
        VARIABLES_MAP_HASH_MAP.set(variablesMap, variablesHashMap);
        VARIABLES_MAP_TREE_MAP.set(variablesMap, variablesTreeMap);
    }

    public static void setParentVariables(Event event, VariablesMapManipulator parent) {
        VariablesMapManipulator vmm = new VariablesMapManipulator(null, parent);
        vmm.manipulate(event);
    }

    public void manipulate(Event event) {
        localVariables.put(event, variablesMap);
    }

    public Object getVariable(String name) {
        return VARIABLES_MAP_GET_VARIABLE.invoke(variablesMap, name);
    }

    public void setVariable(String name, Object value) {
        VARIABLES_MAP_SET_VARIABLE.invoke(variablesMap, name, value);
    }
}
