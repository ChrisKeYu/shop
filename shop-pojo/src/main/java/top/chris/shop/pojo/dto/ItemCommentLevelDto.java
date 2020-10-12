package top.chris.shop.pojo.dto;

import lombok.Data;

/**
 *DTO对象（数据传输对象），是用在服务层需要接收的数据和返回的数据。
 */
@Data
public class ItemCommentLevelDto {
    private Integer counts;
    private Integer commentLevel;
}
