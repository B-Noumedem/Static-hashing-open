import java.util.ArrayList;
import java.util.List;

class staticHashing_SingleThreaded<K, V> {
    class HashingStructure<K, V> {
        K key;
        V value;
        String state;

        public HashingStructure(String __state) {
            state = __state;
        }

        public HashingStructure(String __state, K __key, V __value) {
            key = __key;
            value = __value;
            state = __state;
        }
    }

    private int max_size;
    private int current_size = 0;
    private int remaining_spots;
    private ArrayList<HashingStructure<K, V>> myHashingTree;
    private String empty = "EMPTY";
    private String removed = "REMOVED";
    private String actif = "ACTIF";

    public staticHashing_SingleThreaded(int __max_size) {
        max_size = __max_size;
        current_size = 0;
        remaining_spots = __max_size;
        myHashingTree = new ArrayList<>();
        myHashingTree.ensureCapacity(__max_size);
        for (int i = 0; i < max_size; i++) {
            myHashingTree.add(new HashingStructure<>(empty));
        }
    }

    private int hash_code(K key) {
        return Math.abs(key.hashCode());
    }

    private int hashing_function(K key) {
        return hash_code(key) % max_size;
    }

    public void add(K key, V value) {
        if (remaining_spots == 0) {
            System.err.println("Hashing index is full!");
            return;
        }

        int hashing_code = hashing_function(key);
        int shift = 0;
        int spot;
        while (shift < max_size) {
            spot = (hashing_code + shift) % max_size;
            if (myHashingTree.get(spot).state.equals(empty)) {
                myHashingTree.set(spot, new HashingStructure<K,V>(actif, key, value));
                break;
            }
            shift++;
        }
        remaining_spots--;
        current_size++;
    }

    public void add_all(List<K> keys, List<V> values) {
        int size = keys.size(); 
        if (size != values.size()) {
            System.err.println("Keys' size and Values' size do not match!");
            return;
        }

        if (remaining_spots < size) {
            System.err.println("Remaining spots are less then arrays' size!");
            return;
        }

        for (int i = 0; i < size; i++) {
            add(keys.get(i), values.get(i));
        }
    }


    public V search(K key) {
        int hashing_code = hashing_function(key);
        int shift = 0;
        int spot;
        HashingStructure<K, V> element_to_check;
        while (shift < max_size) {
            spot = (hashing_code + shift) % max_size;
            element_to_check = myHashingTree.get(spot);
            if ((element_to_check.state.equals(actif)) && element_to_check.key.equals(key)) {
                return element_to_check.value;
            }
            shift++;
        }
        return null;
    }

    public void remove(K key) {
        int hashing_code = hashing_function(key);
        int shift = 0;
        int spot;
        HashingStructure<K, V> element_to_check;
        while (shift < max_size) {
            spot = (hashing_code + shift) % max_size;
            element_to_check = myHashingTree.get(spot);
            if ((element_to_check.state.equals(actif)) && element_to_check.key.equals(key)) {
                myHashingTree.set(spot, new HashingStructure<K, V>(removed));
                current_size--;
                return;
            }
            shift++;
        }
    }

    int get_current_size() {
        return current_size;
    }

}