package top.chris.shop.pojo.vo.adminVo;

import lombok.Data;

/**
 * 专门用来判断ItemId在ItemSpec、ItemParam、ItemImg表中是否已经存在，存在flag = true，存在flag = false
 */
@Data
public class IsExistVo {
    Boolean paramIsExist;
    Boolean specIsExist;
    Boolean imgIsExist;
}
