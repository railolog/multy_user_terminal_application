package core;

import java.util.Comparator;

public class CityCreationDateComparator implements Comparator<City> {
    @Override
    public int compare(City o1, City o2) {
        return o1.getCreationDate().compareTo(o2.getCreationDate());
    }
}
