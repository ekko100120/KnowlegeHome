package com.mercury.atomic;

import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.util.Arrays;

/**
 * @param :
 * @author: kenny [411316753@qq.com]
 * @Date: 2018/4/23-16:12
 * @Description:
 * @return:
 */
public class UsafeExample {
  public static void main(String[] args) throws InstantiationException, NoSuchFieldException {

      Unsafe unsafe = null;

    try {
      Field f = Unsafe.class.getDeclaredField("theUnsafe");
      f.setAccessible(true);
      // f.get(null) 没搞懂？？？？
      unsafe = (Unsafe) f.get(null);
    } catch (NoSuchFieldException e) {
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    }

    if (unsafe!=null){
      try {
        //构造一个对象，且不调用其构造函数
        Test test = (Test) unsafe.allocateInstance(Test.class);
        //得到一个对象内部属性的地址
        long x_addr = unsafe.objectFieldOffset(Test.class.getDeclaredField("x"));
        //直接给此属性赋值
        unsafe.getAndSetInt(test,x_addr,100);
        System.out.println(test.get());
      } catch (InstantiationException e) {
        e.printStackTrace();
      } catch (NoSuchFieldException e) {
        e.printStackTrace();
      }
    }

    //通过地址操作数组
    if (unsafe!=null){
      final  int INT_BYTES =4;
      int[] data = new int[10];
      System.out.println(Arrays.toString(data));
      long arrarBaseOffset = unsafe.arrayBaseOffset(int[].class);
      System.out.println("Array address is :" +arrarBaseOffset);
      unsafe.putInt(data,arrarBaseOffset,101);
      unsafe.putInt(data,arrarBaseOffset+INT_BYTES*8,50);
      unsafe.putInt(data,arrarBaseOffset+INT_BYTES*3,50);
      System.out.println(Arrays.toString(data));
    }
    if (unsafe!=null){
      Test test = (Test) unsafe.allocateInstance(Test.class);
      long x_addr = unsafe.objectFieldOffset(Test.class.getDeclaredField("x"));
      unsafe.getAndSetInt(test,x_addr,33);
      System.out.println("test.x is: "+test.get());
      unsafe.compareAndSwapInt(test,x_addr,33,90);
      System.out.println("After CAS：" + test.get());
    }
  }

  static class  Test{
    private  final  int x;

    public Test(int x) {
      this.x = x;
      System.out.println("Test constructor");
    }
    public int get(){
      return  x;
    }
  }
}
