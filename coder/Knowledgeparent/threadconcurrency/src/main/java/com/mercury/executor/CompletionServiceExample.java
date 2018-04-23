package com.mercury.executor;

import java.util.concurrent.*;

/**
 * @param :
 * @author: kenny [411316753@qq.com]
 * @Date: 2018/4/23-13:51
 * @Description:
 * @return:
 */
public class CompletionServiceExample {

  // CompletionService的初始化方法
  // 1. 获得ExecutorService对象实例
  // 2. new 初始化  CompletionService<Integer> completionService = new ExecutorCompletionService<>(service);
  public static void main(String[] args) throws InterruptedException, ExecutionException {
    ExecutorService service = Executors.newCachedThreadPool();
    CompletionService<Integer> completionService = new ExecutorCompletionService<>(service);
    for (int i = 0; i < 5 ; i++) {
      completionService.submit(new CompletionServiceExample().new TaskInteger(i));
    }
    service.shutdown();
    for (int i = 0; i < 5; i++) {
      Future<Integer> future = completionService.take();
      System.out.println(future.get());
    }
  }

  //Callable的泛型类型与 return返回值类型相同
  class TaskInteger implements Callable<Integer>{

    private final  int sum;

    TaskInteger(int sum) {
      this.sum = sum;
    }

    @Override
    public Integer call() throws Exception {
      long start = System.currentTimeMillis();
      TimeUnit.SECONDS.sleep(sum);
      System.out.println("sleep time:"+(System.currentTimeMillis()-start));
      return sum*sum;
    }
  }
}

//    CompletionService接口定义了一组任务管理接口:
//    submit() - 提交任务
//    take() - 获取任务结果
//    poll() - 获取任务结果
//    ExecutorCompletionService类是CompletionService接口的实现
//    ExecutorCompletionService内部管理者一个已完成任务的阻塞队列
//    ExecutorCompletionService引用了一个Executor, 用来执行任务
//    submit()方法最终会委托给内部的executor去执行任务
//    take/poll方法的工作都委托给内部的已完成任务阻塞队列
//    如果阻塞队列中有已完成的任务, take方法就返回任务的结果, 否则阻塞等待任务完成
//    poll与take方法不同, poll有两个版本:
//    无参的poll方法 --- 如果完成队列中有数据就返回, 否则返回null
//    有参数的poll方法 --- 如果完成队列中有数据就直接返回, 否则等待指定的时间, 到时间后如果还是没有数据就返回null
//    ExecutorCompletionService主要用与管理异步任务 (有结果的任务, 任务完成后要处理结果)
