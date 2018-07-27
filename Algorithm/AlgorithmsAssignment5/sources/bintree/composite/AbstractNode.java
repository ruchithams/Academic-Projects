package bintree.composite;

/**
 * This class represents an abstract node for the binary search tree
 */

public abstract class AbstractNode<T extends Comparable<T>> implements Node<T> 
{
	
  protected Node<T> parent;

  public AbstractNode(Node<T> parent) 
  {
    this.parent = parent;
  }

  @Override
  public Node<T> getParent() 
  {
    return parent;
  }
  
  @Override
  public void setParent(Node<T> p) 
  {
    this.parent = p;
  }
}
