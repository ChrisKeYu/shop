package top.chris.shop.exception;

public class OrdersException  extends RuntimeException {

    private static final long serialVersionUID = 4151536699399539293L;

    public OrdersException(String msg){
        super(msg);
    }

}