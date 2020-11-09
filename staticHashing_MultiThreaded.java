import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class staticHashing_MultiThreaded<K, V> extends Thread {

    private int max_size;
    private int current_size = 0;
    private int remaining_spots;
    private ArrayList<HashingStructure<K, V>> myHashingTree; // To fix
    private String empty = "EMPTY";
    private String removed = "REMOVED";
    private String actif = "ACTIF";
    private final String add_array = "add_array";
    private final String remove = "remove";
    private final String search = "search";
    private final String none = "none";
    private boolean is_job_done = false;
    private String task_to_perform;
    private V searched_row = null;

    public staticHashing_MultiThreaded(int __max_size) {
        max_size = __max_size;
        current_size = 0;
        remaining_spots = __max_size;
        myHashingTree = new ArrayList<>();
        myHashingTree.ensureCapacity(__max_size);
        for (int i = 0; i < max_size; i++) {
            myHashingTree.add(new HashingStructure<>(empty));
        }
        is_job_done = false;
        task_to_perform = "None";
        searched_row = null;
    }

    static class HashingStructure<K, V> {
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

    public class myThread extends Thread {
        private List<K> keys;
        private List<V> values;

        public myThread(List<K> __keys, List<V> __values) {
            keys = __keys;
            values = __values;
        }

        private int thread_label;
        private K key_to_search;
        private int nbr_of_threads;

        public myThread(int __thread_label, K __key_to_search, int __nbr_of_threads) {
            thread_label = __thread_label;
            key_to_search = __key_to_search;
            nbr_of_threads = __nbr_of_threads;
        }

        private K key_to_remove;

        public myThread(int __thread_label, int __nbr_of_threads, K __key_to_remove) {
            thread_label = __thread_label;
            key_to_remove = __key_to_remove;
            nbr_of_threads = __nbr_of_threads;
        }

        int shift;
        int hashing_code;
        int spot;

        private void add_array() {
            int size = keys.size();
            int cpt = 0;
            while (cpt < size) {
                add(keys.get(cpt), values.get(cpt));
                cpt++;
            }
        }

        private void search() {
            hashing_code = hashing_function(key_to_search);
            shift = thread_label;
            HashingStructure<K, V> element_to_check;
            while (shift < max_size && !is_job_done) {
                spot = (hashing_code + shift) % max_size;
                element_to_check = myHashingTree.get(spot);
                if ((element_to_check.state.equals(actif)) && element_to_check.key.equals(key_to_search)) {
                    is_job_done = true;
                    searched_row = element_to_check.value;
                }
                shift += nbr_of_threads;
            }
        }

        private void remove() {
            hashing_code = hashing_function(key_to_remove);
            shift = 0;
            HashingStructure<K, V> element_to_check;
            while (shift < max_size && !is_job_done) {
                spot = (hashing_code + shift) % max_size;
                element_to_check = myHashingTree.get(spot);
                if ((element_to_check.state.equals(actif)) && element_to_check.key.equals(key_to_remove)) {
                    is_job_done = true;
                    myHashingTree.set(spot, new HashingStructure<K, V>(removed));
                    current_size--;
                    return;
                }
                shift += nbr_of_threads;
            }
        }

        @Override
        public void run() {
            switch (task_to_perform) {
                case add_array:
                    add_array();
                    break;

                case remove:
                    remove();
                    break;

                case search:
                    search();
                    break;

                default:
                    break;
            }
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
                myHashingTree.set(spot, new HashingStructure<K, V>(actif, key, value));
                break;
            }
            shift++;
        }
        remaining_spots--;
        current_size++;
    }

    public void add_all(List<K> keys, List<V> values, int nbr_of_threads) {
        int size = keys.size();
        if (size != values.size()) {
            System.err.println("Keys' size and Values' size do not match!");
            return;
        }

        if (remaining_spots < size) {
            System.err.println("Remaining spots are less then arrays' size!");
            return;
        }

        task_to_perform = add_array;
        ArrayList<myThread> threads = new ArrayList<>(nbr_of_threads);

        for (int idx = 0; idx < nbr_of_threads; idx++) {
            int min_index = idx * size / nbr_of_threads;
            int max_index = (idx == (nbr_of_threads - 1)) ? (size) : (idx + 1) * size / nbr_of_threads;
            threads.add(new myThread(keys.subList(min_index, max_index), values.subList(min_index, max_index)));
            threads.get(idx).start();
        }
        Iterator<myThread> iter = threads.iterator();
        while (iter.hasNext()) {
            try {
                iter.next().join();
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }

    public V search(K key, int nbr_of_threads) {
        searched_row = null;
        is_job_done = false;
        task_to_perform = search;
        ArrayList<myThread> threads = new ArrayList<>(nbr_of_threads);
        for (int i = 0; i < nbr_of_threads; i++) {
			if (is_job_done) {
				break;
			}
            threads.add(new myThread(i, key, nbr_of_threads));
            threads.get(i).start();
        }
        Iterator<myThread> iter = threads.iterator();
        while (iter.hasNext()) {
            try {
                iter.next().join();
            } catch (Exception e) {
                System.out.println(e);
            }
        }
        return searched_row;
    }

    public void remove(K key, int nbr_of_threads) {
        is_job_done = false;
        task_to_perform = remove;
        ArrayList<myThread> threads = new ArrayList<>(nbr_of_threads);
        for (int i = 0; i < nbr_of_threads; i++) {
            if (is_job_done) {
				break;
			}
            threads.add(new myThread(i, nbr_of_threads, key));
            threads.get(i).start();
        }
        Iterator<myThread> iter = threads.iterator();
        while (iter.hasNext()) {
            try {
                iter.next().join();
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }

    int get_current_size() {
        return current_size;
    }
}
