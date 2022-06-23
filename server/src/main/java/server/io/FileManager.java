package server.io;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import core.City;
import server.json.ZonedDateTimeDeserializer;
import server.json.ZonedDateTimeSerializer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Scanner;

public class FileManager {
    private FileOutputStream fileOutputStream;

    /**
     * Сериализует коллекцию и сохраняет
     * @param collection коллекция для сохранения
     * @param path путь до файла
     * @throws IOException
     */
    public void save(ArrayList<City> collection, String path) throws IOException {
        this.fileOutputStream = new FileOutputStream(path, false);

        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(ZonedDateTime.class, new ZonedDateTimeSerializer())
                .create();

        String json = gson.toJson(collection);
        byte[] buffer = json.getBytes(StandardCharsets.UTF_8);

        fileOutputStream.write(buffer);
    }

    /**
     * Загружает и десериализирует коллекцию
     * @param path путь до файла
     * @return готовую коллекцию
     * @throws FileNotFoundException
     */
    public ArrayList<City> load(String path) throws FileNotFoundException {
        Scanner sc = new Scanner(new File(path));
        StringBuilder res = new StringBuilder();

        while (sc.hasNext()){
            res.append(sc.next());
        }

        Type cityListType = new TypeToken<ArrayList<City>>() {
        }.getType();

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(ZonedDateTime.class, new ZonedDateTimeDeserializer())
                .create();

        return gson.fromJson(String.valueOf(res), cityListType);
    }
}
