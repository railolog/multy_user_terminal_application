package server.io;

import core.City;
import core.Coordinates;
import core.Government;
import core.Human;
import interaction.Response;
import interaction.ResponseStatus;

import java.sql.*;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

public class DataBaseHandler {
    private String URL;
    private String userName;
    private String passWord;
    private Connection connection;

    public DataBaseHandler(String URL, String userName, String passWord) throws SQLException {
        this.URL = URL;
        this.userName = userName;
        this.passWord = passWord;

        setConnection();
        initTables();
    }

    private void setConnection() throws SQLException {
        connection = DriverManager.getConnection(URL, userName, passWord);
    }

    private void initTables(){
        String createUsersTable = "CREATE TABLE IF NOT EXISTS users(" +
                "  id serial PRIMARY KEY," +
                "  login VARCHAR(80) UNIQUE not NULL," +
                "  PASSWORD CHAR(32) not NULL" +
                "  )";
        /*String createGovernmentTable = "CREATE TABLE IF NOT EXISTS government(" +
                "  id INTEGER UNIQUE not NULL," +
                "  type varchar(30) UNIQUE not NULL" +
                "  )";*/
        String createCollectionTable = "CREATE TABLE IF NOT EXISTS cities (" +
                "  id serial PRIMARY KEY," +
                "  name VARCHAR(80) not NULL," +
                "  coords point not NULL," +
                "  creation_date TIMESTAMPTZ DEFAULT now() not NULL," +
                "  area real not NULL," +
                "  population INTEGER not NULL," +
                "  meters_above_sea_level BIGINT," +
                "  is_capital BOOLEAN not NULL," +
                "  telephone_code INTEGER not NULL," +
                "  government VARCHAR(30)," +
                "  governor real," +
                "  creator_id INTEGER REFERENCES users(id) on DELETE CASCADE not NULL" +
                "  )";

        try(Statement statement = connection.createStatement()) {
            statement.execute(createUsersTable);
            // statement.execute(createGovernmentTable);
            statement.execute(createCollectionTable);
        } catch (SQLException e) {
            System.err.println("Произошла ошибка при создании таблиц.\n" + e.getMessage());
            System.exit(-1);
        }

        System.out.println("Инициализация таблиц успешно завершена.");
    }

