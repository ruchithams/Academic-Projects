package bintree.composite;

import java.util.function.Consumer;

import bintree.BinarySearchTree;

/**
 * This class represents an empty node in the binary search tree
 */

public class EmptyNode<T extends Comparable<T>> extends AbstractNode<T> {

	public EmptyNode(Node<T> parent) {
		super(parent);

	}


	@Override
	public Node<T> insert(T object) {
		return new ElementNode<T>(object,this,this,this);
	}

	@Override
	public boolean search(T object) {
		return false;
	}

	@Override
	public void inorder(Consumer<T> processor) {

	}

	@Override
	public void postorder(Consumer<T> processor) {

	}

	@Override
	public void preorder(Consumer<T> processor) {
	}


	@Override
	public BinarySearchTree<T> createBSTCopy(BinarySearchTree<T> bst) {
		return bst;
	}


	@Override
	public boolean checkIfEqual(Node<T>  node) {
		return (node.getClass() == EmptyNode.class)?true:false;
	}


	@Override
	public BinarySearchTree<T> createBSTBalanced(BinarySearchTree<T> bst) {
		return bst;
	}
	
	@Override 
	public int heightOfBST()
	{
		return 0;
	}
}
