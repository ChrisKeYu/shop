package top.chris.shop.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.entity.Example.Criteria;
import top.chris.shop.util.JsonResult;
import top.chris.shop.mapper.StuMapper;
import top.chris.shop.pojo.Stu;
import top.chris.shop.service.PropagationService;

import java.util.List;
import java.util.UUID;

@Service
public class PropagationServiceImpl implements PropagationService {

     //注入通用Mapper生成好的单表操作的工具类
    @Autowired
    private StuMapper stuMapper;

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public List<Stu> getStuWhenIdIn(List<String> ids) {
        //多余复制的单表数据操作，需要用到example对象，构造方法中需要写入待操作单表的类。
        Example example = new Example(Stu.class);
        //使用Criteria对象实现复杂条件的单表操作
        Criteria criteria = example.createCriteria();
        //andIn方法内部实现了循环，所有只需要传入一个可迭代对象即可，这里的第一个参数时实体类Pojo中的成员变量。
        criteria.andIn("stuId",ids);
        return stuMapper.selectByExample(example);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public List<Stu> getAllStu() {
        return stuMapper.selectAll();
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public JsonResult addStu(Stu stu) {
        stuMapper.insert(stu);
        return JsonResult.isOk("insert success");
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public JsonResult updateStu(Stu stu) {
        stuMapper.updateByPrimaryKey(stu);
        return JsonResult.isOk("update success");
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public JsonResult deleteStu(String stuId) {
        Stu stu = new Stu();
        stu.setStuId(stuId);
        stuMapper.delete(stu);
        return JsonResult.isOk("delete success");
    }


    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void addParentStu() {
        Stu stu = new Stu();
        stu.setStuId(UUID.randomUUID().toString());
        stu.setStuName("parent1");
        addStu(stu);
    }

    //@Transactional(propagation = Propagation.MANDATORY) //再调用此方法前就会先检查调用者是否有事务，如果没有那么该方法不会不被执行，而且抛出异常。
    //@Transactional(propagation = Propagation.REQUIRES_NEW)
    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void addSubStu() {
        Stu stu1 = new Stu();
        stu1.setStuId(UUID.randomUUID().toString());
        stu1.setStuName("Sub1");
        addStu(stu1);

        //制造一个异常
        int x = 1/0;

        Stu stu2 = new Stu();
        stu2.setStuId(UUID.randomUUID().toString());
        stu2.setStuName("Sub2");
        addStu(stu2);
    }

}
