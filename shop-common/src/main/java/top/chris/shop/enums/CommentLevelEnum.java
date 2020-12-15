package top.chris.shop.enums;

/**
 * 构建一个枚举类型，用数字来代替对应的中文。
 */
public enum CommentLevelEnum {
    GOOD(1, "好评"), NORMAL(2, "中评"), BAD(3, "差评");

    public final Integer type;
    public final String content;

    CommentLevelEnum(Integer type, String content) {
        this.type = type;
        this.content = content;
    }
}
