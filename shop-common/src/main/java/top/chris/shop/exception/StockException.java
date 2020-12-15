package top.chris.shop.exception;

public class StockException  extends RuntimeException {

    private static final long serialVersionUID = -5845601163841642913L;

    public StockException(String msg){
        super(msg);
    }

}
