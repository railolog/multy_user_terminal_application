package exceptions2;

public class CityNotExistsException extends CityInteractionException{
    public CityNotExistsException(String msg){
        super(msg);
    }
}
