package top.chris.shop.pojo;

import java.io.Serializable;
import javax.persistence.*;

@Table(name = "`stu_course`")
public class StuCourse implements Serializable {
    /**
     * stu_id 学生id
     */
    @Id
    @Column(name = "`stu_id`")
    private String stuId;

    /**
     * course_id 课程id
     */
    @Column(name = "`course_id`")
    private String courseId;

    private static final long serialVersionUID = 1L;

    /**
     * 获取stu_id 学生id
     *
     * @return stu_id - stu_id 学生id
     */
    public String getStuId() {
        return stuId;
    }

    /**
     * 设置stu_id 学生id
     *
     * @param stuId stu_id 学生id
     */
    public void setStuId(String stuId) {
        this.stuId = stuId;
    }

    /**
     * 获取course_id 课程id
     *
     * @return course_id - course_id 课程id
     */
    public String getCourseId() {
        return courseId;
    }

    /**
     * 设置course_id 课程id
     *
     * @param courseId course_id 课程id
     */
    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }
}