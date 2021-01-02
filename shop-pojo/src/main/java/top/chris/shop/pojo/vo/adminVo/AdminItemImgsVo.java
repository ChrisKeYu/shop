package top.chris.shop.pojo.vo.adminVo;

import lombok.Data;

@Data
public class AdminItemImgsVo {
    private String id;
    private String itemId;
    private String imgUrl;
    private String directory;   //要删除文件所在目录
    private String deleteFile;  //要删除的文件

}
