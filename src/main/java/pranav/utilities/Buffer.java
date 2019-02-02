package pranav.utilities;

import android.os.Handler;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created on 07-11-2017 at 16:31 by Pranav Raut.
 * For QRCodeProtection
 */

@SuppressWarnings({"SameParameterValue", "unused", "WeakerAccess"})
public abstract class Buffer<E> implements Tasks.Task<E> {

    protected final E d;
    protected ArrayList<Tasks.Task<E>> tasks = new ArrayList<>();
    protected ArrayList<E> items = new ArrayList<>();
    protected ArrayList<Runnable> runnableArrayList = new ArrayList<>();
    protected long delay = 20;
    protected int i = 1;

    protected Handler h = new Handler();
    protected E current;
    protected E stepCount;

    public Buffer() {
        this(null);
    }

    public Buffer(E d) {
        current = this.d = d;
        addTasks(this);
    }

    public void enqueue(E e) {
        items.add(e);
        Runnable runnable = () -> {
            final E a = current = items.remove(0);
            i--;
            for (Tasks.Task<E> k : tasks) k.execute(a);
        };
        runnableArrayList.add(runnable);
        h.postDelayed(runnable, i++ * delay);
    }

    public synchronized void runTasks(E e) {
        for (Tasks.Task<E> k : tasks) k.execute(e);
    }

    public void stop() {
        for (Runnable r : runnableArrayList) h.removeCallbacks(r);
        reset();
    }

    private void reset() {
        runnableArrayList.clear();
        items.clear();
        i = 0;
        current = d;
    }

    public void setEs(ArrayList<E> items) {
        this.items.clear();
        this.items.addAll(items);
    }

    public abstract void execute(E e);

    public void setDelaySpan(long delay) {
        this.delay = delay;
    }

    @SafeVarargs
    public final void addTasks(Tasks.Task<E>... tasks) {
        this.tasks.addAll(Arrays.asList(tasks));
    }

    @SafeVarargs
    public final void removeTasks(Tasks.Task<E>... tasks) {
        this.tasks.removeAll(Arrays.asList(tasks));
    }

    public ArrayList<E> getItems() {
        return items;
    }

    public int getSize() {
        return items.size();
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    public E getCurrentItem() {
        return current;
    }

    public void setStepCount(E stepCount) {
        this.stepCount = stepCount;
    }

    //public abstract void addAvg(E e);

    public static abstract class IntegerBuffer extends Buffer<Integer> {

        private Integer sum = 0;
        private float avg;
        private int n = 0;

        public IntegerBuffer() {
            this(0);
        }

        public IntegerBuffer(Integer d) {
            super(d);
            stepCount = -1;
        }


        @Override
        public void setStepCount(Integer stepCount) {
            super.setStepCount(stepCount);
            avg = stepCount;
        }

        public void addAvg(Integer a) {
            if (!items.isEmpty()) {
                Integer i = items.get(items.size() - 1);

                if (stepCount != -1) {
                    sum += Math.abs(a - i);
                    avg = sum / ++n;
                }

                while (Math.abs(i - a) > avg) {
                    enqueue((int) (i + (a > i ? avg : -avg)));
                    i = items.get(items.size() - 1);
                }
            }
            enqueue(a);
        }

        @Override
        public void stop() {
            super.stop();
            n = sum = 0;
            avg = d;
        }

        /*
    //items.toArray((E[]) Array.newInstance(items.get(0).getClass()

        }*/
    }
}
