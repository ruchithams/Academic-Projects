package bintree.composite;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import bintree.BinarySearchTree;

/**
 * This class represents a node in the binary search tree that contains a
 * piece of data.
 */

public class ElementNode<T extends Comparable<T>> extends AbstractNode<T> {
	private T object;
	private Node<T> left;
	private Node<T> right;

	public ElementNode(T object,Node<T> left,Node <T> right,Node<T> parent) {
		super(parent);
		this.object = object;
		this.left = left;
		this.right = right;
	}

	@Override
	public Node<T> insert(T object) {
		int result = this.object.compareTo(object);

		if (result<0){
			//go right
			this.right = this.right.insert(object);
			this.right.setParent(this);
		}
		else if (result>0) {
			//go left
			this.left = this.left.insert(object);
			this.left.setParent(this);
		}
		return this;
	}

	@Override
	public boolean search(T object) {
		int result = this.object.compareTo(object);

		if (result<0){
			//go right
			return this.right.search(object);
		}
		else if (result>0) {
			//go left
			return this.left.search(object);
		}
		return true;
	}

	@Override
	public void inorder(Consumer<T> processor) {
		this.left.inorder(processor);
		processor.accept(this.object);
		this.right.inorder(processor);
	}

	@Override
	public void postorder(Consumer<T> processor) {
		this.left.postorder(processor);
		this.right.postorder(processor);
		processor.accept(this.object);
	}

	@Override
	public void preorder(Consumer<T> processor) {
		processor.accept(this.object);
		this.left.preorder(processor);
		this.right.preorder(processor);
	}

	// added
	@Override
	public BinarySearchTree<T> createBSTCopy(BinarySearchTree<T> bst) {

		if(this.object != null )
		{
			bst.insert(this.object);
			bst = this.left.createBSTCopy(bst);
			bst = this.right.createBSTCopy(bst);
			return bst;
		}
		return bst;
	}


	public boolean checkIfEqual(Node<T> nd)
	{
		if(nd.getClass() == EmptyNode.class)
		{
			return false;
		}
		else
		{
			ElementNode<T> node = (ElementNode<T>) nd;
			if((this.object != null)  && (node.object != null))
			{

				return (this.object.compareTo(node.object)==0) &&	
						(this.left.checkIfEqual(node.left)) &&
						(this.right.checkIfEqual(node.right));
			}
			else if((this.object == null) && (node.object == null))
			{
				return true;
			}
			else
			{
				return false;
			}

		}
	}

	private List<T> getNodesInOrder(Node<T> node)
	{
		List<T> lstNodes = new ArrayList<>();
		if(node.getClass() == ElementNode.class)
		{
			ElementNode<T> nd = (ElementNode<T>)node;
			lstNodes.addAll(getNodesInOrder(nd.left));
			lstNodes.add(nd.object);
			lstNodes.addAll(getNodesInOrder(nd.right));
			return lstNodes;
		}
		return lstNodes;
	}

	@Override
	public BinarySearchTree<T> createBSTBalanced(BinarySearchTree<T> bst) 
	{

		List<T> lstNodes = getNodesInOrder(this);
		return makeBalancedBST(bst,lstNodes, 0, (lstNodes.size()-1));
	}

	private BinarySearchTree<T> makeBalancedBST(BinarySearchTree<T> bst, List<T> lstNodes, int start, int end)
	{
		if(start <= end)
		{
			int mid = (start+end)/2;
			bst.insert(lstNodes.get(mid));
			bst = makeBalancedBST(bst, lstNodes,start, (mid-1));
			bst = makeBalancedBST(bst, lstNodes,( mid + 1), end);
			return bst;

		}
		return bst;
		
	}

	public int heightOfBST()
	{

		int leftHeight = this.left.heightOfBST();
		int rightHeight = this.right.heightOfBST();
		if(leftHeight >= rightHeight)
		{
			return leftHeight + 1;
		}
		else
		{
			return rightHeight + 1;
		}		


	}
}
