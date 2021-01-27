package ru.netology;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class App
{
    public static List<Employee> parseCSV(String[] mapping, String fileName) {
        List<Employee> employees = new ArrayList<>();
        try (CSVReader reader = new CSVReader(new FileReader(fileName))) {
            ColumnPositionMappingStrategy<Employee> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping(mapping);

            CsvToBean<Employee> ctb = new CsvToBeanBuilder<Employee>(reader)
                    .withMappingStrategy(strategy)
                    .build();

            employees = ctb.parse();
        } catch (IOException e) {
            System.err.println(e);
        }

        return employees;
    }

    public static List<Employee> parseXML(String fileName) {
        List<Employee> employees = new ArrayList<>();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try  {
            Document document = factory.newDocumentBuilder().parse(new File(fileName));
            NodeList list = document.getDocumentElement().getElementsByTagName("employee");

            for (int i = 0; i < list.getLength(); i++) {
                NodeList infoList = list.item(i).getChildNodes();
                List<String> info = new ArrayList<>();
                for (int j = 0; j < infoList.getLength(); j++) {
                    if (infoList.item(j).getNodeType() == Node.ELEMENT_NODE) {
                        info.add(infoList.item(j).getTextContent());
                    }
                }
                employees.add(new Employee(
                        Long.parseLong(info.get(0)),
                        info.get(1),
                        info.get(2),
                        info.get(3),
                        Integer.parseInt(info.get(4))
                ));
            }
        } catch (Exception e) {
            System.err.println(e);
        }

        return employees;
    }


    public static void writeJSON(List<Employee> list, String fileName) {
        Gson gson = new GsonBuilder().create();

        try (FileWriter writer = new FileWriter(fileName)) {
            writer.write(gson.toJson(list));
            writer.flush();
        } catch (IOException e) {
            System.err.println(e);
        }
    }

    public static String readJSON(String fileName) {
        try (JsonReader reader = new JsonReader(new FileReader(fileName))) {
            Type type = new TypeToken<List<Employee>>(){}.getType();
            List<Employee> l = new Gson().fromJson(reader, type);
            l.forEach(System.out::println);
        } catch (IOException e) {
            System.err.println(e);
        }
        return "";
    }

    public static void main( String[] args )
    {
        String[] columnMapping = { "id", "firstName", "lastName", "country", "age" };
        String path = "D:\\progs\\java\\netology\\specFiles\\csv-json\\src\\main\\java\\ru\\netology\\";
        // 1
        List<Employee> list = parseCSV(columnMapping, path + "data.csv");
        writeJSON(list, path + "data.json");
        // 2
        List<Employee> xmlList = parseXML(path + "data.xml");
        writeJSON(xmlList, path + "data2.json");
        // 3
        readJSON(path + "data.json");
        readJSON(path + "data2.json");
    }
}
