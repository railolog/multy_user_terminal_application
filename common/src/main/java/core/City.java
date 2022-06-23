package core;

import exceptions2.CityInteractionException;

import java.io.Serializable;
import java.time.ZonedDateTime;

/**
 * Класс города
 */
public class City implements Comparable<City>, Serializable {
    /**
     * Уникальный id
     */
    private long id; //Значение поля должно быть больше 0, Значение этого поля должно быть уникальным, Значение этого поля должно генерироваться автоматически

    /**
     * Название города
     */
    private String name; //Поле не может быть null, Строка не может быть пустой

    /**
     * Координаты
     */
    private Coordinates coordinates; //Поле не может быть null

    /**
     * Дата создания элемента*
     */
    private ZonedDateTime creationDate; //Поле не может быть null, Значение этого поля должно генерироваться автоматически

    /**
     * Площадь города
     */
    private Double area; //Значение поля должно быть больше 0, Поле не может быть null

    /**
     * Население города
     */
    private Integer population; //Значение поля должно быть больше 0, Поле не может быть null

    /**
     * Высота над уровнем города
     */
    private Long metersAboveSeaLevel;

    /**
     * Поле, показывающее, является ли столицей город
     */
    private boolean capital;

    /**
     * Телефонный код
     */
    private int telephoneCode; //Значение поля должно быть больше 0, Максимальное значение поля: 100000

    /**
     * Тип правления
     */
    private Government government; //Поле может быть null

    /**
     * Правитель города
     */
    private Human governor; //Поле может быть null

    private int creatorID;

    public City(String name,
                Coordinates coordinates,
                Double area,
                Integer population,
                Long metersAboveSeaLevel,
                boolean capital,
                int telephoneCode,
                Government government,
                Human governor){
        creationDate = ZonedDateTime.now();
        setName(name);
        setCoordinates(coordinates);
        setArea(area);
        setPopulation(population);
        setMetersAboveSeaLevel(metersAboveSeaLevel);
        setCapital(capital);
        setTelephoneCode(telephoneCode);
        setGovernor(governor);
        setGovernment(government);
    }

    public Long getId(){
        return id;
    }

    public void setId(Long id){
        this.id = id;
    }

    public void setName(String name){
        if (name == null || name.trim().length() == 0){
            throw new CityInteractionException("Поле name не может быть пустым или null");
        }
        this.name = name;
    }

    public void setCoordinates(Coordinates coords){
        if (coords == null){
            throw new CityInteractionException("Поле Coordinates не может быть null");
        }
        this.coordinates = coords;
    }


    public Coordinates getCoordinates() {
        return coordinates;
    }

    public void setArea(Double area) {
        if (area == null){
            throw new CityInteractionException("Поле area не может быть null");
        }
        if (area <= 0){
            throw new CityInteractionException("Поле area должно быть больше 0");
        }
        this.area = area;
    }

    public void setPopulation(Integer population) {
        if (population == null){
            throw new CityInteractionException("Поле population не может быть null");
        }
        if (population <= 0){
            throw new CityInteractionException("Поле population должно быть больше 0");
        }
        this.population = population;
    }

    public void setMetersAboveSeaLevel(Long metersAboveSeaLevel) {
        this.metersAboveSeaLevel = metersAboveSeaLevel;
    }

    public void setCapital(boolean capital) {
        this.capital = capital;
    }

    public void setTelephoneCode(int telephoneCode) {
        if (telephoneCode > 100000 || telephoneCode <= 0){
            throw new CityInteractionException("Значение поля telephoneCode не из промежутка [1;100000]");
        }
        this.telephoneCode = telephoneCode;
    }

    public int getQuarter(){
        return coordinates.getQuarter();
    }

    public void setGovernment(Government government) {
        this.government = government;
    }

    public void setGovernor(Human governor) {
        this.governor = governor;
    }

    public ZonedDateTime getCreationDate(){
        return creationDate;
    }

    public Long getMetersAboveSeaLevel(){
        return metersAboveSeaLevel;
    }

    public int getCreatorID() {
        return creatorID;
    }

    public void setCreatorID(int creatorID) {
        this.creatorID = creatorID;
    }

    public String getName() {
        return name;
    }

    public Double getArea() {
        return area;
    }

    public Integer getPopulation() {
        return population;
    }

    public boolean isCapital() {
        return capital;
    }

    public int getTelephoneCode() {
        return telephoneCode;
    }

    public Government getGovernment() {
        return government;
    }

    public Human getGovernor() {
        return governor;
    }

    public void setCreationDate(ZonedDateTime creationDate) {
        this.creationDate = creationDate;
    }

    @Override
    public int compareTo(City o) {
        return population.compareTo(o.population);
    }

    @Override
    public String toString(){
        return "City{" +
                "\nid=" + id +
                ", \nname='" + name + "'" +
                ", \ncoordinate=" + coordinates +
                ", \ncreation_date=" + creationDate +
                ", \narea=" + area +
                ", \npopulation=" + population +
                ", \nmeters_above_sea_level=" + metersAboveSeaLevel +
                ", \nis_capital=" + capital +
                ", \ntelephone_code=" + telephoneCode +
                ", \ngovernment=" + government +
                ", \ngovernor=" + governor +
                "\n}";
    }
}
