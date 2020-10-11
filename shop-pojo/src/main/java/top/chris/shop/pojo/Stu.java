package top.chris.shop.pojo;

import java.io.Serializable;
import javax.persistence.*;

@Table(name = "`stu`")
public class Stu implements Serializable {
    /**
     * stu_id 主键
     */
    @Id
    @Column(name = "`stu_id`")
    private String stuId;

    /**
     * 名字 名字
     */
    @Column(name = "`stu_name`")
    private String stuName;

    private static final long serialVersionUID = 1L;

    /**
     * 获取stu_id 主键
     *
     * @return stu_id - stu_id 主键
     */
    public String getStuId() {
        return stuId;
    }

    /**
     * 设置stu_id 主键
     *
     * @param stuId stu_id 主键
     */
    public void setStuId(String stuId) {
        this.stuId = stuId;
    }

    /**
     * 获取名字 名字
     *
     * @return stu_name - 名字 名字
     */
    public String getStuName() {
        return stuName;
    }

    /**
     * 设置名字 名字
     *
     * @param stuName 名字 名字
     */
    public void setStuName(String stuName) {
        this.stuName = stuName;
    }
}