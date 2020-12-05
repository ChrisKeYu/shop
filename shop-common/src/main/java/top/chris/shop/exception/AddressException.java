package top.chris.shop.exception;

//通过类名划分不同类型的异常处理，处理方式调用父类的方法
public class AddressException extends RuntimeException {

    private static final long serialVersionUID = 6265592779096455491L;

    public AddressException(String msg){
        super(msg);
    }

}
