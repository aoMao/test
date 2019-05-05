import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class TestCondition {

    static ReentrantLock lock = new ReentrantLock();
    static Condition oddCondition = lock.newCondition();
    static Condition evenCondition = lock.newCondition();

    static volatile int count = 0;

    static Object object = new Object();

    static class OddThread implements Runnable {

        @Override
        public void run() {
            lock.lock();
            try {
                while (count < 100) {
                    if ((count & 1) != 0 && count < 100) {
                        System.out.println(Thread.currentThread().getName() + " 等待");
                        oddCondition.await();
                        System.out.println(Thread.currentThread().getName() + " 被唤醒");
                    }
                    if (count < 100) {
                        if ((count & 1) != 0)
                            continue;
                        System.out.println(Thread.currentThread().getName() + " : " + ++count);
                        evenCondition.signal();
                        System.out.println(Thread.currentThread().getName() + " 唤醒一个even线程");
                    } else {
                        evenCondition.signalAll();
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        }
    }

    static class EvenThread implements Runnable {

        @Override
        public void run() {
            lock.lock();
            try {
                while (count < 100) {
                    if ((count & 1) == 0 && count < 100) {
                        System.out.println(Thread.currentThread().getName() + " 等待");
                        evenCondition.await();
                        System.out.println(Thread.currentThread().getName() + " 被唤醒");
                    }
                    if (count < 100) {
                        if ((count & 1) == 0)
                            continue;
                        System.out.println(Thread.currentThread().getName() + " : " + ++count);
                        oddCondition.signal();
                        System.out.println(Thread.currentThread().getName() + " 唤醒一个odd线程");
                    } else {
                        oddCondition.signalAll();
                    }

                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        }
    }

    static class OddThread1 implements Runnable {

        @Override
        public void run() {
            synchronized (object) {
                while (count < 100) {
                    while ((count & 1) != 0 && count < 100) {
                        System.out.println(Thread.currentThread().getName() + " 等待");
                        try {
                            object.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        System.out.println(Thread.currentThread().getName() + " 被唤醒");
                    }

                    if (count < 100) {
                        System.out.println(Thread.currentThread().getName() + " : " + ++count);
                    }
                    object.notifyAll();
                    System.out.println(Thread.currentThread().getName() + " 唤醒所有线程");
                }
                System.out.println("odd end");
            }
        }
    }

    static class EvenThread1 implements Runnable {

        @Override
        public void run() {
            synchronized (object) {
                while (count < 100) {
                    while ((count & 1) == 0 && count < 100) {
                        System.out.println(Thread.currentThread().getName() + " 等待");
                        try {
                            object.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        System.out.println(Thread.currentThread().getName() + " 被唤醒");
                    }

                    if (count < 100) {
                        System.out.println(Thread.currentThread().getName() + " : " + ++count);
                    }
                    object.notifyAll();
                    System.out.println(Thread.currentThread().getName() + " 唤醒所有线程");
                }
            }
            System.out.println("even end");
        }
    }

    public static void main(String[] args) {
        for (int i = 0; i < 11; ++i) {
            Thread oddThread = new Thread(new OddThread1());
            oddThread.setName("odd-" + i);
            oddThread.start();
        }

        for (int i = 0; i < 5; ++i) {
            Thread evenThread = new Thread(new EvenThread1());
            evenThread.setName("even-" + i);
            evenThread.start();
        }

//		Thread oddThread = new Thread(new OddThread1());
//		oddThread.setName("odd");
//		Thread evenThread = new Thread(new EvenThread1());
//		evenThread.setName("even");
//		oddThread.start();
//		evenThread.start();
    }
}
