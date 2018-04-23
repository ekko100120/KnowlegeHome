package com.mercury.executor;

import java.util.concurrent.*;

/**
 * @param :
 * @author: kenny [411316753@qq.com]
 * @Date: 2018/4/23-12:17
 * @Description:
 * @return:
 */


public class CallableExample {
  public static void main(String[] args) throws ExecutionException, InterruptedException {
    ExecutorService service = Executors.newCachedThreadPool();


    //Cabable<V> --> Future<V> --> future.get()
    Future<Integer> future = service.submit(new Callable<Integer>() {
      @Override
      public Integer call() throws Exception {
        System.out.println("Callable is running");
        TimeUnit.SECONDS.sleep(2);
        return  47;
      }
    });
    service.shutdown();
    System.out.println("future result: "+future.get());
  }
}
