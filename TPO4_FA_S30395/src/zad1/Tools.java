/**
 *
 *  @author Fus Aleksandra S30395
 *
 */

package zad1;


import org.yaml.snakeyaml.Yaml;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Tools {

    static Options createOptionsFromYaml(String fileName) throws Exception {

        String yaml = Files.readAllLines(Paths.get(fileName))
                .stream()
                .collect(Collectors.joining(System.lineSeparator()));

        Map<String, Object> yamlContent = new Yaml().loadAs(yaml, Map.class);


        String host = (String) yamlContent.get("host");

        int port = (int) yamlContent.get("port");

        boolean concurMode = (boolean) yamlContent.get("concurMode");

        boolean showSendRes = (boolean) yamlContent.get("showSendRes");


        Object clientsMapObj = yamlContent.get("clientsMap");

        if (clientsMapObj instanceof Map) {

            @SuppressWarnings("unchecked")
            Map<String, List<String>> clientsMap = (Map<String, List<String>>) clientsMapObj;

            return new Options(host, port, concurMode, showSendRes, clientsMap);

        } else {
            throw new IllegalArgumentException("ClientsMap must be a Map instance");
        }

    }

}

