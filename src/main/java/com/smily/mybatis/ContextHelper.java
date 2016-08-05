package com.smily.mybatis;

import java.lang.annotation.Annotation;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.Callable;

import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class ContextHelper {
	public static AnnotationConfigApplicationContext run(Class<?> clazz) throws Exception {
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(new Class[] { clazz });
		if (!(context.isActive())) {
			context.refresh();
		}
		return context;
	}

	public static <T> T call(Class<? extends Callable<T>> clazz, String[] args) throws Exception {
		ConfigurableApplicationContext context = new AnnotationConfigApplicationContext(new Class[] { clazz });
		Throwable localThrowable2 = null;
		try {
			Callable callable = context.getBean(clazz);
			if (callable instanceof ArgumentsAware) {
				((ArgumentsAware) callable).setArguments(args);
			}
			Object localObject1 = callable.call();
			return (T) localObject1;//做了修改
		} catch (Throwable localThrowable1) {
		} finally {
			if (context != null)
				if (localThrowable2 != null)
					try {
						context.close();
					} catch (Throwable x2) {
						localThrowable2.addSuppressed(x2);
					}
				else
					context.close();
		}
		return null;//做了修改
	}

	public static <T> Map<String, T> getBeans(ListableBeanFactory beanFactory, Class<T> clazz,
			Class<? extends Annotation> annotation) {
		Map map = beanFactory.getBeansOfType(clazz);

		if ((annotation != null) && (map != null)) {
			Iterator iterator = map.entrySet().iterator();
			while (iterator.hasNext()) {
				Map.Entry entry = (Map.Entry) iterator.next();
				if (!(entry.getValue().getClass().isAnnotationPresent(annotation))) {
					iterator.remove();
				}
			}
		}

		return map;
	}
}