package singlenode;

public class BSTreeNode<T extends Comparable<T>> {
  private T data;
  private BSTreeNode<T> left,right;

  /**
   * Create a new node with the provided data and left,right subtrees
   * @param data the data to be stored in this node
   * @param left the left subtree
   * @param right the right subtree
   */

  public BSTreeNode(T data,BSTreeNode<T> left,BSTreeNode<T> right) {
    this.data = data;
    this.left = left;
    this.right = right;
  }

  public void setLeft(BSTreeNode<T> left) {
    this.left = left;
  }

  public void setRight(BSTreeNode<T> right) {
    this.right = right;
  }

  public BSTreeNode<T> getLeft() {
    return this.left;
  }

  public BSTreeNode<T> getRight() {
    return this.right;
  }

  public T getData(){
    return this.data;
  }
}
