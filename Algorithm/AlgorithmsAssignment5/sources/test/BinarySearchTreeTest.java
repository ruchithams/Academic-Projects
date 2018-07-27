package test;
import org.junit.Before;
import org.junit.Test;

import java.util.function.Consumer;

import bintree.BinarySearchTree;
import bintree.composite.BinarySearchTreeImpl;
import singlenode.RoutineBinarySearchTree;

import static org.junit.Assert.*;

public abstract class BinarySearchTreeTest {
	BinarySearchTree<Integer> tree;

	protected abstract BinarySearchTree<Integer> createTree();

	@Before
	public void setup() 
	{
		tree = createTree();
		tree.insert(3);
		tree.insert(4);
		tree.insert(10);
		tree.insert(-4);
		tree.insert(-8);
		tree.insert(5);
		tree.insert(10);
		tree.insert(6);
	}
	@Test
	public void testSearch() 
	{
		assertTrue(tree.find(3));
		assertTrue(tree.find(4));
		assertTrue(tree.find(10));
		assertTrue(tree.find(-4));
		assertTrue(tree.find(-8));
		assertTrue(tree.find(5));
		assertTrue(tree.find(10));
		assertTrue(tree.find(6));
		assertFalse(tree.find(-1));
		assertFalse(tree.find(100));
		assertFalse(tree.find(7));
	}

	@Test
	public void testInorder() 
	{
		OrderPrinter<Integer> pw = new OrderPrinter<Integer>();
		tree.inorder(pw);
		assertEquals("-8 -4 3 4 5 6 10",pw.toString());
	}

	@Test
	public void testPreorder() 
	{
		OrderPrinter<Integer> pw = new OrderPrinter<Integer>();
		tree.preorder(pw);
		assertEquals("3 -4 -8 4 10 5 6",pw.toString());
	}

	@Test
	public void testPostorder() 
	{
		OrderPrinter<Integer> pw = new OrderPrinter<Integer>();
		tree.postorder(pw);
		assertEquals("-8 -4 6 5 10 4 3",pw.toString());
	}

	//added
	@Test
	public void testSingle()
	{

		Integer data1 = new Integer(10);
		Integer data2 = new Integer(20);
		Integer data3 = new Integer(30);
		Integer data4 = new Integer(40);
		Integer data5 = new Integer(50);
		Integer data6 = new Integer(60);
		Integer data7 = new Integer(70);
		Integer data8 = new Integer(80);
		Integer data9 = new Integer(90);


		RoutineBinarySearchTreeTest rObj = new RoutineBinarySearchTreeTest();
		BinarySearchTree<Integer> rTree = rObj.createTree();
		rTree.insert(data1);
		rTree.insert(data2);
		rTree.insert(data3);
		rTree.insert(data4);
		rTree.insert(data5);
		rTree.insert(data6);
		rTree.insert(data7);
		rTree.insert(data8);
		rTree.insert(data9);

		Consumer<Integer> consumer= i-> System.out.print(" "+i);

		System.out.println("skewed tree order");

		rTree.preorder(consumer);
		System.out.println("\n");
		rTree.inorder(consumer);
		System.out.println("\n");
		rTree.postorder(consumer);
		System.out.println("\n");

		// testing skewed tree case insertion in ascendind order
		assertEquals(rTree.createACopyOFBSTree().getHeightOfBST(), rTree.getHeightOfBST());

		// Balanced tree
		BinarySearchTree<Integer> balaTree= rTree.createBalancedBST();
		assertEquals(balaTree.getHeightOfBST(),4);

		System.out.println("balanced tree order");
		balaTree.preorder(consumer);
		System.out.println("\n");
		balaTree.inorder(consumer);
		System.out.println("\n");
		balaTree.postorder(consumer);
		System.out.println("\n");

		// testing empty tree
		BinarySearchTree<Integer> rempty = rObj.createTree();
		assertEquals(rempty.getHeightOfBST(), 0);
		assertEquals(rempty.createBalancedBST().isEqual(rempty), true);
		assertEquals(rempty.createACopyOFBSTree().isEqual(rempty),true);
		assertEquals(rempty.createBalancedBST().getHeightOfBST(),0);
		assertEquals(rempty.createACopyOFBSTree().getHeightOfBST(),0);

		BinarySearchTree<Integer> rTree2 = rObj.createTree();

		rTree2.insert(data5);
		rTree2.insert(data1);
		rTree2.insert(data2);
		rTree2.insert(data3);
		rTree2.insert(data4);
		rTree2.insert(data6);
		rTree2.insert(data7);
		rTree2.insert(data8);
		rTree2.insert(data9);

		System.out.println("rTree2 tree order");
		rTree2.preorder(consumer);
		System.out.println("\n");
		rTree2.inorder(consumer);
		System.out.println("\n");
		rTree2.postorder(consumer);
		System.out.println("\n");
		assertEquals(rTree2.getHeightOfBST(),5);
		assertEquals(rTree2.createBalancedBST().getHeightOfBST(),4);
		assertEquals(rTree2.createACopyOFBSTree().getHeightOfBST(),5);
		assertEquals(rTree2.createACopyOFBSTree().isEqual(rTree2), true);

	}

