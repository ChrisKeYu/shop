package top.chris.shop.enums;

public enum Sex {
    MALE(1, "男"), FEMALE(2, "女"), SECRET(0, "保密");

    public final Integer type;
    public final String content;

    Sex(Integer type, String content) {
        this.type = type;
        this.content = content;
    }
}
