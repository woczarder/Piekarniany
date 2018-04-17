package com.harddrillstudio.wat;

public class BakersProcess extends Thread {

    public static int numberOfThreads = 8;

    public static volatile boolean[] entering = new boolean[numberOfThreads];
    public static volatile int[] number = new int[numberOfThreads];

    public int threadID;

    public BakersProcess(int id) {
        this.threadID = id;
    }

    public void run() {
        for (int i = 0; i < 100; i++) {
            lock(this.threadID);
            // critical section
            System.out.println("["+threadID+"] enters critical section");
            try {
                sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("["+threadID+"] leaves critical section");
            // end critical section
            unlock(this.threadID);
        }
    }

    void lock(int id) {
        entering[id] = true;
        number[id] = getMaxNumber() + 1;
        entering[id] = false;

        for (int j = 1; j < numberOfThreads; j++) {
            while (entering[j]) { /* do nothing */ }
            while ( (number[j] != 0) && (number[j] < number[id] || (number[id] == number[j] && j < id ) ) ) { /* do nothing */ }

        }
    }

    void unlock(int id) {
        number[id] = 0;
    }

    private int getMaxNumber() {
        int maxID = number[0];

        for (int i = 0; i < number.length; i++) {
            if (maxID < number[i])
                maxID = number[i];
        }

        return maxID;
    }

    public static void main(String[] args) {

        for (int i = 0; i < numberOfThreads; i++) {
            entering[i] = false;
            number[i] = 0;
        }

        BakersProcess[] threads = new BakersProcess[numberOfThreads];
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new BakersProcess(i);
            threads[i].start();
        }

    }

}