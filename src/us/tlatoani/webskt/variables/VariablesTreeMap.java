package us.tlatoani.webskt.variables;

import java.util.Set;
import java.util.Optional;
import java.util.TreeMap;

public class VariablesTreeMap extends TreeMap<String, Object> {
    private final Optional<VariablesTreeMap> parent;
    private final Optional<Set<String>> keys;

    public VariablesTreeMap(VariablesTreeMap parent, Set<String> keys) {
        this.parent = Optional.ofNullable(parent);
        this.keys = Optional.ofNullable(keys);
    }

    private boolean keyMatches(String key) {
        return keys.map(Set -> Set.contains(key)).orElse(true) || keyMatchesParent(key);
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
            VariablesTreeMap parent = this.parent.get();
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
            VariablesTreeMap parent = this.parent.get();
            return parent.put(key, elem);
        } else {
            return super.put(key, elem);
        }
    }
}
