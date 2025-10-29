/******************************************************************
 *
 *   Jane Dunbar / Section 002
 *
 ********************************************************************/

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.lang.Math;

@SuppressWarnings("unchecked")
public class CuckooHash<K, V> {

    private int CAPACITY;  				// Hashmap capacity
    private Bucket<K, V>[] table;		// Hashmap table
    private int a = 37, b = 17;			// Constants used in h2(key)

    private class Bucket<K, V> {
        private K bucKey = null;
        private V value = null;

        public Bucket(K k, V v) {
            bucKey = k;
            value = v;
        }

        private K getBucKey() { return bucKey; }
        private V getValue()  { return value;  }
    }

    private int hash1(K key) { return Math.abs(key.hashCode()) % CAPACITY; }

    private int hash2(K key) {
        return Math.abs((a * key.hashCode() + b) % CAPACITY);
    }

    public CuckooHash(int size) {
        CAPACITY = size;
        table = new Bucket[CAPACITY];
    }

    public int size() {
        int count = 0;
        for (int i = 0; i < CAPACITY; ++i) {
            if (table[i] != null) count++;
        }
        return count;
    }

    public void clear() {
        table = new Bucket[CAPACITY];
    }

    public int mapSize() { return CAPACITY; }    // used in external testing only

    public List<V> values() {
        List<V> allValues = new ArrayList<V>();
        for (int i = 0; i < CAPACITY; ++i) {
            if (table[i] != null) {
                allValues.add(table[i].getValue());
            }
        }
        return allValues;
    }

    public Set<K> keys() {
        Set<K> allKeys = new HashSet<K>();
        for (int i = 0; i < CAPACITY; ++i) {
            if (table[i] != null) {
                allKeys.add(table[i].getBucKey());
            }
        }
        return allKeys;
    }

    public void put(K key, V value) {
        if (key == null) return;

        K currKey = key;
        V currVal = value;

        while (true) { // loop until insertion succeeds
            boolean useFirst = true;

            for (int attempt = 0; attempt < CAPACITY; attempt++) {
                int pos1 = hash1(currKey);
                int pos2 = hash2(currKey);

                if (table[pos1] != null && table[pos1].getBucKey().equals(currKey)) {
                    table[pos1] = new Bucket<>(currKey, currVal);
                    return;
                }
                if (table[pos2] != null && table[pos2].getBucKey().equals(currKey)) {
                    table[pos2] = new Bucket<>(currKey, currVal);
                    return;
                }

                int pos = useFirst ? pos1 : pos2;
                if (table[pos] == null) {
                    table[pos] = new Bucket<>(currKey, currVal);
                    return;
                }

                Bucket<K, V> kicked = table[pos];
                table[pos] = new Bucket<>(currKey, currVal);
                currKey = kicked.getBucKey();
                currVal = kicked.getValue();
                useFirst = !useFirst;
            }


            rehash();
        }
    }


    public V get(K key) {
        if (key == null) return null;  // fix for Error 17
        int pos1 = hash1(key);
        int pos2 = hash2(key);
        if (table[pos1] != null && table[pos1].getBucKey().equals(key))
            return table[pos1].getValue();
        else if (table[pos2] != null && table[pos2].getBucKey().equals(key))
            return table[pos2].getValue();
        return null;
    }

    public boolean remove(K key, V value) {
        if (key == null) return false;  // fix for Error 17
        int pos1 = hash1(key);
        int pos2 = hash2(key);
        if (table[pos1] != null && table[pos1].getBucKey().equals(key) && table[pos1].getValue().equals(value)) {
            table[pos1] = null;
            return true;
        }
        else if (table[pos2] != null && table[pos2].getBucKey().equals(key) && table[pos2].getValue().equals(value)) {
            table[pos2] = null;
            return true;
        }
        return false;
    }

    public String printTable() {
        StringBuilder sb = new StringBuilder();
        sb.append("[ ");
        for (int i = 0; i < CAPACITY; ++i) {
            if (table[i] != null) {
                sb.append("<");
                sb.append(table[i].getBucKey());
                sb.append(", ");
                sb.append(table[i].getValue());
                sb.append("> ");
            }
        }
        sb.append("]");
        return sb.toString();
    }

    private void rehash() {
        Bucket<K, V>[] tableCopy = table.clone();
        int oldCap = CAPACITY;
        CAPACITY = (CAPACITY * 2) + 1;
        table = new Bucket[CAPACITY];
        a = 31 + (int)(Math.random() * 50);
        b = 13 + (int)(Math.random() * 50);

        for (int i = 0; i < oldCap; ++i) {
            if (tableCopy[i] != null) {
                put(tableCopy[i].getBucKey(), tableCopy[i].getValue());
            }
        }
    }
}
