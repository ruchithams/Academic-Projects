package bintree.composite;

import java.util.function.Consumer;

import bintree.BinarySearchTree;

/**
 * This interface represents a node in the binary search tree
 *
 */
public interface Node<T extends Comparable<T>> {
    
  Node<T> insert(T object);
  boolean search(T object);
  void inorder(Consumer<T> processor);
  void postorder(Consumer<T> processor);
  void preorder(Consumer<T> processor);
  Node<T> getParent();
  void setParent(Node<T> p);
  BinarySearchTree<T> createBSTCopy(BinarySearchTree<T> bst);
  boolean checkIfEqual(Node<T> node);
  BinarySearchTree<T> createBSTBalanced(BinarySearchTree<T> bst);
  int heightOfBST();
}
