import java.io.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by hadar on 07/01/2019.
 * read  a file and return parts of it as lists
 */
public class FileParser {

    private BufferedReader reader;
    private File file;

    /**
     * constructor
     *
     * @param filePath
     */
    public FileParser(String filePath) {
        File file = new File(filePath);
        //BufferedReader reader = null;

        try {
            this.reader = new BufferedReader(new FileReader(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * read the first line of the file
     *
     * @return list of attributes names
     */
    public List<String> getFeatursNames() {
        List<String> featurslist = new ArrayList<String>();

        String firstLine = null;
        try {
            firstLine = this.reader.readLine();
            if (firstLine != null) {
                featurslist = Arrays.asList(firstLine.split("\\t"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return featurslist;
    }

    /**
     * read the examples for the file
     *
     * @return vectors of features values for each examples
     */
    public List<List<String>> getExamplesVectors() {
        List<List<String>> listOLists = new ArrayList<List<String>>();
        try {
            String line = null;
            while ((line = this.reader.readLine()) != null) {
                List<String> singleLineList = Arrays.asList(line.split("\\t"));
                listOLists.add(singleLineList);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return listOLists;
    }
}


