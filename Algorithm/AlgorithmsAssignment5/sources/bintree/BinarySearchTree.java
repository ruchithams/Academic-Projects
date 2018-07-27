package bintree;

import java.util.function.Consumer;

/**
 * This interface contains all the methods necessary for a binary search tree
 * implementation
 */

public interface BinarySearchTree<T extends Comparable<T>> {
  void insert(T object);
  boolean find(T object);
  void inorder(Consumer<T> processor);
  void postorder(Consumer<T> processor);
  void preorder(Consumer<T> processor);
  boolean isEqual(BinarySearchTree<T> tree);
  BinarySearchTree<T> createACopyOFBSTree();
  BinarySearchTree<T> createBalancedBST();
  int getHeightOfBST();
}
