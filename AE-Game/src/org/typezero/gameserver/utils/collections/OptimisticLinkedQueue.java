/*
 * Copyright (c) 2015, TypeZero Engine (game.developpers.com)
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * Neither the name of TypeZero Engine nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */

package org.typezero.gameserver.utils.collections;

import java.util.AbstractQueue;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

import javax.annotation.concurrent.ThreadSafe;

/**
 * Optimistic approach to lock-free FIFO queue;
 * E. Ladan-Mozes and N. Shavit algorithm, less CAS failures when enqueueing,
 * if compared with Michael and Scott Nonblocking Queue, in ConcurrentLinkedQueue
 */
@ThreadSafe
public class OptimisticLinkedQueue<E> extends AbstractQueue<E> implements Queue<E>, java.io.Serializable {

	private static final long serialVersionUID = -3445502502831420722L;

	private static class Node<E> {

		private volatile E item;
		private volatile Node<E> next;
		private volatile Node<E> prev;

		@SuppressWarnings("unused")
		Node(E x) {
			item = x;
			next = null;
			prev = null;
		}

		Node(E x, Node<E> n) {
			item = x;
			next = n;
			prev = null;
		}

		E getItem() {
			return item;
		}

		@SuppressWarnings("unused")
		void setItem(E val) {
			this.item = val;
		}

		Node<E> getNext() {
			return next;
		}

		void setNext(Node<E> val) {
			next = val;
		}

		Node<E> getPrev() {
			return prev;
		}

		void setPrev(Node<E> val) {
			prev = val;
		}
	}

	@SuppressWarnings("rawtypes")
	private static final AtomicReferenceFieldUpdater<OptimisticLinkedQueue, Node> tailUpdater = AtomicReferenceFieldUpdater
		.newUpdater(OptimisticLinkedQueue.class, Node.class, "tail");
	@SuppressWarnings("rawtypes")
	private static final AtomicReferenceFieldUpdater<OptimisticLinkedQueue, Node> headUpdater = AtomicReferenceFieldUpdater
		.newUpdater(OptimisticLinkedQueue.class, Node.class, "head");

	private boolean casTail(Node<E> cmp, Node<E> val) {
		return tailUpdater.compareAndSet(this, cmp, val);
	}

	private boolean casHead(Node<E> cmp, Node<E> val) {
		return headUpdater.compareAndSet(this, cmp, val);
	}

	/**
	 * Pointer to the head node, initialized to a dummy node. The first actual node is at head.getPrev().
	 */
	private transient volatile Node<E> head = new Node<E>(null, null);
	/**
	 *  Pointer to last node on list
	 */
	private transient volatile Node<E> tail = head;

	/**
	 * Creates a <tt>OptimisticLinkedQueue</tt> that is initially empty.
	 */
	public OptimisticLinkedQueue() {
	}

	AtomicInteger count = new AtomicInteger();

	/**
	 * Enqueues the specified element at the tail of this queue.
	 */
	public boolean offer(E e) {
		if (e == null)
			throw new NullPointerException();
		Node<E> n = new Node<E>(e, null);
		for (;;) {
			Node<E> t = tail;
			n.setNext(t);
			count.incrementAndGet();
			if (casTail(t, n)) {
				t.setPrev(n);
				return true;
			}
		}
	}

	/**
	 * Dequeues an element from the queue. After a successful casHead, the prev and next pointers of the dequeued node are
	 * set to null to allow garbage collection.
	 */
	public E poll() {
		for (;;) {
			Node<E> h = head;
			Node<E> t = tail;
			Node<E> first = h.getPrev();
			if (h == head) {
				if (h != t) {
					if (first == null) {
						fixList(t, h);
						continue;
					}
					E item = first.getItem();
					if (casHead(h, first)) {
						h.setNext(null);
						h.setPrev(null);
						count.decrementAndGet();
						return item;
					}
				}
				else
					return null;
			}
		}
	}

	/**
	 * Fixing the backwards pointers when needed
	 */
	private void fixList(Node<E> t, Node<E> h) {
		Node<E> curNodeNext;
		Node<E> curNode = t;
		while (h == this.head && curNode != h) {
			curNodeNext = curNode.getNext();
			curNodeNext.setPrev(curNode);
			curNode = curNode.getNext();
		}
	}

	public void clear() {
		while (poll() != null);
	}

	public int leaveTail() {
		E elem = null;
		E elem1 = null;
		int removed = 0;
		while ((elem = poll()) != null) {
			elem1 = elem;
			removed++;
		}
		if (elem1 != null) {
			removed--;
			offer(elem1);
		}
		return removed;
	}

	@Override
	public E peek() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Iterator<E> iterator() {
		throw new UnsupportedOperationException();
	}

	@Override
	public int size() {
		return count.get();
	}
}
