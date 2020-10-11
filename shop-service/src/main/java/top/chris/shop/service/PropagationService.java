package top.chris.shop.service;

import top.chris.shop.util.JsonResult;
import top.chris.shop.pojo.Stu;

import java.util.List;
/**
 * 事务传播演示控制器，专门用来演示事务的传播功能和特性的控制器
 * 任何的事务都是具有原子性的，即要么成功，要么失败。
 * 事务的传播：在一个Controller层中需要处理一个请求，该请求需要调用其它多个Service层中的方法来完成,那
 *           么在调多个业务层的方法时，也要保证各业务层方法之间的事务可以接受人为控制的行为，这种行为就
 *           叫做事务的传播行为(在两个不同的Service层之间，如何实现共享和使用事务)。
 */
public interface PropagationService {

    public List<Stu> getStuWhenIdIn(List<String> ids);

    public List<Stu> getAllStu();

    public JsonResult addStu(Stu stu);

    public JsonResult updateStu(Stu stu);

    public JsonResult deleteStu(String stuId);

    /**
     * 下面模拟一下事务传播的环境，即同时需要用到多个Service中的不同方法来完成一个请求
     */
    public void addParentStu();

    public void addSubStu();

}
