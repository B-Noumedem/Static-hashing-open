import java.util.ArrayList;
import java.util.Iterator;

public class main {

    public static void run_single_thread() {
        int max_size = 20;
        staticHashing_SingleThreaded<Integer, person> staticHashing_MT = new staticHashing_SingleThreaded<>(max_size);
        ArrayList<person> persons = person.readFromCSV("test.csv", true, ",");
        ArrayList<Integer> keys = new ArrayList<>();
        Iterator<person> iter = persons.iterator();
        while (iter.hasNext()) {
            keys.add(iter.next().ID);
        }

        staticHashing_MT.add_all(keys, persons);
        System.out.println("Current_size at now = " + staticHashing_MT.get_current_size());

        int search_key = 1;
        if (staticHashing_MT.search(search_key) != null) {
            staticHashing_MT.search(search_key).showData();
        } else {
            System.out.println("Searched key do not exist!");
        }
        System.out.println("Current_size at now = " + staticHashing_MT.get_current_size());

        int remove_key = 1;
        staticHashing_MT.remove(remove_key);
        System.out.println("Current_size at now = " + staticHashing_MT.get_current_size());

        search_key = 1;
        if (staticHashing_MT.search(search_key) != null) {
            staticHashing_MT.search(search_key).showData();
        } else {
            System.out.println("Searched key do not exist!");
        }
    }

    public static void run_multi_thread() {
        int max_size = 20;
        int nbr_of_threads = 3;
        staticHashing_MultiThreaded<Integer, person> staticHashing_MT = new staticHashing_MultiThreaded<Integer, person>(
                max_size);
        ArrayList<person> persons = person.readFromCSV("test.csv", true, ",");
        ArrayList<Integer> keys = new ArrayList<>();
        Iterator<person> iter = persons.iterator();
        while (iter.hasNext()) {
            keys.add(iter.next().ID);
        }

        System.out.println("\tRunning add_all function.");
        staticHashing_MT.add_all(keys, persons, nbr_of_threads);
        System.out.println("Current_size at now = " + staticHashing_MT.get_current_size());
        System.out.println("\tadd_all function runned successfully.");

        int search_key = 1;
        System.out.println("\n\tRunning search function.");
        if (staticHashing_MT.search(search_key, nbr_of_threads) != null) {
            staticHashing_MT.search(search_key, nbr_of_threads).showData();
        } else {
            System.out.println("Searched key do not exist!");
        }
        System.out.println("\tsearch function runned successfully.");
        System.out.println("Current_size at now = " + staticHashing_MT.get_current_size());

        int remove_key = 1;
        System.out.println("\n\tRunning remove function.");
        staticHashing_MT.remove(remove_key, nbr_of_threads);
        System.out.println("Current_size at now = " + staticHashing_MT.get_current_size());
        System.out.println("\tremove function runned successfully.");

        search_key = 1;
        if (staticHashing_MT.search(search_key, nbr_of_threads) != null) {
            staticHashing_MT.search(search_key, nbr_of_threads).showData();
        } else {
            System.out.println("Searched key do not exist!");
        }
    }

    public static void main(String[] args) {
        // run_single_thread();
        run_multi_thread();

    }
}
