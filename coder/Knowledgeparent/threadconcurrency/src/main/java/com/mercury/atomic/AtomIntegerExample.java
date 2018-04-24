package com.mercury.atomic;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @param :
 * @author: kenny [411316753@qq.com]
 * @Date: 2018/4/23-18:00
 * @Description:
 * @return:
 */
public class AtomIntegerExample {

  public static void main(String[] args) throws InterruptedException {
    AtomicInteger atomicInteger = new AtomicInteger(0);
    ExecutorService service = Executors.newCachedThreadPool();
    for (int i = 0; i <10 ; i++) {
        service.execute(new Runnable() {
          @Override
          public void run() {
            for (int j = 0; j <10000 ; j++) {
               atomicInteger.incrementAndGet();
            }
          }
        });
    }
    service.shutdown();
    while (!service.awaitTermination(1, TimeUnit.SECONDS)){
      System.out.println(atomicInteger.get());
    }
    System.out.println("++++++++++++++++++++++++++++++++++");
    System.out.println(atomicInteger.get());
  }
}
