package ctxfree;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class NodeMap<K, V> implements Cloneable, Iterable<NodeMap.Node<K, V>>
{
    // Requirement 1: Entry nodes with mutable values
    public static class Node<K, V> 
    {
        private final K key;
        private V value;

        Node(K key, V value) 
        {
            this.key = key;
            this.value = value;
        }

        public K getKey() { return key; }
        public V getValue() { return value; }
        public void setValue(V value) { this.value = value; }
    }

    // Structural wrapper to handle map collisions independently of the Node
    private static class EntryChain<K, V> 
    {
        final Node<K, V> node;
        EntryChain<K, V> next;

        EntryChain(Node<K, V> node, EntryChain<K, V> next) 
        {
            this.node = node;
            this.next = next;
        }
    }

    private EntryChain<K, V>[] table;
    private int size;
    private int threshold;
    private static final int INITIAL_CAPACITY = 16;
    private static final float LOAD_FACTOR = 0.75f;

    @SuppressWarnings("unchecked")
    public NodeMap() 
    {
        table = new EntryChain[INITIAL_CAPACITY];
        threshold = (int) (INITIAL_CAPACITY * LOAD_FACTOR);
    }

    private int indexFor(K key, int length) 
    {
        return (key == null) ? 0 : (key.hashCode() & 0x7fffffff) & (length - 1);
    }

    // Requirement 2: Insert, remove, and retrieve entries
    public Node<K, V> put(K key, V value) 
    {
        int i = indexFor(key, table.length);
        
        for (EntryChain<K, V> e = table[i]; e != null; e = e.next) 
        {
            if (e.node.key == key || (key != null && key.equals(e.node.key))) 
            {
                e.node.value = value;
                return e.node;
            }
        }

        Node<K, V> newNode = new Node<>(key, value);
        table[i] = new EntryChain<>(newNode, table[i]);
        
        if (++size >= threshold) 
        {
            resize(table.length * 2);
        }
        return newNode;
    }

    public Node<K, V> get(K key) 
    {
        int i = indexFor(key, table.length);
        for (EntryChain<K, V> e = table[i]; e != null; e = e.next) 
        {
            if (e.node.key == key || (key != null && key.equals(e.node.key))) 
            {
                return e.node;
            }
        }
        return null;
    }

    public Node<K, V> remove(K key) 
    {
        int i = indexFor(key, table.length);
        EntryChain<K, V> prev = null;
        EntryChain<K, V> e = table[i];

        while (e != null) 
        {
            if (e.node.key == key || (key != null && key.equals(e.node.key))) 
            {
                size--;
                if (prev == null) 
                {
                    table[i] = e.next;
                } 
                else 
                {
                    prev.next = e.next;
                }
                return e.node;
            }
            prev = e;
            e = e.next;
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private void resize(int newCapacity) 
    {
        EntryChain<K, V>[] oldTable = table;
        EntryChain<K, V>[] newTable = new EntryChain[newCapacity];

        for (int j = 0; j < oldTable.length; j++) 
        {
            EntryChain<K, V> e = oldTable[j];
            while (e != null) 
            {
                EntryChain<K, V> next = e.next;
                int i = indexFor(e.node.key, newCapacity);
                e.next = newTable[i];
                newTable[i] = e;
                e = next;
            }
        }
        table = newTable;
        threshold = (int) (newCapacity * LOAD_FACTOR);
    }

    // Requirement 3: Clone with shared structural references to the exact same nodes
    @Override
    @SuppressWarnings("unchecked")
    public NodeMap<K, V> clone() 
    {
        try 
        {
            NodeMap<K, V> clone = (NodeMap<K, V>) super.clone();
            clone.table = new EntryChain[table.length];
            
            for (int i = 0; i < table.length; i++) 
            {
                if (table[i] != null) 
                {
                    EntryChain<K, V> original = table[i];
                    EntryChain<K, V> newHead = new EntryChain<>(original.node, null);
                    clone.table[i] = newHead;

                    EntryChain<K, V> currentClone = newHead;
                    while (original.next != null) 
                    {
                        original = original.next;
                        EntryChain<K, V> nextClone = new EntryChain<>(original.node, null);
                        currentClone.next = nextClone;
                        currentClone = nextClone;
                    }
                }
            }
            return clone;
        } 
        catch (CloneNotSupportedException e) 
        {
            throw new AssertionError();
        }
    }

    // Requirement 4: Allow clean iteration over map nodes
    @Override
    public Iterator<Node<K, V>> iterator() 
    {
        return new Iter();
    }

    public void printTable()
    {
        System.out.println("==TABLE==");
        System.out.println("len: " + size);
        System.out.println("cap: " + table.length);
        for (int i = 0; i < table.length; i++)
        {
            if (table[i] == null) continue;
            System.out.print(i + ": ");
            EntryChain<K, V> e = table[i];
            while (e != null) 
            {
                System.out.print(e.node.key + "=" + e.node.value + " -> ");
                e = e.next;
            }
            System.out.println("null");
        }
    }

    private class Iter implements Iterator<Node<K, V>>
    {
        private int bucketIndex = 0;
        private EntryChain<K, V> currentEntry = null;

        private void advance() 
        {
            if (currentEntry != null) 
            {
                currentEntry = currentEntry.next;
            }
            while (currentEntry == null && bucketIndex < table.length) 
            {
                currentEntry = (EntryChain<K, V>) table[bucketIndex++];
            }
        }

        @Override
        public boolean hasNext() 
        {
            if (currentEntry == null) 
            {
                advance();
            }
            return currentEntry != null;
        }

        @Override
        public Node<K, V> next() 
        {
            if (!hasNext()) 
            {
                throw new NoSuchElementException();
            }
            Node<K, V> node = currentEntry.node;
            advance();
            return node;
        }
    }
}
