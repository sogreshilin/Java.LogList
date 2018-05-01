import java.util.LinkedList;
import java.util.Objects;

public class LogList<T> extends AVLTree<T> {
    public LogList() {
        super((a, b) -> 0);
    }

    public T set(int index, T element) {
        T removedElement = remove(index);
        add(index, element);
        return removedElement;
    }

    public void add(int index, T element){
        Objects.requireNonNull(element);
        if (index < 0 || index > size()) {
            throw new ArrayIndexOutOfBoundsException("Index " + index + " is out of bounds");
        }
        add(new MyNode(element, index));
    }

    public void add(T element){
        add(new MyNode(element, size()));
    }

    @Override
    public String toString() {
        StringBuilder rv = new StringBuilder();
        rv.append("[");
        for (int i = 0; i < size() - 1; ++i) {
            rv.append(get(i));
            rv.append(", ");
        }
        rv.append(get(size() - 1));
        rv.append("]");
        return rv.toString();
    }

    private class MyNode extends Node {
        private int indexInList;
        private int modificationCountWhenIndexSet;

        MyNode(T object, int initialIndex){
            super(object);
            indexInList = initialIndex;
            modificationCountWhenIndexSet = modificationCount;
        }

        @Override
        public int compareTo(Node other) {
            MyNode node = (MyNode) other;
            int compare = getIndexInList() - node.getIndexInList();
            if (compare == 0) {
                if (isInTree() && !node.isInTree()) {
                    return 1;
                } else if (!isInTree() && node.isInTree()){
                    return -1;
                }
            }
            return compare;
        }

        private int getIndexInList() {
            LinkedList<MyNode> stack = new LinkedList<>();
            MyNode current = this;
            while (current != null && current.modificationCountWhenIndexSet != modificationCount) {
                stack.push(current);
                current = (MyNode) current.getParent();
            }

            while (!stack.isEmpty()) {
                current = stack.pop();
                MyNode parent = (MyNode) current.getParent();
                if (parent == null) {
                    Node leftChild = current.getLeftChild();
                    current.indexInList = (leftChild == null) ? 0 : leftChild.sizeOfSubTree();
                } else {
                    if (current.isLeftChild()) {
                        Node rightChild = current.getRightChild();
                        current.indexInList = parent.indexInList - (rightChild == null ? 1 : 1 + rightChild.sizeOfSubTree());
                    } else {
                        Node leftChild = current.getLeftChild();
                        current.indexInList = parent.indexInList + (leftChild == null ? 1 : 1 + leftChild.sizeOfSubTree());
                    }
                }
                current.modificationCountWhenIndexSet = modificationCount;
            }
            return indexInList;
        }

        private boolean isInTree() {
            return getParent() != null || getRoot() == this;
        }
    }
}
