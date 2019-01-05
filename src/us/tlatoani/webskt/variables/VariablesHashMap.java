package us.tlatoani.webskt.variables;

import java.util.HashMap;
import java.util.Set;
import java.util.Optional;

public class VariablesHashMap extends HashMap<String, Object> {
    private final Optional<VariablesHashMap> parent;
    private final Optional<Set<String>> keys;

    public VariablesHashMap(VariablesHashMap parent, Set<String> keys) {
        this.parent = Optional.ofNullable(parent);
        this.keys = Optional.ofNullable(keys);
    }

    private boolean keyMatches(String key) {
        return keys.map(Set -> Set.contains(key.split("::")[0])).orElse(true) || keyMatchesParent(key);
    }

    private boolean keyMatchesParent(String key) {
        return parent.map(map -> map.keyMatches(key)).orElse(false);
    }

    @Override
    public Object get(Object keyObj) {
        if (!(keyObj instanceof String)) {
            return null;
        }
        String key = (String) keyObj;
        if (!keyMatches(key)) {
            return null;
        }
        if (keyMatchesParent(key)) {
            VariablesHashMap parent = this.parent.get();
            return parent.get(key);
        } else {
            return super.get(key);
        }
    }

    @Override
    public Object put(String key, Object elem) {
        if (!keyMatches(key)) {
            return null;
        }
        if (keyMatchesParent(key)) {
            VariablesHashMap parent = this.parent.get();
            return parent.put(key, elem);
        } else {
            return super.put(key, elem);
        }
    }
}
