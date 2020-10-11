package top.chris.shop.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;
import top.chris.shop.service.PropagationService;

import java.util.ArrayList;
import java.util.List;

/**
 * 事务传播演示控制器，专门用来演示事务的传播功能和特性的控制器
 * 只要在某个方法中开启了事务，那么该方法中的任何一个环境出现了异常，那么该方法之前的所有操作都会失效！！
 * 任何的事务都是具有原子性的，即要么成功，要么失败。
 * 事务的传播：在一个Controller层中需要处理一个请求，该请求需要调用其它多个Service层中的方法来完成,那
 *           么在调多个业务层的方法时，也要保证各业务层方法之间的事务可以接受人为控制的行为，这种行为就
 *           叫做事务的传播行为(在两个不同的Service层之间，如何实现共享和使用事务)。
 */
@ApiIgnore //Swage2不会去扫描这个控制器，不需要进入文档
@RestController  //属于RestFul风格的Api
@RequestMapping("/propagation") //添加一个访问前缀
public class PropagatationController {

    @Autowired
    private PropagationService propagationService;

    /**
     * 演示在Spring中事务传播的规则（传播指的是调用者与被调用者之间的关系。例如SUPPORT和MANDATORY就是一个很好的例证！）
     * 当然，一般情况下在控制层中不会写事务属性，这里为了方便，将控制层作为父级事务进行调度，业务层作为子业务调动
     * 在Spring中，事务一共存在以下几种情况
     * Propagation。  （按照使用率的先后顺序讲解）
     *  1、REQUIRED：使用当前的事务，如果当前没有事务，则自己新建一个事务，如果已经存在事务(检查代码块中是否已经存在了事物控制的代码{try-catch})，则加入这个已存在的事务，
     *              到REQUIRED事务内，形成一个整体的事务管理。（其实就是在大的Try-Catch框架内，再加入已经存在的事务，组成一个由REQUIRED主导的整体事务管理。）
     *              也就是说,如果应用在当前父方法调用子方法的场景，相当于总父方法开始开启事务，在子方法最后
     *              结束,如果中间有任何一个环节抛出异常,则全部回滚,也就是说会加入到同一个事务控制。
     *              {通用公式} 多用于增删该操作，换句话说：所有会影响数据库的操作，基本上都要使用事务中的Propagation.REQUIRED来进行事务的控制。
     *              该注解加到了哪个方法上，那么就在该方法内的所有代码块的最外层都加上了一个Try-Catch语句包裹起来。
     *  2、SUPPORTS：如果当前(调用方法中存在事务)存在事务,则(被调用的方法)使用事务,如果当前不存在事务,则不使用事务。也就是说SUPPORTS是跟随调用者的。
     *              在当前场景中,如果父方法开启了事务,那么子方法会使用父方法的事务，发生异常回滚，
     *              如果父方法没有事务，那么子方法不以事务的形式运行，多用于查询操作。
     *              一般不用写，如果要写，那么可以在查询方法上(被调用方)用SUPPORTS，且只有在调用方有事务存在，那么被调用方才支持调用方的事务（共享调用方的事务）
     *  3、MANDATORY:该传播属性强制要求(调用者)必须存在一个事务，如果没有事务存在，(被调用者)则抛出异常。
     *               子方法在运行的时候，要求父方法一定要有事务，不然会抛出异常（很少用）
     *  4、REQUIRED_NEW：如果当前(调用者)有事务，则挂起该事务，并且自己(被调用者)创建一个新的事务执行。
     *                  在本列中，父子调用的状态中比较明显，如果父方法执行中抛出了异常，但不影响子方法运行，子方法运行的时候，会将父方法的事务挂起，自己
     *                  以一个新的事务的方式去运行(被调用者会创建新的事务来控制其本身的代码逻辑，只要本身的逻辑没有错误，就不会产生回滚，即使调用者自身出现了问题，也不管被调用者的事情)。
     *  5、NOT_SUPPORTED：如果当前(调用者)有事务，则把事务挂起，自己(被调用者)以无事务的方式去运行。
     *                   相对于support，在本列中，相当于强制要求子方法不适用事务，就算父方法有事务，子方法也不会使用父方法的事务
     *  6、NEVER：如果当前事务存在，则抛出异常
     *           强制要求子方法和父方法都没有事务，不管是父事务还是子事务，只要存在都会抛出异常。
     *  7、NESTED：以父子嵌套的方式去运行事务，父事务可以处理当子事务中抛出异常之后是否要回滚。
     *            以下作为父事务，准备调度子事务，即Service中的业务，是否要回滚，看下面两个例子：
     *            在本情境中，如果在父方法中try-catch子事务了，那么父事务不会回滚子事务的异常
     *            在本情境中，如果在父方法没有添加try-catch来处理子事务了，那么父事务会回滚子事务的异常
     *
     *
     */
    @Transactional(propagation = Propagation.NESTED)
    @GetMapping
    public String test(){
        /**
         * 1、在未添加事务的情况下，完成一个请求，调用了一个Service层中两个不同的方法完成。
         *    如果第一个方法发生了异常，那么下一个方法还会执行成功吗？这个两个方法之间的事务
         *    是如何运作，答：第一个发生异常，程序会执行第一个发生异常前的代码，也就是会先在
         *    数据库中插入一条parent1的记录，如何执行异常，接着程序停止运行，那么下一个方法
         *    就没有执行，也就没有在数据库中插入Sub1和Sub2的记录，那么很明显不满足请求的要求
         *    从这里就看出来，没有遵循事务的一致性。即要么两个方法一起成功，要么一起失败！
         * 2、解决方法：
         *    1）直接在该请求控制器的方法上加一个事物控制的注解@Transactional(propagation = Propagation.REQUIRED)
         *    propagation = Propagation.REQUIRED =》事务处理原则就是在你的代码中加上了一个try-catch语句！只要中间发生了异常
         *    就回由Spring的事务管理器去完成事务的回滚操作，然后抛出异常原因给Spring的事务异常管理器处理。{方法1：直接在控制层的具体
         *    处理请求方法中添加注解的形式，保证了事务的一致性，这种方式在你对Service层中各事务不是很清楚的情况下，可以直接在控制层的
         *    方法中添加事务注解，以保证，再处理该请求中的所有涉及到的事务都能够保证其一致性。}
         */
      //try { //在父方法中try-catch子事务了，那么父事务不会回滚子事务的异常
          propagationService.addParentStu();

          propagationService.addSubStu();
          //int x = 1/0;

      //}catch (Exception e){
          //e.printStackTrace();
      //}
       return "ok";
    }
}
