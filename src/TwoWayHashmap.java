// TwoWayHashmap by Gopi
// https://stackoverflow.com/a/3430209

import java.util.Hashtable;
import java.util.Map;

public class TwoWayHashmap<K extends Object, V extends Object> {

    private Map<K,V> forward = new Hashtable<K, V>();
    private Map<V,K> backward = new Hashtable<V, K>();
    private int size = 0;

    public synchronized void put(K key, V value) {
        forward.put(key, value);
        backward.put(value, key);
        size++;
    }

    public synchronized V getForward(K key) {
        return forward.get(key);
    }

    public synchronized K getBackward(V key) {
        return backward.get(key);
    }

    public int size() {
        return size;
    }
}