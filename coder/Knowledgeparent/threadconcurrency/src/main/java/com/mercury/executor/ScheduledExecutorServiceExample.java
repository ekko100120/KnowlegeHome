package com.mercury.executor;

import java.util.concurrent.*;

/**
 * @param :
 * @author: kenny [411316753@qq.com]
 * @Date: 2018/4/23-14:33
 * @Description:
 * @return:
 */
public class ScheduledExecutorServiceExample {

  //1. ScheduledExecutorService的初始化过程有点怪异(ScheduledThreadPoolExecutor是接口的实现类), 用的 new ScheduledThreadPoolExecutor   ？？？
  //2. .scheduleAtFixedRate()方法每隔一秒重复打印“beep"
  //3. .schedule()方法在10秒后延迟执行，它的作用是取消第一个任务
  //涉及到  ScheduledExecutorService 和 ScheduledFuture 两个API
  public static void main(String[] args) {
    ScheduledExecutorService scheduler = new ScheduledThreadPoolExecutor(2);
    ScheduledFuture<?> scheduledFuture = scheduler.scheduleAtFixedRate(new Runnable() {
      @Override
      public void run() {
        System.out.println("beep");
      }
    },1,1, TimeUnit.SECONDS);
    scheduler.scheduleWithFixedDelay(new Runnable() {
          @Override
          public void run() {
            System.out.println("delay");
          }
        },1,1, TimeUnit.SECONDS
    );
    scheduler.schedule(new Runnable() {
      @Override
      public void run() {
        System.out.println("cancel beep");
        scheduledFuture.cancel(true);
        scheduler.shutdown();
      }
    },10, TimeUnit.SECONDS);
  }
}
