import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class person {
    int ID;
    String FirstName;
    String LastName;
    int Age;
    String Gender;

    public person(String[] params) {
        if (params.length != 5) {
            System.err.println("Chwaya les params:" + Arrays.toString(params) + "\n\tLen=" + params.length);
        }
        ID = Integer.parseInt(params[0]);
        FirstName = params[1];
        LastName = params[2];
        Age = Byte.parseByte(params[3]);
        Gender = params[4];
    }

    public person(int __ID, String __FirstName, String __LastName, int __Age, String __Gender) {
        ID = __ID;
        FirstName = __FirstName;
        LastName = __LastName;
        Age = __Age;
        Gender = __Gender;
    }

    public void showData() {
        System.out.println("\n\t ID= " + ID + "\n\t FirstName= " + FirstName + "\n\t LastName= " + LastName + "\n\t Age= "
                + Age + "\n\t Gender= " + Gender);
    }

    public static ArrayList<person> readFromCSV(String filename, boolean Headers, String cvsSplitBy) {
        ArrayList<person> list = new ArrayList<>();
        try {
            File myFile = new File(filename);
            Scanner myReader = new Scanner(myFile);
            if (Headers) {
                String __ = myReader.nextLine();
            }
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                if (data.length() == 0) {
                    continue;
                }
                String[] dataSplitted = data.split(cvsSplitBy);
                list.add(new person(dataSplitted));
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        return (list);
    }
}