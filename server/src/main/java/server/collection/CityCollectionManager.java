package server.collection;

import core.City;
import exceptions2.CityInteractionException;
import exceptions2.CityNotExistsException;
import interaction.Response;
import interaction.ResponseStatus;
import server.io.ClientComManager;
import server.io.ConsoleManager;
import server.io.DataBaseHandler;
import server.io.FileManager;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

public class CityCollectionManager{
    /** Дата создания */
    private Date creationDate;

    /**
     * Коллекция, которой управляет класс
     */
    private ArrayList<City> cityCollection = new ArrayList<>();

    /**
     * id хранимых элементов
     */
    private HashSet<Long> collectionIdSet = new HashSet<>();

    /**
     * Класс, осуществляющий сохранение и загрузку коллекции
     */
    private FileManager fileManager = new FileManager();

    private final DataBaseHandler dataBaseHandler;

    String collectionManagerPath = "saves.json";
    private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    public CityCollectionManager(DataBaseHandler dataBaseHandler){
        creationDate = new Date();
        this.dataBaseHandler = dataBaseHandler;
    }

    public void setCollection(ArrayList<City> collection) {
        lock.writeLock().lock();

        for (City city : collection) {
            cityCollection.add(city);
            collectionIdSet.add(city.getId());
        }

        lock.writeLock().unlock();
    }

    public int getIndexById(long id) throws CityNotExistsException {
        lock.readLock().lock();

        for (int i = 0; i < cityCollection.size(); i++){
            if (id == cityCollection.get(i).getId()){
                lock.readLock().unlock();
                return i;
            }
        }
        lock.readLock().unlock();

        throw new CityNotExistsException("В коллекции нет элемента с id " + id);
    }

    public Response showCollectionInfo() {
        return new Response(ResponseStatus.SUCCESS, "ArrayList of Cities\nCreation Date: " + creationDate + "\n" +
                                                    "Number of elements: " + cityCollection.size());
    }

    public Response addElement(ClientComManager clientComManager, int userID) throws IOException, ClassNotFoundException {
        City city;
        try {
            city = clientComManager.readCity();
        }
        catch (CityInteractionException e){
            return new Response(ResponseStatus.ERROR, "не удалось добавить элемент. " + e.getMessage());
        }

        Response response;
        lock.writeLock().lock();
        city.setCreatorID(userID);
        int row = dataBaseHandler.addCity(city);

        try {
            if (row > 0) {
                Long id = dataBaseHandler.syncCollection(collectionIdSet);
                if (id != null) {
                    city.setId(id);
                    cityCollection.add(city);
                    sort();
                    response = new Response(ResponseStatus.SUCCESS, "Элемент успешно добавлен");
                } else {
                    response = new Response(ResponseStatus.ERROR, "Произошла непредвиденная ошибка при добавлении элемента");
                }
            } else {
                response = new Response(ResponseStatus.ERROR, "Произошла непредвиденная ошибка при добавлении элемента");
            }
        }
        finally {
            lock.writeLock().unlock();
        }
        return response;
    }

    public Response update(long id, ClientComManager clientComManager, int userID) throws IOException, ClassNotFoundException {
        City newCity;

        try{
            newCity = clientComManager.readCity();
        }
        catch (CityInteractionException e){
            return new Response(ResponseStatus.ERROR, "не удалось считать элемент. " + e.getMessage());
        }

        lock.writeLock().lock();
        int res = dataBaseHandler.updateCity(id, newCity, userID);

        Response response;
        try {
            if (res == 1) {
                newCity.setId(id);
                newCity.setCreatorID(userID);
                cityCollection.set(getIndexById(id), newCity);
                response = new Response(ResponseStatus.SUCCESS, "Элемент успешно обновлён");
            } else if (res == 0) {
                response = new Response(ResponseStatus.ERROR, "Элемента с id " + id + " не существует, либо у вас недостаточно прав");
            } else {
                response = new Response(ResponseStatus.ERROR, "Произошла ошибка при взаимодействии с БД");
            }
        }
        finally {
            lock.writeLock().unlock();
        }

        return response;
    }

    public Response update(String id, ClientComManager clientComManager, int userID) throws IOException, ClassNotFoundException {
        try {
            return update(Long.parseLong(id), clientComManager, userID);
        }
        catch (NumberFormatException e){
            return new Response(ResponseStatus.ERROR, "введён id не являющийся целым числом");
        }
    }

    public void sort() {
        lock.writeLock().lock();
        Collections.sort(cityCollection);
        lock.writeLock().unlock();
    }

    public Response printElements() {
        lock.writeLock().lock();

        Response response;
        try{
            if (cityCollection.isEmpty()) {
                response = new Response(ResponseStatus.SUCCESS, "Коллекция пуста");
            } else {
                response = new Response(ResponseStatus.SUCCESS,
                        cityCollection.stream().map(City::toString).collect(Collectors.joining("\n")));
            }
        }
        finally {
            lock.writeLock().unlock();
        }

        return response;
    }

