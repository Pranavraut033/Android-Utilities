package pranav.utilities;

import android.os.AsyncTask;
import android.os.Handler;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created on 07-11-2017 at 16:31 by Pranav Raut.
 * For QRCodeProtection
 */

public abstract class Buffer<E> implements Task<E> {

    A a = new A();
    ArrayList<Task<E>> tasks = new ArrayList<>();
    private ArrayList<E> items = new ArrayList<>();
    private long delay = 20;
    private int i = 1;

    public Buffer() {
        addTasks(this);
    }

    public void justAdd(E e) {
        items.add(e);
    }

    public void add(E e) {
        items.add(e);
        E a = items.remove(0);
        new Handler().postDelayed(() -> {
            i--;
            for (Task<E> k : tasks) k.execute(a);
        }, i++ * delay);
        //this.a.doInBackground(a);
    }

    public synchronized void runTasks(E e) {
        for (Task<E> k : tasks) k.execute(e);
    }

    public void stop() {

    }

    public void setEs(ArrayList<E> items) {
        this.items.clear();
        this.items.addAll(items);
    }

    public void run() {
    }

    public abstract void execute(E e);

    public Buffer<E> setDelaySpan(long delay) {
        this.delay = delay;
        return this;
    }

    @SafeVarargs
    public final void addTasks(Task<E>... tasks) {
        this.tasks.addAll(Arrays.asList(tasks));
    }

    @SafeVarargs
    public final void removeTasks(Task<E>... tasks) {
        this.tasks.removeAll(Arrays.asList(tasks));
    }

    class A extends AsyncTask<E, String, String> {
        @SafeVarargs
        @Override
        protected final String doInBackground(E... objects) {
            for (E o : objects) {
                try {
                    Buffer.this.wait(delay);
                    Buffer.this.execute(o);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
    }


}