	@Test
	public void testComposite()
	{

		Integer data1 = new Integer(10);
		Integer data2 = new Integer(20);
		Integer data3 = new Integer(30);
		Integer data4 = new Integer(40);
		Integer data5 = new Integer(50);
		Integer data6 = new Integer(60);
		Integer data7 = new Integer(70);
		Integer data8 = new Integer(80);
		Integer data9 = new Integer(90);


		BinarySearchTreeImplTest rObj = new BinarySearchTreeImplTest();
		BinarySearchTree<Integer> rTree = rObj.createTree();
		rTree.insert(data1);
		rTree.insert(data2);
		rTree.insert(data3);
		rTree.insert(data4);
		rTree.insert(data5);
		rTree.insert(data6);
		rTree.insert(data7);
		rTree.insert(data8);
		rTree.insert(data9);

		Consumer<Integer> consumer= i-> System.out.print(" "+i);

		System.out.println("skewed tree order");

		rTree.preorder(consumer);
		System.out.println("\n");
		rTree.inorder(consumer);
		System.out.println("\n");
		rTree.postorder(consumer);
		System.out.println("\n");

		// testing skewed tree case insertion in ascendind order
		assertEquals(rTree.createACopyOFBSTree().getHeightOfBST(), rTree.getHeightOfBST());

		// Balanced tree
		BinarySearchTree<Integer> balaTree= rTree.createBalancedBST();
		assertEquals(balaTree.getHeightOfBST(),4);

		System.out.println("balanced tree order");
		balaTree.preorder(consumer);
		System.out.println("\n");
		balaTree.inorder(consumer);
		System.out.println("\n");
		balaTree.postorder(consumer);
		System.out.println("\n");

		// testing empty tree
		BinarySearchTree<Integer> rempty = rObj.createTree();
		assertEquals(rempty.getHeightOfBST(), 0);
		assertEquals(rempty.createBalancedBST().isEqual(rempty), true);
		assertEquals(rempty.createACopyOFBSTree().isEqual(rempty),true);
		assertEquals(rempty.createBalancedBST().getHeightOfBST(),0);
		assertEquals(rempty.createACopyOFBSTree().getHeightOfBST(),0);

		BinarySearchTree<Integer> rTree2 = rObj.createTree();

		rTree2.insert(data5);
		rTree2.insert(data1);
		rTree2.insert(data2);
		rTree2.insert(data3);
		rTree2.insert(data4);
		rTree2.insert(data6);
		rTree2.insert(data7);
		rTree2.insert(data8);
		rTree2.insert(data9);

		System.out.println("rTree2 tree order");
		rTree2.preorder(consumer);
		System.out.println("\n");
		rTree2.inorder(consumer);
		System.out.println("\n");
		rTree2.postorder(consumer);
		System.out.println("\n");
		assertEquals(rTree2.getHeightOfBST(),5);
		assertEquals(rTree2.createBalancedBST().getHeightOfBST(),4);
		assertEquals(rTree2.createACopyOFBSTree().getHeightOfBST(),5);
		assertEquals(rTree2.createACopyOFBSTree().isEqual(rTree2), true);

	}


	class OrderPrinter<T> implements Consumer<T> {
		private StringBuilder sb;

		public OrderPrinter() {
			sb = new StringBuilder();
		}

		public void accept(T t) {
			if (sb.toString().length()>0) {
				sb.append(" ");
			}
			sb.append(t.toString());
		}

		public String toString() {
			return sb.toString();
		}
	}

	/**
	 * Tester for BinarySearchTreeImpl
	 */
	public static final class BinarySearchTreeImplTest extends BinarySearchTreeTest {
		@Override
		public BinarySearchTree<Integer> createTree() {
			return new BinarySearchTreeImpl<Integer>();
		}
	}

	/**
	 * Tester for RoutineBinarySearchTree
	 */

	public static final class RoutineBinarySearchTreeTest extends BinarySearchTreeTest 
	{
		@Override
		public BinarySearchTree<Integer> createTree() 
		{
			return new RoutineBinarySearchTree<Integer>();
		}
	}



}

