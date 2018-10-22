package pl.mwosz.Snake;

import java.util.Deque;
import java.util.LinkedList;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Snake {

    class ConsoleListner implements Runnable {

        @Override
        public void run() {
            Scanner sc = new Scanner(System.in);

            while (true) {
                String c = sc.nextLine();
                System.out.println("wczytane z klawiatury: " + c);
                if (c.equalsIgnoreCase("a")) {
                    setDirection(Direction.LEFT);
                } else if (c.equalsIgnoreCase("w")) {
                    setDirection(Direction.UP);
                } else if (c.equalsIgnoreCase("s")) {
                    setDirection(Direction.DOWN);
                } else if (c.equalsIgnoreCase("d")) {
                    setDirection(Direction.RIGHT);
                    System.out.println();
                }
            }
        }
    }

    private Element[][] tab;
    private int n;
    private Deque<Pos> Position = new LinkedList<>();
    private Lock mutex = new ReentrantLock();
    private Direction direction = Direction.LEFT;
    private Direction prevDirection = direction;


    public Snake() {
        this.n = 12;
        this.tab = new Element[n][n];
        for (int i = 0; i < tab.length; i++) {
            for (int j = 0; j < tab[i].length; j++) {
                tab[i][j] = Element.NONE;
            }
        }
        Pos p = new Pos(6, 6);
        Position.addFirst(p);
        tab[p.i][p.j] = Element.SNAKE;
        randomFood();
        Thread t1 = new Thread(new ConsoleListner());
        t1.start();
    }

    public void randomFood() {
        Random rand = new Random();
        int a;
        int b;
        do {
            a = rand.nextInt(n);
            b = rand.nextInt(n);
        } while (tab[a][b] != Element.NONE);

        for (int i = 0; i < tab.length; i++) {
            for (int j = 0; j < tab[i].length; j++) {
                if (tab[i][j] == Element.FOOD) {
                    break;
                } else {
                    tab[a][b] = Element.FOOD;
                }
            }
        }
    }

    public int up() {
        int r = 0;

        Pos p = Position.peekFirst();
        Pos head = new Pos(p.i - 1, p.j);

        if (head.i < 0 || tab[head.i][head.j] == Element.SNAKE) {
            return 0;
        }

        if (tab[head.i][head.j] != Element.FOOD) {
            Pos tail = Position.pollLast();
            tab[tail.i][tail.j] = Element.NONE;
            r = 1;
        } else {
            r = 2;
        }

        Position.addFirst(head);
        tab[head.i][head.j] = Element.SNAKE;

        return r;
    }

    public int down() {
        int r = 0;

        Pos p = Position.peekFirst();
        Pos head = new Pos(p.i + 1, p.j);

        if (head.i >= n || tab[head.i][head.j] == Element.SNAKE) {
            return 0;
        }

        if (tab[head.i][head.j] != Element.FOOD) {
            Pos tail = Position.pollLast();
            tab[tail.i][tail.j] = Element.NONE;
            r = 1;
        } else {
            r = 2;
        }

        Position.addFirst(head);
        tab[head.i][head.j] = Element.SNAKE;

        return r;
    }

    public int left() {
        int r = 0;

        Pos p = Position.peekFirst();
        Pos head = new Pos(p.i, p.j - 1);

        if (head.j < 0 || tab[head.i][head.j] == Element.SNAKE) {
            return 0;
        }

        if (tab[head.i][head.j] != Element.FOOD) {
            Pos tail = Position.pollLast();
            tab[tail.i][tail.j] = Element.NONE;
            r = 1;
        } else {
            r = 2;
        }

        Position.addFirst(head);
        tab[head.i][head.j] = Element.SNAKE;

        return r;
    }

    public int right() {
        int r = 0;

        Pos p = Position.peekFirst();
        Pos head = new Pos(p.i, p.j + 1);

        if (head.j >= n || tab[head.i][head.j] == Element.SNAKE) {
            return r;
        }

        if (tab[head.i][head.j] != Element.FOOD) {
            Pos tail = Position.pollLast();
            tab[tail.i][tail.j] = Element.NONE;
            r = 1;
        } else {
            r = 2;
        }

        Position.addFirst(head);
        tab[head.i][head.j] = Element.SNAKE;

        return r;
    }


    public Direction getPrevDirection() {
        Direction d;
        mutex.lock();
        d = prevDirection;
        mutex.unlock();
        return d;
    }

    public Direction getDirection() {
        Direction d;
        mutex.lock();
        d = direction;
        mutex.unlock();
        return d;
    }

    public void setDirection(Direction direction) {
        mutex.lock();
        this.prevDirection = this.direction;
        this.direction = direction;
        System.out.println("kierunek = " + direction);
        mutex.unlock();
    }

    public void play()  {

        while (true) {
            System.out.println(this);

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Direction d = getDirection();
            Direction pd = getPrevDirection();
            int r = 1;
            if (d == Direction.DOWN && pd != Direction.UP) {
                r = down();
            } else if (d == Direction.LEFT && pd != Direction.RIGHT) {
                r = left();
            } else if (d == Direction.RIGHT && pd != Direction.LEFT) {
                r = right();
            } else if (d == Direction.UP && pd != Direction.DOWN) {
                r = up();
            }

            System.out.println("r = " + r);
            if (r == 0) {
                System.out.println("GAME OVER");
                break;
            } else if (r == 2) {
                randomFood();
            }
        }
    }


    @Override
    public String toString() {
        StringBuffer s = new StringBuffer();

        s.append("+");
        for (int i = 0; i < tab.length; i++) {
            s.append("---");
        }
        s.append("+\n");
        for (int i = 0; i < tab.length; i++) {
            s.append("|");
            for (int j = 0; j < tab[i].length; j++) {
                if (tab[i][j] == Element.SNAKE) {
                    s.append("SNK");
                } else if (tab[i][j] == Element.NONE) {
                    s.append("   ");
                } else if (tab[i][j] == Element.FOOD) {
                    s.append(" # ");
                }
            }
            s.append("|\n");
        }
        s.append("+");
        for (int i = 0; i < tab.length; i++) {
            s.append("---");
        }
        s.append("+");

        return s.toString();
    }


    public static void main(String[] args) {

        Snake game = new Snake();
        game.play();
    }
}