    public int authorize(String login, String password){
        String authorizeTmpl = "SELECT id, login from users where login = ? and password = ?";

        try(PreparedStatement preparedStatement = connection.prepareStatement(authorizeTmpl)) {
            preparedStatement.setString(1, login);
            preparedStatement.setString(2, password);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()){
                return resultSet.getInt("id");
            }
            else {
                return -1;
            }
        }
        catch (SQLException e){
            return -1;
        }
    }

    public Response addUser(String login, String password){
        String addUserTmpl = "INSERT INTO users (login, password) VALUES (?, ?)";

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(addUserTmpl);
            preparedStatement.setString(1, login);
            preparedStatement.setString(2, password);

            preparedStatement.executeUpdate();

            preparedStatement.close();

            return new Response(ResponseStatus.SUCCESS, "Пользователь с логином " + login + " успешно добавлен.");

        } catch (SQLException e) {
            e.printStackTrace();
            return new Response(ResponseStatus.ERROR, "Пользователь с данным логином уже существует.");
        }
    }

    public int addCity(City city){
        String addCityTmpl = "INSERT INTO cities " +
                "(name, coords, area, population, meters_above_sea_level, is_capital, telephone_code, government, governor, creator_id) values " +
                "(?, point(?, ?), ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(addCityTmpl)) {
            preparedStatement.setString(1, city.getName());
            preparedStatement.setFloat(2, city.getCoordinates().getX());
            preparedStatement.setFloat(3, city.getCoordinates().getY());
            preparedStatement.setDouble(4, city.getArea());
            preparedStatement.setInt(5, city.getPopulation());
            preparedStatement.setObject(6, city.getMetersAboveSeaLevel());
            preparedStatement.setBoolean(7, city.isCapital());
            preparedStatement.setInt(8, city.getTelephoneCode());
            preparedStatement.setString(9, city.getGovernment() == null ? null : city.getGovernment().toString());
            preparedStatement.setObject(10, city.getGovernor() == null ? null : city.getGovernor().getHeight());
            preparedStatement.setInt(11, city.getCreatorID());

            return preparedStatement.executeUpdate();
        }
        catch (SQLException | NullPointerException e){
            e.printStackTrace();
            return -1;
        }
    }

    public int updateCity(Long id, City city, int userId){
        String updateTmpl = "UPDATE cities\n" +
                "set name = ?\n" +
                "coords = ?\n" +
                "area = ?\n" +
                "population = ?\n" +
                "meters_above_sea_level = ?\n" +
                "is_capital = ?\n" +
                "telephone_code = ?\n" +
                "government = ?\n" +
                "governor = ?\n" +
                "where id = ? and creator_id = ?";

        try(PreparedStatement preparedStatement = connection.prepareStatement(updateTmpl)) {
            preparedStatement.setString(1, city.getName());
            preparedStatement.setFloat(2, city.getCoordinates().getX());
            preparedStatement.setFloat(3, city.getCoordinates().getY());
            preparedStatement.setDouble(4, city.getArea());
            preparedStatement.setInt(5, city.getPopulation());
            preparedStatement.setObject(6, city.getMetersAboveSeaLevel());
            preparedStatement.setBoolean(7, city.isCapital());
            preparedStatement.setInt(8, city.getTelephoneCode());
            preparedStatement.setString(9, city.getGovernment() == null ? null : city.getGovernment().toString());
            preparedStatement.setObject(10, city.getGovernor() == null ? null : city.getGovernor().getHeight());
            preparedStatement.setLong(11, id);
            preparedStatement.setInt(12, city.getCreatorID());

            return preparedStatement.executeUpdate();
        }
        catch (SQLException e){
            return -1;
        }
    }

    public Long syncCollection(HashSet<Long> collectionIdSet){
        try (Statement st = connection.createStatement()){
            ResultSet resultSet = st.executeQuery("SELECT id FROM cities");
            HashSet<Long> dbIdSet = new HashSet<>();

            while (resultSet.next()){
                dbIdSet.add(resultSet.getLong("id"));
            }

            if (collectionIdSet.size() > dbIdSet.size()){
                for (long id : collectionIdSet) {
                    if (!dbIdSet.contains(id)) {
                        return id;
                    }
                }
            }
            else {
                for (long id: dbIdSet){
                    if (!collectionIdSet.contains(id)){
                        collectionIdSet.add(id);
                        return id;
                    }
                    System.out.println(id);
                }
            }
        }
        catch (SQLException ignored){}

        return null;
    }

    public ArrayList<City> readCollection() throws SQLException {
        Statement st = connection.createStatement();
        ResultSet resultSet = st.executeQuery("SELECT * FROM cities");

        ArrayList<City> resArray = new ArrayList<>();
        while (resultSet.next()){
            String name = resultSet.getString("name");
            Double area = resultSet.getDouble("area");
            Integer population = resultSet.getInt("population");
            Long meters = resultSet.getLong("meters_above_sea_level");
            boolean isCapital = resultSet.getBoolean("is_capital");
            int telCode = resultSet.getInt("telephone_code");
            String gov = resultSet.getString("government");
            Government government;

            if (gov == null) {
                government = null;
            }
            else {
                switch (gov) {
                    case "Клептократия":
                        government = Government.KLEPTOCRACY;
                    case "Ноократия":
                        government = Government.NOOCRACY;
                    case "Олигархия":
                        government = Government.OLIGARCHY;
                        break;
                    default:
                        throw new IllegalStateException("Unexpected value: " + gov);
                }
            }

            Human governor;
            Double height = resultSet.getDouble("governor");
            if (height == 0){
                governor = null;
            }
            else {
                governor = new Human(height);
            }

            Long id = resultSet.getLong("id");
            ZonedDateTime creationDate = resultSet.getObject("creation_date", OffsetDateTime.class).toZonedDateTime();
            int creatorId = resultSet.getInt("creator_id");

            PreparedStatement preparedStatement = connection.prepareStatement("SELECT coords[?] from cities where id = ?");
            preparedStatement.setInt(1, 0);
            preparedStatement.setLong(2, id);
            ResultSet interSet = preparedStatement.executeQuery();
            interSet.next();
            Float x = interSet.getFloat(1);
            preparedStatement.setInt(1, 1);
            preparedStatement.setLong(2, id);
            interSet = preparedStatement.executeQuery();
            interSet.next();
            Float y = interSet.getFloat(1);

            City city = new City(name, new Coordinates(x, y), area, population, meters, isCapital, telCode, government, governor);
            city.setId(id);
            city.setCreatorID(creatorId);
            city.setCreationDate(creationDate);



            resArray.add(city);
        }
        return resArray;
    }

    public int deleteCity(Long id, int userId){
        try(PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM cities WHERE id = ? AND creator_id = ?")) {
            preparedStatement.setLong(1, id);
            preparedStatement.setInt(2, userId);

            return preparedStatement.executeUpdate();
        }
        catch (SQLException e){
            return -1;
        }
    }

    public void exit(){
        try {
            connection.close();
        } catch (SQLException ignored) {}
    }
}
