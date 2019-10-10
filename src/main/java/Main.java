import dto.Line;
import dto.Metro;
import dto.Station;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.util.*;

public class Main {

    public static void main(String[] args) throws IOException, ParseException {
        Metro metro = createMetro();
        String jsonObject = createJson(metro);
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
//        String path = reader.readLine();
        String path = "data/metro.json";
        saveJSON(jsonObject, path);
        printStationsForLines(path);
    }

    private static void printStationsForLines(String path) throws IOException, ParseException {
        JSONParser parser = new JSONParser();
        JSONObject jsonObject = (JSONObject) parser.parse(new FileReader(path));
        Map<String, List<String>> mapStation = (Map<String, List<String>>) jsonObject.get("stations");
        List<Map<String, String>> lines = (List<Map<String, String>>) jsonObject.get("lines");
        for (Map.Entry<String, List<String>> pair : mapStation.entrySet()) {
            System.out.println("Линия: " + getLineName(pair.getKey(), lines) + ", количество станций: " + pair.getValue().size());
        }


    }

    private static String getLineName(String numberLine, List<Map<String, String>> lines) {
        for (Map<String, String> line : lines) {
            if (line.get("number").equals(numberLine)) {
                return line.get("name");
            }
        }
        return "NO NAME";
    }

    private static void saveJSON(String jsonObject, String path) {
        try(FileWriter writer = new FileWriter(path)) {
            writer.write(jsonObject);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String createJson(Metro metro) {
        JSONObject jsonObject = new JSONObject();

        jsonObject.put("stations", createMapStations(metro.getStations()));
        jsonObject.put("lines", createListLines(metro.getLines()));

        return jsonObject.toJSONString();
    }

    private static List<JSONObject> createListLines(List<Line> lines) {
        List<JSONObject> list = new ArrayList<JSONObject>();
        for(Line line : lines) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("number", line.getNumber());
            jsonObject.put("name", line.getName());
            list.add(jsonObject);
        }
        return list;
    }

    private static Map<String, List<String>> createMapStations(List<Station> stations) {
        Map<String, List<String>> map = new TreeMap<String, List<String>>();
        for (Station station : stations) {
            if (map.containsKey(station.getNumberLine())) {
                List<String> namesStations = map.get(station.getNumberLine());
                namesStations.add(station.getName());
                map.put(station.getNumberLine(), namesStations);
            } else {
                List<String> namesStations = new ArrayList<String>();
                namesStations.add(station.getName());
                map.put(station.getNumberLine(), namesStations);
            }
        }
        return map;
    }

    private static Metro createMetro() throws IOException {
        Document document = Jsoup.connect("https://ru.wikipedia.org/wiki/Список_станций_Московского_метрополитена#Станции_Московского_метрополитена").maxBodySize(0).get();
        Metro metro = new Metro();
        metro.setStations(getStations(document));
        metro.setLines(getLines(document));
        return metro;
    }

    private static List<Station> getStations(Document document) {
        List<Station> stations = new ArrayList<Station>();
        Elements table = document.getElementsByClass("standard sortable");
        Elements rows = table.select("tr");
        for (int i = 1; i < rows.size(); i++) {
            Element row = rows.get(i);
            Elements cols = row.select("td");
            if (cols.size() == 0) {
                continue;
            }
            Station station = new Station();
            String numberLine = cols.get(0).text().split(" ")[0];
            numberLine = numberLine.length() <= 2
                    ? numberLine
                    : numberLine.substring(0,numberLine.length() - 2);
            station.setNumberLine(numberLine);
            station.setName(cols.get(1).text());
            station.setTransfers(cols.get(3).text().split(" "));
            stations.add(station);
        }
        return stations;
    }

    private static List<Line> getLines(Document document) {
        List<Line> lines = new ArrayList<Line>();
        Elements elements = document.getElementsByClass("navbox");
        for (Element line : elements.get(0).select("dd")) {
            Line itemLine = new Line();
            itemLine.setName(line.text().split(" ")[1]);
            itemLine.setNumber(line.text().split(" ")[0]);
            lines.add(itemLine);
        }
        return lines;
    }
}
