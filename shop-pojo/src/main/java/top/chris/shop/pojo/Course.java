package top.chris.shop.pojo;

import java.io.Serializable;
import javax.persistence.*;

@Table(name = "`course`")
public class Course implements Serializable {
    /**
     * course_id 课程主键
     */
    @Id
    @Column(name = "`course_id`")
    private String courseId;

    /**
     * 课程名字 课程名字
     */
    @Column(name = "`course_name`")
    private String courseName;

    private static final long serialVersionUID = 1L;

    /**
     * 获取course_id 课程主键
     *
     * @return course_id - course_id 课程主键
     */
    public String getCourseId() {
        return courseId;
    }

    /**
     * 设置course_id 课程主键
     *
     * @param courseId course_id 课程主键
     */
    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    /**
     * 获取课程名字 课程名字
     *
     * @return course_name - 课程名字 课程名字
     */
    public String getCourseName() {
        return courseName;
    }

    /**
     * 设置课程名字 课程名字
     *
     * @param courseName 课程名字 课程名字
     */
    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }
}