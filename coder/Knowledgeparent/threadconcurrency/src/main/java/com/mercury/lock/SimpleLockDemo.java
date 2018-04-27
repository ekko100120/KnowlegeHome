package com.mercury.lock;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.AbstractQueuedLongSynchronizer;

/**
 * @param :
 * @author: kenny [411316753@qq.com]
 * @Date: 2018/4/25-15:25
 * @Description:
 * @return:
 */
public class SimpleLockDemo {

  private final Sync sync = new Sync();
  public void lock(){
    sync.acquire(1);
  }
  public  void  unlock(){
    sync.release(0);
  }

  public static class Sync extends AbstractQueuedLongSynchronizer{

    public Sync() {
      super();
    }

    @Override
    protected boolean tryAcquire(long arg) {
      
      return  compareAndSetState(0,1);
    }

    @Override
    protected boolean tryRelease(long arg) {
      setState(0);
      return true;
    }
  }

  private static class MyThread extends Thread{
    private final String name;
    private final SimpleLockDemo lockDemo;

    public MyThread(String name, SimpleLockDemo lockDemo) {
      this.name = name;
      this.lockDemo = lockDemo;
    }
    @Override
    public void run() {
      try {
        lockDemo.lock();
        System.out.println(name+ " get the lock");
        TimeUnit.SECONDS.sleep(6);

      } catch (InterruptedException e) {
        e.printStackTrace();
      }finally {
        lockDemo.unlock();
        System.out.println(name+ " release the lock");
      }
    }
  }

  public static void main(String[] args) {
    final SimpleLockDemo lockDemo = new SimpleLockDemo();
    MyThread t1 = new MyThread("t1",lockDemo);
    MyThread t2 = new MyThread("t2",lockDemo);
    MyThread t3 = new MyThread("t3",lockDemo);
    t1.start();
    t2.start();
    t3.start();

    try {
      t1.join();
      t2.join();
      t3.join();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    System.out.println("main thread exit !");
  }
}
