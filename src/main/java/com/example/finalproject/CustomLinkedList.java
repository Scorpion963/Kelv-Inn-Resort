package com.example.finalproject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

public class CustomLinkedList<T> implements Iterable<T> {
    private Node<T> head;

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

    public CustomLinkedList(CustomLinkedList<T> other) {
        if (other == null) return;
        Node<T> current = other.head;
        while (current != null) {
            this.add(current.data);
            current = current.next;
        }
    }

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

    public int size() {
        int count = 0;
        Node<T> current = head;
        while (current != null) {
            count++;
            current = current.next;
        }
        return count;
    }

    public List<T> toList() {
        List<T> result = new ArrayList<>();
        Node<T> current = head;
        while (current != null) {
            result.add(current.data);
            current = current.next;
        }
        return result;
    }

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