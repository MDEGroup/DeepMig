package com.llsfw.core.common;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AssignableTypeFilter;

public class ClassUtil {

    private ClassUtil() {

    }

    /**
     * 
     * 获取在指定包下某个class的所有非抽象子类
     * 
     * 
     * 
     * @param parentClass
     * 
     *            父类
     * 
     * @param packagePath
     * 
     *            指定包，格式如"com/iteye/strongzhu"
     * 
     * @return 该父类对应的所有子类列表
     * @throws ClassNotFoundException
     */
    public static <E> List<Class<E>> getSubClasses(final Class<E> parentClass, final String packagePath)
            throws ClassNotFoundException {
        final ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(
                false);
        provider.addIncludeFilter(new AssignableTypeFilter(parentClass));
        final Set<BeanDefinition> components = provider.findCandidateComponents(packagePath);
        final List<Class<E>> subClasses = new ArrayList<>();
        for (final BeanDefinition component : components) {
            @SuppressWarnings("unchecked")
            final Class<E> cls = (Class<E>) Class.forName(component.getBeanClassName());
            if (Modifier.isAbstract(cls.getModifiers())) {
                continue;
            }
            subClasses.add(cls);
        }
        return subClasses;
    }
}