    /**
     * Удаляет элемент коллекции
     * @param id id удаляемого элемента
     */
    public Response removeById(long id, int userId){
        lock.writeLock().lock();

        Response response;
        try {
            int res = dataBaseHandler.deleteCity(id, userId);

            if (res == 0) {
                response = new Response(ResponseStatus.ERROR, "Элемента с заданным id не существует, либо у вас недостаточно прав");
            } else if (res == -1) {
                response = new Response(ResponseStatus.ERROR, "Произошла непредвиденная ошибка при работе с БД");
            } else {
                int index = getIndexById(id);
                cityCollection.remove(index);
                response = new Response(ResponseStatus.SUCCESS, "Элемент с id " + id + " успешно удален");
            }
        }
        finally {
            lock.writeLock().unlock();
        }

        return response;
    }

    public Response removeById(String id, int userId) {
        try {
            return removeById(Long.parseLong(id), userId);
        }
        catch (NumberFormatException e){
            return new Response(ResponseStatus.ERROR, "введён id не являющийся целым числом");
        }
    }

    public Response clearCollection(int userId) {
        lock.writeLock().lock();
        try {
            for (City city : cityCollection) {
                if (city.getCreatorID() == userId) {
                    int res = dataBaseHandler.deleteCity(city.getId(), userId);
                    if (res == 1){
                        cityCollection.remove(getIndexById(city.getId()));
                    }
                }
            }
        }
        finally {
            lock.writeLock().unlock();
        }
        return new Response(ResponseStatus.SUCCESS, "Все элементы, принадлежащие вам, успешно удалены");
    }

    public Response reorder() {
        lock.writeLock().lock();
        try {
            Collections.reverse(cityCollection);
            return new Response(ResponseStatus.SUCCESS, "Коллекция отсортирована в порядке, обратном нынешнему");
        }
        finally {
            lock.writeLock().unlock();
        }
    }

    public Response shuffle() {
        lock.writeLock().lock();
        Collections.shuffle(cityCollection);
        lock.writeLock().unlock();
        return new Response(ResponseStatus.SUCCESS, "Элементы коллекции перемешаны в случайном порядке");
    }

    public Response addIfMax(ClientComManager clientComManager, int userID) throws IOException, ClassNotFoundException {
        lock.writeLock().lock();
        try {
            if (cityCollection.size() == 0) {
                return addElement(clientComManager, userID);
            } else {
                City maxCity = cityCollection.stream().max(City::compareTo).get();
                City city;
                try {
                    city = clientComManager.readCity();
                } catch (CityInteractionException e) {
                    return new Response(ResponseStatus.ERROR,
                            "не удалось считать элемент. " + e.getMessage());
                }

                if (city.compareTo(maxCity) > 0) {
                    return addElement(clientComManager, userID);
                } else {
                    return new Response(ResponseStatus.SUCCESS,
                            "Элемент не больше максимального элемента коллекции");
                }
            }
        }
        finally {
            lock.writeLock().unlock();
        }
    }

    public Response minByCreationDate() {
        lock.readLock().lock();
        try {
            if (cityCollection.size() == 0) {
                return new Response(ResponseStatus.SUCCESS, "Коллекция пуста");
            } else {
                return new Response(ResponseStatus.SUCCESS, cityCollection.stream().min(City::compareTo).toString());
            }
        }
        finally {
            lock.readLock().unlock();
        }
    }

    public Response filterGreaterThanSeaLevel(String metersAboveSeaLevel) {
        lock.readLock().lock();
        try {
            return filterGreaterThanSeaLevel(Long.parseLong(metersAboveSeaLevel));
        }
        catch (NumberFormatException e){
            return new Response(ResponseStatus.ERROR, "Введено не целое число");
        }
        finally {
            lock.readLock().unlock();
        }
    }

    public Response filterGreaterThanSeaLevel(long metersAboveSeaLevel) {
        if (cityCollection.size() == 0){
            return new Response(ResponseStatus.SUCCESS, "Коллекция пуста");
        }
        else {
            try {
                return new Response(ResponseStatus.SUCCESS, cityCollection.stream()
                        .filter((a) -> a.getMetersAboveSeaLevel() != null)
                        .filter((a) -> a.getMetersAboveSeaLevel() > metersAboveSeaLevel)
                        .map(City::toString)
                        .collect(Collectors.joining("\n")));
            }
            catch (NullPointerException e) {
                e.printStackTrace();
                return new Response(ResponseStatus.SUCCESS,
                        "В коллекции нет элементов со значением поля metersAboveSeaLevel больше заданного");
            }
        }
    }

    public Response groupCountingByCoordinates() {
        lock.readLock().lock();
        try {
            if (cityCollection.size() == 0) {
                return new Response(ResponseStatus.SUCCESS, "Коллекция пуста");
            } else {
                Map<Integer, Long> groups = cityCollection.stream().collect(Collectors.groupingBy(City::getQuarter, Collectors.counting()));

                StringBuilder res = new StringBuilder();
                res.append("Распределение городов по четвертям:\n");

                for (Map.Entry<Integer, Long> entry : groups.entrySet()) {
                    res.append(entry.getKey().toString()).append(": ").append(entry.getValue()).append("\n");
                }

                return new Response(ResponseStatus.SUCCESS, res.toString());
            }
        }
        finally {
            lock.readLock().unlock();
        }
    }

    public void save(String path) {
        dataBaseHandler.exit();
    }
}
