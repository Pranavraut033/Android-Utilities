package pranav.utilities;

public class Tasks<E> {

    public interface Task<E> {
        void execute(E e);
    }

    public interface DTask<E> {
        void execute(E[] es);
    }
}

