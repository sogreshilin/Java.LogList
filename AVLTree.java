import java.util.Comparator;
import java.util.Objects;

public class AVLTree<T> {
    private Node root = null;
    private final Comparator<? super T> comparator;
    protected int modificationCount;

    public AVLTree(Comparator<? super T> comparator) {
        this.comparator = comparator;
    }

    public void add(T value) {
        Objects.requireNonNull(value);
        add(new Node(value));
    }

    public T get(int index){
        Node node = findNodeAtIndex(index);
        return node.value;
    }

    public T remove(int index){
        Node node = findNodeAtIndex(index);
        remove(node);
        return node.value;
    }

    public int size() {
        return (root == null) ? 0 : 1 + root.childrenCount;
    }

    protected Node getRoot() {
        return root;
    }

    protected void add(Node node) {
        if (root == null) {
            root = node;
        } else {
            Node current = root;
            while (true) {
                int compare = node.compareTo(current);
                if (compare < 0) {
                    if (current.leftChild == null) {
                        current.setLeftChild(node);
                        break;
                    } else {
                        current = current.leftChild;
                    }
                } else {
                    if (current.rightChild == null) {
                        current.setRightChild(node);
                        break;
                    } else {
                        current = current.rightChild;
                    }
                }
            }
        }
        modificationCount += 1;
    }

    private Node findNodeAtIndex(int index) {
        if (index < 0 || index >= size()) {
            throw new ArrayIndexOutOfBoundsException(index + " is out of range");
        }

        Node current = root;
        int totalSmallerElements = (current.leftChild == null) ? 0 : current.leftChild.sizeOfSubTree();
        while (true) {
            if (totalSmallerElements == index) {
                break;
            }
            if (totalSmallerElements > index) {
                current = current.leftChild;
                totalSmallerElements -= 1;
                totalSmallerElements -= (current.rightChild == null) ? 0 : current.rightChild.sizeOfSubTree();
            } else {
                totalSmallerElements += 1;
                current = current.rightChild;
                totalSmallerElements += (current.leftChild == null) ? 0 : current.leftChild.sizeOfSubTree();
            }
        }
        return current;
    }

    private Node findFirstNodeWithValue(T value) {
        Node current = root;
        while(current != null) {
            int comparison = comparator.compare(current.value, value);
            if (comparison == 0) {
                while (current.leftChild != null && comparator.compare(current.leftChild.value, value) == 0) {
                    current = current.leftChild;
                }
                break;
            } else if (comparison < 0) {
                current = current.rightChild;
            } else {
                current = current.leftChild;
            }
        }
        return current;
    }

    private void remove(Node node) {
        if (node.isLeaf()) {
            Node parent = node.parent;
            if (parent == null) {
                root = null;
            } else {
                node.detachLeafFromParent();
            }
        } else if (node.hasTwoChildren()) {
            Node successor = node.successor();
            node.value = successor.value;
            remove(successor);
        } else if (node.leftChild != null) {
            node.leftChild.removeParent();
        } else {
            node.rightChild.removeParent();
        }
        modificationCount++;
    }

    @Override
    public String toString() {
        return root.toString();
    }

    protected class Node implements Comparable<Node> {
        private T value;
        private Node leftChild;
        private Node rightChild;
        private Node parent;

        private int height;
        private int childrenCount;

        Node(T value) {
            this.value = value;
        }

        @Override
        public int compareTo(Node other) {
            return comparator.compare(value, other.value);
        }

        private void setLeftChild(Node node) {
            if (node != null) {
                node.parent = this;
            }
            leftChild = node;
            recompute();
            balanceTree();
        }

        private void setRightChild(Node node) {
            if (node != null) {
                node.parent = this;
            }
            rightChild = node;
            recompute();
            balanceTree();
        }

        private int getBalanceFactor() {
            int leftSubTreeHeight = leftChild == null ? -1 : leftChild.height;
            int rightSubTreeHeight = rightChild == null ? -1 : rightChild.height;
            return leftSubTreeHeight - rightSubTreeHeight;
        }

        private void recompute() {
            Node current = this;
            while (current != null) {
                int leftSubTreeHeight = current.leftChild == null ? -1 : current.leftChild.height;
                int rightSubTreeHeight = current.rightChild == null ? -1 : current.rightChild.height;
                current.height = Math.max(leftSubTreeHeight, rightSubTreeHeight) + 1;

                int leftSubTreeChildrenCount = current.leftChild == null ? 0 : current.leftChild.childrenCount + 1;
                int rightSubTreeChildrenCount = current.rightChild == null ? 0 : current.rightChild.childrenCount + 1;
                current.childrenCount = leftSubTreeChildrenCount + rightSubTreeChildrenCount;

                current = current.parent;
            }
        }

