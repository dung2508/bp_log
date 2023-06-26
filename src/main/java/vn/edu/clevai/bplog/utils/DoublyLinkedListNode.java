package vn.edu.clevai.bplog.utils;

public class DoublyLinkedListNode<T> {
	protected T data;
	protected DoublyLinkedListNode<T> next, prev;

	public DoublyLinkedListNode(T t) {
		next = null;
		prev = null;
		data = t;
	}

	public DoublyLinkedListNode(T d, DoublyLinkedListNode<T> n, DoublyLinkedListNode<T> p) {
		data = d;
		next = n;
		prev = p;
	}

	public void setLinkNext(DoublyLinkedListNode<T> n) {
		next = n;
	}

	public void setLinkPrev(DoublyLinkedListNode<T> p) {
		prev = p;
	}

	public DoublyLinkedListNode<T> getLinkNext() {
		return next;
	}

	public DoublyLinkedListNode<T> getLinkPrev() {
		return prev;
	}

	public void setData(T d) {
		data = d;
	}

	public T getData() {
		return data;
	}
}
