package com.mercury.executor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @param :
 * @author: kenny [411316753@qq.com]
 * @Date: 2018/4/23-10:19
 * @Description:
 * @return:
 */
public class ExecutorExample {

  public static void main(String[] args) {
    ExecutorService service = Executors.newCachedThreadPool();
    //内部类初始化 InnerClass demo = new UpperClass().new InnerClass();
    service.execute(new ExecutorExample().new Task("task1"));
    service.execute(new ExecutorExample().new Task("task2"));
    service.execute(new ExecutorExample().new Task("task3"));
    service.shutdown();
  }




  class Task implements Runnable{
    private final String name;

    public Task(String name) {
      this.name = name;
    }

    @Override
    public void run() {
      try {
        for (int i = 0; i < 5; i++) {
          TimeUnit.SECONDS.sleep(1);
          System.out.println(name+"-["+i+"]");
        }
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }
}
