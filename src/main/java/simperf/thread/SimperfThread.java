package simperf.thread;

import java.util.concurrent.CountDownLatch;

import simperf.result.DataStatistics;

/**
 * �����߳�
 * @author imbugs
 */
public class SimperfThread implements Runnable {

    protected long           transCount    = 0;
    protected DataStatistics statistics    = new DataStatistics();
    protected CountDownLatch threadLatch;

    /**
     * ��������
     */
    protected long           maxTps        = -1;
    /**
     * ��¼���޴���������ƽ���ٶ�
     */
    protected long           overflowCount = 1;

    public void run() {
        try {
            threadLatch.countDown();
            threadLatch.await();
            beforeRunTask();
            statistics.startTime = System.currentTimeMillis();
            while (transCount > 0) {
                if (runTask()) {
                    statistics.successCount++;
                } else {
                    statistics.failCount++;
                }
                transCount--;
                statistics.endTime = System.currentTimeMillis();
                if (maxTps > 0) {
                    // ����һ��ʱ�䣬�ﵽָ��TPS
                    long sleepTime = calcSleepTime();
                    if (sleepTime > 0) {
                        Thread.sleep(sleepTime);
                    }
                }
            }
            afterRunTask();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * ��������ʱ�䣬�Դﵽָ��maxTPS
     */
    protected long calcSleepTime() {
        if (maxTps <= 0) {
            return -1;
        }
        long allCount = statistics.successCount + statistics.failCount;
        long allTime = statistics.endTime - statistics.startTime;
        if (allCount < maxTps * allTime / 1000) {
            if (overflowCount > 1) {
                overflowCount >>= 1;
            }
            return -1;
        } else {
            overflowCount <<= 1;
            float expTime = 1000 / maxTps;
            float actTime = allTime / allCount;
            long differ = (long) (expTime - actTime);
            long sleep = differ + overflowCount;
            if (sleep <= 0) {
                return 1;
            }
            return differ + overflowCount;
        }
    }

    /**
     * ִ��runTask()֮ǰ���ã�ִֻ��һ��
     */
    public void beforeRunTask() {

    }

    /**
     * ִ��runTask()֮����ã�ִֻ��һ��
     */
    public void afterRunTask() {

    }

    public boolean runTask() {
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void setTransCount(long transCount) {
        this.transCount = transCount;
    }

    public long getTransCount() {
        return transCount;
    }

    public void setThreadLatch(CountDownLatch threadLatch) {
        this.threadLatch = threadLatch;
    }

    public CountDownLatch getThreadLatch() {
        return threadLatch;
    }

    public DataStatistics getStatistics() {
        return statistics;
    }

    public long getMaxTps() {
        return maxTps;
    }

    public void setMaxTps(long maxTps) {
        this.maxTps = maxTps;
    }
}