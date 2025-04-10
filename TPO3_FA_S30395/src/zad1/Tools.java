/**
 *
 *  @author Fus Aleksandra S30395
 *
 */

package zad1;


import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.stream.Collectors;

public class Tools {

    static Options createOptionsFromYaml(String fileName) throws Exception {

        String yaml = Files.readAllLines(Paths.get(fileName))
                .stream()
                .collect(Collectors.joining(System.lineSeparator()));

        return null;
    }

}
