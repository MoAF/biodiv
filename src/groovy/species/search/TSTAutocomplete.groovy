package species.search

import java.util.*;

public class TSTAutocomplete implements Serializable {

	private static final long serialVersionUID = 7526472295622776156L;

	/**
	 * Inserting keys in TST in the order middle,small,big (lexicographic measure)
	 * recursively creates a balanced tree which reduces insertion and search
	 * times significantly.
	 * 
	 * @param tokens
	 *          Sorted list of keys to be inserted in TST.
	 * @param lo
	 *          stores the lower index of current list.
	 * @param hi
	 *          stores the higher index of current list.
	 * @param root
	 *          a reference object to root of TST.
	 */
	public void balancedTree(Object[] tokens, Object[] vals, int lo, int hi,
	TernaryTreeNode root) {
		if (lo > hi) return;
		int mid = (lo + hi) / 2;
		root = insert(root, (String) tokens[mid], vals[mid], 0);
		balancedTree(tokens, vals, lo, mid - 1, root);
		balancedTree(tokens, vals, mid + 1, hi, root);
	}

	/**
	 * Inserts a key in TST creating a series of Binary Search Trees at each node.
	 * The key is actually stored across the eqKid of each node in a successive
	 * manner.
	 * 
	 * @param currentNode
	 *          a reference node where the insertion will take currently.
	 * @param s
	 *          key to be inserted in TST.
	 * @param x
	 *          index of character in key to be inserted currently.
	 * @return currentNode The new reference to root node of TST
	 */
	public TernaryTreeNode insert(TernaryTreeNode currentNode, String s,
	Object val, int x) {
		if (s == null || s.length() <= x) {
			return currentNode;
		}
		if (currentNode == null) {
			TernaryTreeNode newNode = new TernaryTreeNode();
			newNode.splitchar = s.charAt(x);
			currentNode = newNode;
			if (x < s.length() - 1) {
				currentNode.eqKid = insert(currentNode.eqKid, s, val, x + 1);
			} else {
				currentNode.token = s;
				if(val) {
					if(currentNode.val == null) {
						currentNode.val = [];
					}
					currentNode.val.add(val);
				}
				return currentNode;
			}
		} else if (currentNode.splitchar > s.charAt(x)) {
			currentNode.loKid = insert(currentNode.loKid, s, val, x);
		} else if (currentNode.splitchar == s.charAt(x)) {
			if (x < s.length() - 1) {
				currentNode.eqKid = insert(currentNode.eqKid, s, val, x + 1);
			} else {
				currentNode.token = s;
				
				if(val) {
					if(currentNode.val == null) {
						currentNode.val = [];
					}
					currentNode.val.add(val);
				}
				return currentNode;
			}
		} else {
			currentNode.hiKid = insert(currentNode.hiKid, s, val, x);
		}
		return currentNode;
	}

	/**
	 * Auto-completes a given prefix query using Depth-First Search with the end
	 * of prefix as source node each time finding a new leaf to get a complete key
	 * to be added in the suggest list.
	 * 
	 * @param root
	 *          a reference to root node of TST.
	 * @param s
	 *          prefix query to be auto-completed.
	 * @param x
	 *          index of current character to be searched while traversing through
	 *          the prefix in TST.
	 * @return suggest list of auto-completed keys for the given prefix query.
	 */
	public ArrayList<TernaryTreeNode> prefixCompletion(TernaryTreeNode root,
	String s, int x) {

		TernaryTreeNode p = root;
		ArrayList<TernaryTreeNode> suggest = new ArrayList<TernaryTreeNode>();

		while (p != null) {
			if (s.charAt(x) < p.splitchar) {
				p = p.loKid;
			} else if (s.charAt(x) == p.splitchar) {
				if (x == s.length() - 1) {
					break;
				} else {
					x++;
				}
				p = p.eqKid;
			} else {
				p = p.hiKid;
			}
		}

		if (p == null) return suggest;
		if (p.eqKid == null && p.token == null) return suggest;
		if (p.eqKid == null && p.token != null) {
			suggest.add(p);
			return suggest;
		}

		if (p.token != null) {
			suggest.add(p);
		}
		p = p.eqKid;

		Stack<TernaryTreeNode> st = new Stack<TernaryTreeNode>();
		st.push(p);
		while (!st.empty()) {
			TernaryTreeNode top = st.peek();
			st.pop();
			if (top.token != null) {
				suggest.add(top);
			}
			if (top.eqKid != null) {
				st.push(top.eqKid);
			}
			if (top.loKid != null) {
				st.push(top.loKid);
			}
			if (top.hiKid != null) {
				st.push(top.hiKid);
			}
		}
		return suggest;
	}
}