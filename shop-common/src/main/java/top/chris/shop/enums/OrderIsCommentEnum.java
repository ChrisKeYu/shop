package top.chris.shop.enums;

public enum  OrderIsCommentEnum{
    COMMENT(1, "已评论"), NOT_COMMENT(2, "未评论");

    public final Integer type;
    public final String content;

    OrderIsCommentEnum(Integer type, String content) {
        this.type = type;
        this.content = content;
    }
}