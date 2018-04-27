package com.mercury.atomic;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @param :
 * @author: kenny [411316753@qq.com]
 * @Date: 2018/4/24-11:39
 * @Description:
 * @return:
 */
public class AtomicIntegerFieldExample {

  public static void main(String[] args) throws InterruptedException {
    Student student = new Student(1,"kenny");
    AtomicIntegerFieldUpdater<Student> updater = AtomicIntegerFieldUpdater.newUpdater(Student.class,"id");
    ExecutorService service = Executors.newCachedThreadPool();
    for (int i = 0; i < 10 ; i++) {
      service.execute(new Runnable() {
        @Override
        public void run() {
          for (int j = 0; j <10000 ; j++) {
            updater.getAndIncrement(student);
          }
        }
      });
    }
    service.shutdown();
    service.awaitTermination(1, TimeUnit.DAYS);
    System.out.println(student);
  }


  private static class Student {
    volatile int id;
    String name;

    public Student(int id, String name) {
      this.id = id;
      this.name = name;
    }

    @Override
    public String toString() {
      return "Student id = " + id + ",name = " + name;
    }
  }
}
