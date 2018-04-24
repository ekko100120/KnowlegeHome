package com.mercury.atomic;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @param :
 * @author: kenny [411316753@qq.com]
 * @Date: 2018/4/24-9:00
 * @Description:
 * @return:
 */
public class AtomicReferenceExample {
  public static void main(String[] args) throws InterruptedException {
    AtomicReference<Element> reference = new AtomicReference<>(new Element(1,1));
    ExecutorService service = Executors.newCachedThreadPool();
    for (int i = 0; i < 5; i++) {
      service.execute(new Runnable() {
        @Override
        public void run() {
          for (int j = 0; j <10000 ; j++) {
            boolean flag =false;
            // 自旋锁
            while (!flag){
              Element storedElement =reference.get();
              Element newElement = new Element(storedElement.x+1,storedElement.y+1);
              flag=reference.compareAndSet(storedElement,newElement);
            }
          }
        }
      });
    }
    service.shutdown();
    service.awaitTermination(1, TimeUnit.DAYS);
    System.out.println("Element.x = "+reference.get().x+" ,element.y= "+reference.get().y);
  }

  private static class Element{
     private  int x;
     private  int y;


    public Element(int x, int y) {
      this.x = x;
      this.y = y;
    }
  }
}
