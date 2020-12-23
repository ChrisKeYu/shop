package top.chris.shop.exception;

public class UserException extends RuntimeException {

    private static final long serialVersionUID = 6265592779096455491L;

    public UserException(String msg){
        super(msg);
    }

}