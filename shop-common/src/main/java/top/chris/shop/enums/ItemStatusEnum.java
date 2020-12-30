package top.chris.shop.enums;

public enum ItemStatusEnum {
    ON(1, "上架"), OFF(2, "下架");

    public final Integer type;
    public final String content;

    ItemStatusEnum(Integer type, String content) {
        this.type = type;
        this.content = content;
    }
}
