package utils;

/**
 *
 * @author aaron
 */
public class Queue<T>{
    private static class Node<T>{
        Node<T> next;
        T elem;
        
        public Node(T e){
            elem = e;
        }
    }
    
    private Node<T> head;
    private Node<T> tail;
    
    private int size;
    
    public Queue(){ }
    
    public void enqueue(T elem){
        Node<T> n = new Node(elem);
        if(head == null){
            head = tail = n;
        }
        else{
            tail.next = n;
            tail = n;
        }
        size++;
    }
    
    public T dequeue(){
        if(size == 0){
            head = tail = null;
            return null;
        }
        
        T elem = head.elem;
        
        head = head.next;
                
        size--;
        
        return elem;
    }
    
    public int size(){
        return size;
    }
}
