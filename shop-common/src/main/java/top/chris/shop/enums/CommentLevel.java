package top.chris.shop.enums;

/**
 * 构建一个枚举类型，用数字来代替对应的中文。
 */
public enum CommentLevel {
    GOOD(1, "好评"), NORMAL(2, "中评"), BAD(0, "差评");

    public final Integer type;
    public final String content;

    CommentLevel(Integer type, String content) {
        this.type = type;
        this.content = content;
    }
}
