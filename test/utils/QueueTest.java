/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author aaron
 */
public class QueueTest {
    
    public QueueTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of enqueue method, of class Queue.
     */
    @Test
    public void testEnqueue() {
        System.out.println("enqueue");
        String elem = "Test";
        Queue<String> instance = new Queue<>();
        instance.enqueue(elem);
        assertTrue(instance.size() == 1);
    }

    /**
     * Test of dequeue method, of class Queue.
     */
    @Test
    public void testDequeue() {
        System.out.println("dequeue");
        String elem1 = "Test1";
        String elem2 = "Test2";
        Queue<String> instance = new Queue<>();
        instance.enqueue(elem1);
        instance.enqueue(elem2);
        String actual = instance.dequeue();
        assertEquals(elem1, actual);
        actual = instance.dequeue();
        assertEquals(elem2, actual);
    }

    /**
     * Test of size method, of class Queue.
     */
    @Test
    public void testSize() {
        System.out.println("size");
        Queue<String> instance = new Queue<>();
        assertTrue(instance.size() == 0);
        instance.enqueue("1");
        assertTrue(instance.size() == 1);
        instance.enqueue("2");
        assertTrue(instance.size() == 2);
        instance.dequeue();
        assertTrue(instance.size() == 1);
    }
    
}
