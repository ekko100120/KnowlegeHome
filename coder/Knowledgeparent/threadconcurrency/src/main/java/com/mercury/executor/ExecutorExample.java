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
    // 实际上是调用父接口Executor的execute方法；
    service.execute(new ExecutorExample().new Task("task1"));
    service.execute(new ExecutorExample().new Task("task2"));
    service.execute(new ExecutorExample().new Task("task3"));
    //shutdown方法 在关闭 ExecutorService 之前等待提交的任务执行完成。
    // shutdownNow方法 会阻止开启新的任务并且尝试停止当前正在执行的线程，一旦调用该方法，线程池中将没有激活的任务，没有等待执行的任务，也没有新任务提交。没有任务执行的ExecutorService将会被回收。
    //方法submit扩展了Executor.execute(Runnable) 方法， 创建并返回一个 Future 结果，这个Future可以取消任务的执行或者等待完成得到返回值。
    //方法invokeAny and invokeAll 可以执行一组任务，等待至少一个任务或者多个任务完成（ExecutorCompletionService扩展了这些方法的实现）。
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

/***
output:

task1-[0]
task2-[0]
task3-[0]
task3-[1]
task1-[1]
task2-[1]
task3-[2]
task2-[2]
task1-[2]
task3-[3]
task2-[3]
task1-[3]
task1-[4]
task2-[4]
task3-[4]

 ***/
