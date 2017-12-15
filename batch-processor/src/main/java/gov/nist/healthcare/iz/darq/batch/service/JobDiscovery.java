package gov.nist.healthcare.iz.darq.batch.service;

import java.lang.reflect.Field;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

public class JobDiscovery {
	private final static Field callableInFutureTask;
	@SuppressWarnings("rawtypes")
	private static final Class<? extends Callable> adapterClass;
	private static final Field runnableInAdapter;

	static {
		try {
			callableInFutureTask = FutureTask.class.getDeclaredField("callable");
			callableInFutureTask.setAccessible(true);
			adapterClass = Executors.callable(new Runnable() {
				public void run() {
				}
			}).getClass();
			runnableInAdapter = adapterClass.getDeclaredField("task");
			runnableInAdapter.setAccessible(true);
		} catch (NoSuchFieldException e) {
			throw new ExceptionInInitializerError(e);
		}
	}

	public static Object findRealTask(Runnable task) {
		if (task instanceof FutureTask) {
			try {
				Object callable = callableInFutureTask.get(task);
				if (adapterClass.isInstance(callable)) {
					return runnableInAdapter.get(callable);
				} else {
					return callable;
				}
			} catch (IllegalAccessException e) {
				throw new IllegalStateException(e);
			}
		}
		throw new ClassCastException("Not a FutureTask");
	}
}
