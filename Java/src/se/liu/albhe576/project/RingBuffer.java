package se.liu.albhe576.project;

public class RingBuffer<T>
{
    private T[] container;
    private int head;
    private int tail;
    private int count;
    private int initialCapacity;
    public T deque(){
        if(this.head == this.tail){
           return null;
        }
        T t = container[this.head];
        this.head = (this.head + 1) % this.initialCapacity;
        return t;
    }
    public T getHead(){
        return this.count > 0 ? this.container[this.head] : null;
    }
    public T getTail(){
        return this.count > 0 ? this.container[this.tail] : null;
    }
    @SuppressWarnings("unchecked")
    private void resizeContainer(){
        if(this.count > this.initialCapacity){
            int prevCap = this.initialCapacity;
            this.initialCapacity *= 2;

            T[] prevContainer = this.container;
            this.container = (T[]) new Object[this.initialCapacity];

            System.arraycopy(prevContainer, 0, this.container, 0, prevCap);
        }
    }
    public void enqueue(T element){
        resizeContainer();
        container[this.tail] = element;
        this.tail = (this.tail + 1) % this.initialCapacity;
    }

    @SuppressWarnings("unchecked")
    public RingBuffer(){
        this.initialCapacity = 10;
        this.container = (T[]) new Object[this.initialCapacity];
        this.count = 0;
    }
    @SuppressWarnings("unchecked")
    public RingBuffer(int initialCapacity){
        this.initialCapacity = initialCapacity;
        this.container = (T[]) new Object[this.initialCapacity];
        this.count = 0;
        this.head = 0;
        this.tail= 0;
    }
}
