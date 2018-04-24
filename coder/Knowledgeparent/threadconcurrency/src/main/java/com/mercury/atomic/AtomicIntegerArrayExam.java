package com.mercury.atomic;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicIntegerArray;

/**
 * @param :
 * @author: kenny [411316753@qq.com]
 * @Date: 2018/4/24-12:10
 * @Description:
 * @return:
 */
public class AtomicIntegerArrayExam {

  public static void main(String[] args) throws InterruptedException {
    //AtomicIntegerArray通过set()函数初始化保持原子性
    AtomicIntegerArray array = new AtomicIntegerArray(5);
    array.set(0, 0);
    array.set(1, 0);
    array.set(2, 10);
    ExecutorService service = Executors.newCachedThreadPool();
    for (int i = 0; i < 10; i++) {
      service.execute(new Runnable() {
        @Override
        public void run() {
          for (int j = 0; j < 10000; j++) {
            // array.getAndIncrement(index)来读取数组的值
            array.getAndIncrement(0);
          }
          for (int j = 0; j < 10000; j++) {
            array.getAndIncrement(1);
          }
        }
      });
    }

    service.shutdown();
    service.awaitTermination(1, TimeUnit.DAYS);
    System.out.println("array = "+array.toString());
  }
}
