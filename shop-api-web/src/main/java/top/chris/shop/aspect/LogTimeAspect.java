package top.chris.shop.aspect;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@Aspect
@Slf4j //lombok，可以少写生成logger对象
public class LogTimeAspect {

    //设定切点：通过限定表达式，任意返回值，选中指定的目标service层中所有实现类的所有任意参数的方法。
    @Pointcut("execution(* top.chris..*.service.impl.*.*(..))")
    public void pointCut(){}

    //切面方法：环绕通知
    @Around("pointCut()")
    public Object arroundServiceLog(ProceedingJoinPoint pjp) throws Throwable {
        Long startTime = System.currentTimeMillis();
        //监控并接受指定范围内的所有方法的返回值，不需要对它进行干涉，只需要在其前后添加一下额外的信息(这个方法运行多长时间)
        Object result = pjp.proceed();
        Long endTime = System.currentTimeMillis();
        Long time = endTime - startTime;
        if (time < 1000){
            log.info("------方法{}.{}在{}时使用参数{}调用,并得到了结果{}，共耗时{}毫秒------",pjp.getTarget().getClass().getName()
                    ,pjp.getSignature().getName(),new Date(startTime), ReflectionToStringBuilder.toString(pjp.getArgs()),result,time);
        }else if(time > 1000 && time < 3000){
            log.warn("------方法{}.{}在{}时使用参数{}调用,并得到了结果{}，共耗时{}毫秒------",pjp.getTarget().getClass().getName()
                    ,pjp.getSignature().getName(),new Date(startTime), ReflectionToStringBuilder.toString(pjp.getArgs()),result,time);
        }else {
            log.error("------方法{}.{}在{}时使用参数{}调用,并得到了结果{}，共耗时{}毫秒------",pjp.getTarget().getClass().getName()
                    ,pjp.getSignature().getName(),new Date(startTime), ReflectionToStringBuilder.toString(pjp.getArgs()),result,time);
        }
        return result;
    }
}
