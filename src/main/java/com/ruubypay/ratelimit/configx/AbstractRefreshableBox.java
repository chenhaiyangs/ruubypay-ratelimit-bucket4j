package com.ruubypay.ratelimit.configx;

import com.google.common.base.Preconditions;
import com.ruubypay.framework.configx.AbstractGeneralConfigGroup;
import com.ruubypay.framework.configx.observer.IObserver;

import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 根据属性变化自刷新的容器。
 * 
 * @author chenhaiyang
 *
 */
public abstract class AbstractRefreshableBox<T> implements IObserver {

	/**
	 * 真实对象
	 */
	private T obj;

	/**
	 * 会影响真实对象的属性值，为空时代表任意属性变化都会刷新对象
	 */
	private List<String> propertyKeysCare;

	private AbstractGeneralConfigGroup node;

	private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

	public AbstractRefreshableBox(AbstractGeneralConfigGroup node, List<String> propertyKeysCare) {
		this.node = Preconditions.checkNotNull(node);
		this.propertyKeysCare = propertyKeysCare;
		node.register(this);
		init();
	}

	public AbstractRefreshableBox(AbstractGeneralConfigGroup node) {
		this(node, null);
	}

	private void init() {
		lock.writeLock().lock();
		try {
			obj = doInit(node);
		} finally {
			lock.writeLock().unlock();
		}
	}

	/**
	 * 需要子类实现的配置node
	 * @param node node
	 * @return 返回
	 */
	protected abstract T doInit(AbstractGeneralConfigGroup node);

	protected T getObj() {
		lock.readLock().lock();
		try {
			return obj;
		} finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public void notifyObserver(String data, String value) {
		if (propertyKeysCare == null || propertyKeysCare.isEmpty() || propertyKeysCare.contains(data)) {
			init();
		}
	}
    @Override
    public void notifyObserver() {
        if (propertyKeysCare == null || propertyKeysCare.isEmpty()) {
            init();
        }
    }
}
