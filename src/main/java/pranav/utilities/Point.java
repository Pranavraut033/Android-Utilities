package pranav.utilities;

/**
 * Created on 27-08-2017 at 20:57 by Pranav Raut.
 * For QRCodeProtection
 */

public class Point<E> {
    public E X;
    public E Y;

    public Point() {
        X = null;
        Y = null;
    }

    public Point(E x, E y) {
        this.X = x;
        this.Y = y;
    }

    public void set(E x, E y) {
        this.X = x;
        this.Y = y;
    }

    public final boolean equals(E x, E y) {
        return this.X == x && this.Y == y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Point point = (Point) o;
        if (X != point.X) return false;
        return Y == point.Y;
    }

}
