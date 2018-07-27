package bintree.composite;

import java.util.function.Consumer;

import bintree.BinarySearchTree;


public class BinarySearchTreeImpl<T extends Comparable<T>> implements BinarySearchTree<T> {
	Node<T> root;
	int size;

	public BinarySearchTreeImpl() {
		root = new EmptyNode<T>(null);
		size=0;
	}

	@Override
	public void insert(T object) {
		if (!find(object)) {
			size++;
		}
		root = root.insert(object);
	}

	@Override
	public boolean find(T object) {
		return root.search(object);
	}

	@Override
	public void inorder(Consumer<T> processor) {
		root.inorder(processor);
	}

	@Override
	public void postorder(Consumer<T> processor) {
		root.postorder(processor);
	}

	@Override
	public void preorder(Consumer<T> processor) {
		root.preorder(processor);
	}

	public BinarySearchTree<T> createACopyOFBSTree()
	{

		return root.createBSTCopy(new BinarySearchTreeImpl<>());
	}

	@Override
	public boolean isEqual(BinarySearchTree<T> tree) {
		BinarySearchTreeImpl<T> t = (BinarySearchTreeImpl<T>) tree;
		return this.root.checkIfEqual(t.root);
	}

	@Override
	public BinarySearchTree<T> createBalancedBST() {
		return  this.root.createBSTBalanced(new BinarySearchTreeImpl<T>());
	}

	@Override
	public int getHeightOfBST() {
		return this.root.heightOfBST();
	}
	
	

}
