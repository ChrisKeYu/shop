package top.chris.shop.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import top.chris.shop.ShopMainApplication;
import top.chris.shop.pojo.Stu;

/**
 * 测试用例，用来测试事务传播的服务层与数据库层是否能够正确的传递数据
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ShopMainApplication.class)
public class PropagationServiceImplTest {

    @Autowired
    private PropagationService propagationService;

    @Test
    public void propagationTestAddStu(){
        Stu stu = new Stu();
        stu.setStuId("5");
        stu.setStuName("中科院");
        propagationService.addStu(stu);
    }

    @Test
    public void propagationTestUpdateStu(){
        Stu stu = new Stu();
        stu.setStuId("5");
        stu.setStuName("中科院2");
        propagationService.updateStu(stu);
    }

    @Test
    public void propagationTestGetAllStu(){
        propagationService.getAllStu();
    }

    @Test
    public void propagationTestDeleteStu(){
        propagationService.deleteStu("5");
    }

}
