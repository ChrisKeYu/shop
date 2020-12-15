package top.chris.shop.enums;

/**
 * 使用Java的枚举类，把所有常量进行一个对应映射
 */
public enum PayMethodEnum {
    //调用了构造器，创建两个常量：等同于 public static final PayMethodEnum WECHAT = new PayMethodEnum(1,"微信支付")
    WECHAT(1,"微信支付"),ALIPAY(2,"支付宝支付");

    //final只能被赋值一次
    public final Integer type;
    //content是用来具体描述type的，两者是一一对应关系
    public final String content;

    PayMethodEnum(Integer type, String content) {
        this.type = type;
        this.content = content;
    }



}
