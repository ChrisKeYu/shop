package top.chris.shop.enums;

public enum OrdersStatusEnum {

    WAIT_PAY(10,"待支付"),PAID(20,"已支付"),DELIVERED(30,"已发货"),
    SUCCESS(40,"交易成功"),CLOSED(50,"交易关闭");

    public final Integer type;
    public final String content;

    OrdersStatusEnum(Integer type, String content) {
        this.type = type;
        this.content = content;
    }
}
