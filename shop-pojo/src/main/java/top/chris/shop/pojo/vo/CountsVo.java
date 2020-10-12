package top.chris.shop.pojo.vo;

import lombok.Data;

//各个评价的数量模型（各评论数量可以为0）
@Data
public class CountsVo {
    private Integer totalCounts;
    private Integer goodCounts;
    private Integer normalCounts;
    private Integer badCounts;
    //构造一个无参构造器，赋初值为0。
    public CountsVo(){
        this.totalCounts = 0;
        this.goodCounts = 0;
        this.normalCounts = 0;
        this.badCounts= 0;
    }
}
