package singlenode;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import bintree.BinarySearchTree;

/**
 * This implementation of the BinarySearchTree uses a normal one-type Node,
 * with possibly null children
 */

public class RoutineBinarySearchTree<T extends Comparable<T>> implements
BinarySearchTree<T> {

	private BSTreeNode<T> root;

	public RoutineBinarySearchTree() {
		root = null;
	}
	@Override
	public void insert(T object) {

		BSTreeNode<T> current,previous;

		current = root;
		previous = null;
		while (current!=null) {
			int result = current.getData().compareTo(object);
			previous = current;
			if (result==0) {
				return; //already present
			}
			else if (result<0) {
				current = current.getRight();
			}
			else {
				current = current.getLeft();
			}
		}

		current = new BSTreeNode<T>(object,null,null);
		if (previous==null) {
			root = current;
		}
		else {
			if (previous.getData().compareTo(object)<0) {
				previous.setRight(current);
			}
			else {
				previous.setLeft(current);
			}
		}
	}

	@Override
	public boolean find(T object) {
		BSTreeNode<T> current;

		current = root;
		while (current!=null) {
			int result = current.getData().compareTo(object);
			if (result==0) {
				return true; //found
			}
			else if (result<0) {
				current = current.getRight();
			}
			else {
				current = current.getLeft();
			}
		}
		return false;
	}

	@Override
	public void inorder(Consumer<T> processor) {
		inOrderRec(root,processor);
	}

	@Override
	public void postorder(Consumer<T> processor) {
		postOrderRec(root,processor);
	}

	@Override
	public void preorder(Consumer<T> processor) {
		preOrderRec(root,processor);
	}

	private void preOrderRec(BSTreeNode<T> root,Consumer<T> processor) {
		if (root!=null) {
			processor.accept(root.getData());
			preOrderRec(root.getLeft(),processor);
			preOrderRec(root.getRight(),processor);
		}
	}

	private void inOrderRec(BSTreeNode<T> root,Consumer<T> processor) {
		if (root!=null) {
			inOrderRec(root.getLeft(),processor);
			processor.accept(root.getData());
			inOrderRec(root.getRight(),processor);
		}
	}

	private void postOrderRec(BSTreeNode<T> root,Consumer<T> processor) {
		if (root!=null) {
			postOrderRec(root.getLeft(),processor);
			postOrderRec(root.getRight(),processor);
			processor.accept(root.getData());
		}
	}


	public BinarySearchTree<T> createACopyOFBSTree()
	{
		return copyOfBinarySearchTree(this.root,new RoutineBinarySearchTree<T>());
	}

	private BinarySearchTree<T> copyOfBinarySearchTree(BSTreeNode<T> root, RoutineBinarySearchTree<T> copyTree)
	{
		if(root != null)
		{
			copyTree.insert(root.getData());
			copyTree =  (RoutineBinarySearchTree<T>) copyOfBinarySearchTree(root.getLeft(),copyTree);
			copyTree = (RoutineBinarySearchTree<T>) copyOfBinarySearchTree(root.getRight(),copyTree);
			return copyTree;
		}
		return copyTree;

	}

	private boolean checkIfEqual(BSTreeNode<T> node1,BSTreeNode<T> node2)
	{
		if(node1 != null && node2 != null)
		{
			return (node1.getData().compareTo(node2.getData()) == 0) 
					&& checkIfEqual(node1.getLeft(), node2.getLeft()) 
					&& checkIfEqual(node1.getRight(), node2.getRight());
		}
		else if(node1 == null && node2 == null)
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	@Override
	public boolean isEqual(BinarySearchTree<T> tree)
	{
		RoutineBinarySearchTree<T> tree1 = (RoutineBinarySearchTree<T>) tree;
		return checkIfEqual(this.root, tree1.root); 
	}

	private List<T> fetchTreeNodeInOrder(BSTreeNode<T> root)
	{
		List<T> nodeLst = new ArrayList<>();
		if (root!=null) 
		{
			
			nodeLst.addAll(fetchTreeNodeInOrder(root.getLeft()));
			nodeLst.add((T)root.getData());
			nodeLst.addAll(fetchTreeNodeInOrder(root.getRight()));
			return nodeLst;
		}
		return nodeLst;
	}

	private BinarySearchTree<T> makeBalancedBST(BinarySearchTree<T> balancedBST,List<T> nodesLst, int start, int end)
	{
		if(start <= end)
		{
			int mid = (start+end)/2;
			T nodeVal = nodesLst.get(mid);
			balancedBST.insert(nodeVal);
			balancedBST = makeBalancedBST(balancedBST, nodesLst, start, (mid-1));
			balancedBST = makeBalancedBST(balancedBST, nodesLst, (mid+1) , end);
			return balancedBST;
		}
		return balancedBST;
		
	}

	public BinarySearchTree<T> createBalancedBST()
	{
		List<T> nodeLst = fetchTreeNodeInOrder(this.root);
		
		return makeBalancedBST(new RoutineBinarySearchTree<T>(),nodeLst,0,(nodeLst.size()-1));
	}
	
	@Override
	public int getHeightOfBST() 
	{
		return fetchHeightOfBST(this.root);
	}

	private int fetchHeightOfBST(BSTreeNode<T> node)
	{
		if( node != null)
		{
			int leftTreeHeight = fetchHeightOfBST(node.getLeft());
			int rightTreeHeight = fetchHeightOfBST(node.getRight());
			if(leftTreeHeight > rightTreeHeight)
			{
				return leftTreeHeight + 1;
			}
			else 
			{
				return rightTreeHeight +1;
			}
			
		}
		else
		{
			return 0;
		}
	}
	
	
}
