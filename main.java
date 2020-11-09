import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import java.io.*;

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

    public static void eval_single_thread() throws IOException {
        int max_size = 670000;
        String data_path = "911.csv";
        String csvsplitby = ",";
        staticHashing_SingleThreaded<String, rowdata> staticHashing_ST = new staticHashing_SingleThreaded<>(max_size);
        ArrayList<rowdata> data = rowdata.readFromCSV(data_path, true, csvsplitby);
        ArrayList<String> keys = new ArrayList<String>();
        Iterator<rowdata> iter = data.iterator();

        while (iter.hasNext()) {
            keys.add(iter.next().ID);
        }
        int size = keys.size();
        ArrayList<Long> insert_times = new ArrayList<>();

        System.out.println("Single thread adding time computation");
        // Insert sequentially all data in hashtable
        for (int i = 0; i < size; i++) {
            long start = System.nanoTime();
            staticHashing_ST.add(keys.get(i), data.get(i));
            long finish = System.nanoTime();
            insert_times.add(finish - start);
        }

        PrintWriter writer = new PrintWriter(new FileWriter("Seq_Insertions_times_ST.txt"));
        writer.println(insert_times.toString());
        writer.close();

        System.out.println("Single thread adding (bloc) and searching time computation");
        // Insert data in hashtable in bloc
        staticHashing_SingleThreaded<String, rowdata> staticHashing_ST2 = new staticHashing_SingleThreaded<>(size);
        ArrayList<Long> insert_bloc_times = new ArrayList<>();
        ArrayList<Long> search_bloc_times = new ArrayList<>();
        ArrayList<Long> delete_bloc_times = new ArrayList<>();

        int min_index = 0;
        int max_index = 0;
        int nb_fold = 12;
        int step_index = 40000;

        System.out.println("Single thread delete/search time computation");

        int nb_search = 1000;
        int nb_delete = 1000;

        System.out.println("Multi thread adding (bloc), search and deletion time computation");

        for (int i = 0; i < nb_fold; i++) {
            /*
             * if (i==(nb_fold-1)) { max_index = size; } else { max_index = min_index +
             * step_index; }
             */
            max_index = min_index + step_index;
            List<String> subkeys = keys.subList(min_index, max_index);
            List<rowdata> subvalues = data.subList(min_index, max_index);
            long start = System.nanoTime();
            staticHashing_ST2.add_all(subkeys, subvalues);
            long finish = System.nanoTime();
            insert_bloc_times.add(finish - start);
            min_index = max_index;
        }

        PrintWriter writer2 = new PrintWriter(new FileWriter("bloc_Insertions_times_ST.txt"));
        writer2.println(insert_bloc_times.toString());
        writer2.close();

        for (int i = 0; i < nb_fold; i++) {
            System.out.println("Computations running for fold number = " + (i + 1) + "/" + nb_fold);
            // delete time computation here ... delete 1000 times different values
            for (int k = 0; k < nb_delete; k++) {
                int delete_index = ThreadLocalRandom.current().nextInt(0, size - 1);
                String delete_key = keys.get(delete_index);
                long start3 = System.nanoTime();
                staticHashing_ST2.remove(delete_key);
                long finish3 = System.nanoTime();
                delete_bloc_times.add(finish3 - start3);
            }

            // Search time computation here ... search 1000 times different values
            for (int k = 0; k < nb_search; k++) {
                int search_index = ThreadLocalRandom.current().nextInt(0, size - 1);
                String search_key = keys.get(search_index);
                long start2 = System.nanoTime();
                staticHashing_ST2.search(search_key);
                long finish2 = System.nanoTime();
                search_bloc_times.add(finish2 - start2);
            }
        }

        PrintWriter writer3 = new PrintWriter(new FileWriter("bloc_Search_times_ST.txt"));
        writer3.println(search_bloc_times.toString());
        writer3.close();

        PrintWriter writer4 = new PrintWriter(new FileWriter("bloc_delete_times_ST.txt"));
        writer4.println(search_bloc_times.toString());
        writer4.close();
    }

    public static void eval_multi_thread(int nbr_of_threads) {

        int max_size = 670000;
        String data_path = "911.csv";
        String csvsplitby = ",";
        staticHashing_MultiThreaded<String, rowdata> staticHashing_MT = new staticHashing_MultiThreaded<>(max_size);
        ArrayList<rowdata> data = rowdata.readFromCSV(data_path, true, csvsplitby);
        ArrayList<String> keys = new ArrayList<>();
        Iterator<rowdata> iter = data.iterator();
        while (iter.hasNext()) {
            keys.add(iter.next().ID);
        }
        int size = keys.size();
        ArrayList<Long> insert_times = new ArrayList<>();

        System.out.println("Multi thread adding time computation");

        // Insert sequentially all data in hashtable
        for (int i = 0; i < size; i++) {
            long start = System.nanoTime();
            staticHashing_MT.add(keys.get(i), data.get(i));
            long finish = System.nanoTime();
            insert_times.add(finish - start);
        }

        PrintWriter writer = new PrintWriter(new FileWriter("Seq_Insertions_times_MT.txt"));
        writer.println(insert_times.toString());
        writer.close();

        System.out.println("Multi thread adding (bloc), search and deletion time computation");
        // Insert data in hashtable in bloc
        staticHashing_MultiThreaded<String, rowdata> staticHashing_MT2 = new staticHashing_MultiThreaded<>(max_size);
        ArrayList<Long> insert_bloc_times = new ArrayList<>();
        ArrayList<Long> search_bloc_times = new ArrayList<>();
        ArrayList<Long> delete_bloc_times = new ArrayList<>();

        int min_index = 0;
        int max_index = 0;
        int nb_fold = 12;
        int step_index = 40000;
        int nb_search = 1000;

        for (int i = 0; i < nb_fold; i++) {
            /*
             * if (i==nb_fold-1) { max_index = size; } else { max_index = min_index +
             * step_index; }
             */
            max_index = min_index + step_index;
            List<String> subkeys = keys.subList(min_index, max_index);
            List<rowdata> subvalues = data.subList(min_index, max_index);
            long start = System.nanoTime();
            staticHashing_MT2.add_all(subkeys, subvalues, nbr_of_threads);
            long finish = System.nanoTime();
            insert_bloc_times.add(finish - start);
            min_index = max_index;
        }

        PrintWriter writer2 = new PrintWriter(new FileWriter("bloc_Insertions_times_MT.txt"));
        writer2.println(insert_bloc_times.toString());
        writer2.close();

        // Search time computation here ... search 1000 times different values
        for (int i = 0; i < nb_fold; i++) {
            System.out.println("Computations running for fold number = " + (i + 1) + "/" + nb_fold);
            for (int k = 0; k < nb_search; k++) {
                int delete_index = ThreadLocalRandom.current().nextInt(0, size - 1);
                String delete_key = keys.get(delete_index);
                long start3 = System.nanoTime();
                staticHashing_MT2.remove(delete_key, nbr_of_threads);
                long finish3 = System.nanoTime();
                delete_bloc_times.add(finish3 - start3);
            }

            for (int k = 0; k < nb_search; k++) {
                int search_index = ThreadLocalRandom.current().nextInt(0, size - 1);
                String search_key = keys.get(search_index);
                long start2 = System.nanoTime();
                staticHashing_MT2.search(search_key, nbr_of_threads);
                long finish2 = System.nanoTime();
                search_bloc_times.add(finish2 - start2);
            }
        }

        PrintWriter writer3 = new PrintWriter(new FileWriter("bloc_Search_times_MT.txt"));
        writer3.println(search_bloc_times.toString());
        writer3.close();

        PrintWriter writer4 = new PrintWriter(new FileWriter("bloc_delete_times_MT.txt"));
        writer4.println(delete_bloc_times.toString());
        writer4.close();
    }

    public static long eval_nbr_of_threads_oneIter(int nbr_of_threads) throws IOException {

        int max_size = 670000;
        String data_path = "911.csv";
        String csvsplitby = ",";
        staticHashing_MultiThreaded<String, rowdata> staticHashing_MT = new staticHashing_MultiThreaded<>(max_size);
        ArrayList<rowdata> data = rowdata.readFromCSV(data_path, true, csvsplitby);
        ArrayList<String> keys = new ArrayList<>();
        Iterator<rowdata> iter = data.iterator();
        while (iter.hasNext()) {
            keys.add(iter.next().ID);
        }
        int size = keys.size();

        // Insert data in hashtable in bloc

        long start = System.nanoTime();
        staticHashing_MT.add_all(keys, data, nbr_of_threads);
        long finish = System.nanoTime();
        return (finish - start);
    }

    public static void eval_nbr_of_threads(int min_nbr_threads, int max_nbr_threads) {
        ArrayList<Long> time_spent = new ArrayList<>(12);
        ArrayList<Long> aux = new ArrayList<>(10);
        for (int i = min_nbr_threads; i <= max_nbr_threads; i++) {
            System.out.println("Running with number of threads = " + i);
            aux.clear();
            for (int j = 0; j < 10; j++) {
                System.out.println("\tIter = " + j);
                aux.add(eval_nbr_of_threads_oneIter(i));
            }
            Long avg = 0L;
            for (int j = 0; j < 10; j++) {
                avg += aux.get(j);
            }
            time_spent.add(avg / 10);
        }

        PrintWriter writer = new PrintWriter(new FileWriter("bloc_Search_times_MT.txt"));
        writer.println(time_spent.toString());
        writer.close();
    }

    public static void main(String[] args) throws IOException {
        int nbr_of_threads = 4;
        int min_nbr_threads =1;
        int max_nbr_threads = 12;
        run_single_thread();
        run_multi_thread();
        eval_single_thread();
        eval_multi_thread(nbr_of_threads);
        eval_nbr_of_threads(min_nbr_threads, max_nbr_threads);
}