        private void balanceTree() {
            Node current = this;
            while (true) {
                int balanceFactor = current.getBalanceFactor();
                if (balanceFactor == -2) {
                    if (current.rightChild.getBalanceFactor() == 1) {
                        current.rightChild.leftChild.rightRotate();
                    }
                    current.rightChild.leftRotate();

                } else if (balanceFactor == 2) {
                    if (current.leftChild.getBalanceFactor() == -1) {
                        current.leftChild.rightChild.leftRotate();
                    }
                    current.leftChild.rightRotate();
                }

                if (current.parent == null) {
                    root = current;
                    break;
                } else {
                    current = current.parent;
                }
            }
        }

        private void leftRotate() {
            Node oldParent = parent;
            Node grandParent = getGrandParent();

            if (grandParent != null) {
                if (parent.isLeftChild()) {
                    grandParent.leftChild = this;
                } else {
                    grandParent.rightChild = this;
                }
            }
            this.parent = grandParent;

            Node oldLeftChild = leftChild;
            oldParent.parent = this;
            leftChild = oldParent;
            if (oldLeftChild != null) {
                oldLeftChild.parent = oldParent;
            }
            oldParent.rightChild = oldLeftChild;

            oldParent.recompute();
        }

        private void rightRotate() {
            Node oldParent = parent;
            Node grandParent = getGrandParent();

            if (grandParent != null) {
                if (parent.isLeftChild()) {
                    grandParent.leftChild = this;
                } else {
                    grandParent.rightChild = this;
                }
            }
            this.parent = grandParent;

            oldParent.parent = this;
            Node oldRightChild = rightChild;
            rightChild = oldParent;
            if (oldRightChild != null) {
                oldRightChild.parent = oldParent;
            }
            oldParent.leftChild = oldRightChild;
            oldParent.recompute();
        }

        private void removeParent() {
            Node grandParent = getGrandParent();
            if (grandParent != null) {
                if (isLeftChild()) {
                    if (parent.isLeftChild()) {
                        grandParent.leftChild = this;
                    } else {
                        grandParent.rightChild = this;
                    }
                    parent = grandParent;
                } else {
                    if (parent.isLeftChild()) {
                        grandParent.leftChild = this;
                    } else {
                        grandParent.rightChild = this;
                    }
                    parent = grandParent;
                }
            } else {
                parent = null;
                root = this;
            }

            recompute();
            balanceTree();
        }

        private Node successor() {
            Node successor = null;
            if (rightChild != null) {
                successor = rightChild.smallestNodeInSubTree();
            } else if (parent != null) {
                Node current = this;
                while (current != null && current.isRightChild()) {
                    current = current.parent;
                }
                successor = current != null ? current.parent : null;
            }
            return successor;
        }

        private Node smallestNodeInSubTree(){
            Node current = this;
            while (true) {
                if(current.leftChild == null) {
                    break;
                } else {
                    current = current.leftChild;
                }
            }
            return current;
        }

        private void detachLeafFromParent() {
            if (isLeftChild()) {
                parent.setLeftChild(null);
            } else {
                parent.setRightChild(null);
            }
        }

        private Node getGrandParent() {
            if (parent == null) {
                return null;
            }
            if (parent.parent == null) {
                return null;
            }
            return parent.parent;
        }

        @Override
        public String toString() {
            String rv = "";
            if (leftChild != null) {
                rv += "(" + leftChild.toString() + ")";
            }
            rv += value;
            if (rightChild != null) {
                rv += "(" + rightChild.toString() + ")";
            }
            return rv;
        }

        private boolean isLeaf() {
            return leftChild == null && rightChild == null;
        }

        protected boolean isLeftChild() {
            return parent != null && parent.leftChild == this;
        }

        private boolean isRightChild() {
            return parent != null && parent.rightChild == this;
        }

        protected int sizeOfSubTree() {
            return 1 + childrenCount;
        }

        private boolean hasTwoChildren() {
            return leftChild != null && rightChild != null;
        }

        protected Node getParent() {
            return parent;
        }

        protected Node getLeftChild() {
            return leftChild;
        }

        protected Node getRightChild() {
            return rightChild;
        }


    }

}
