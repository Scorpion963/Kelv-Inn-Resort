package com.example.finalproject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

// custom singly linked list implementation with filter and iterable support
public class CustomLinkedList<T> implements Iterable<T> {
    private Node<T> head;

    // internal node class to store data and next reference
    private static class Node<E> {
        E data;
        Node<E> next;

        public Node(E data) {
            this.data = data;
            this.next = null;
        }
    }

    public CustomLinkedList() {
        head = null;
    }

    // copy constructor: creates a deep copy of another CustomLinkedList
    public CustomLinkedList(CustomLinkedList<T> other) {
        if (other == null) return;
        Node<T> current = other.head;
        while (current != null) {
            this.add(current.data);
            current = current.next;
        }
    }

    // adds a new element to the end of the list
    public void add(T data) {
        Node<T> newNode = new Node<>(data);
        if (head == null) {
            head = newNode;
        } else {
            Node<T> current = head;
            while (current.next != null) {
                current = current.next;
            }
            current.next = newNode;
        }
    }

    // returns the element at the specified index
    public T get(int index) {
        int count = 0;
        Node<T> current = head;
        while (current != null) {
            if (count == index) return current.data;
            current = current.next;
            count++;
        }
        throw new IndexOutOfBoundsException("Index: " + index);
    }

    // sets the value at the specified index
    public void set(int index, T data) {
        int count = 0;
        Node<T> current = head;
        while (current != null) {
            if (count == index) {
                current.data = data;
                return;
            }
            current = current.next;
            count++;
        }
        throw new IndexOutOfBoundsException("Index: " + index);
    }

    // removes the first occurrence of the target value from the list
    public boolean remove(T target) {
        if (head == null || target == null) return false;

        if (head.data.equals(target)) {
            head = head.next;
            return true;
        }

        Node<T> current = head;
        while (current.next != null) {
            if (current.next.data.equals(target)) {
                current.next = current.next.next;
                return true;
            }
            current = current.next;
        }

        return false;
    }

    // returns the number of elements in the list
    public int size() {
        int count = 0;
        Node<T> current = head;
        while (current != null) {
            count++;
            current = current.next;
        }
        return count;
    }

    // converts the list to a standard Java List
    public List<T> toList() {
        List<T> result = new ArrayList<>();
        Node<T> current = head;
        while (current != null) {
            result.add(current.data);
            current = current.next;
        }
        return result;
    }

    // returns a new CustomLinkedList containing only elements that match the given condition
    public CustomLinkedList<T> filter(Predicate<T> condition) {
        CustomLinkedList<T> result = new CustomLinkedList<>();
        Node<T> current = head;
        while (current != null) {
            if (condition.test(current.data)) {
                result.add(current.data);
            }
            current = current.next;
        }
        return result;
    }

    // enables foreach-style iteration over the list
    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private Node<T> current = head;

            @Override
            public boolean hasNext() {
                return current != null;
            }

            @Override
            public T next() {
                T data = current.data;
                current = current.next;
                return data;
            }
        };
    }
}