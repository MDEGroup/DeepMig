/**
 * 
 */
package com.llsfw.core.pagequery;

import java.lang.reflect.Field;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.llsfw.core.exception.SystemException;

/**
 * @author kkll
 *
 */
public class ReflectHelper {

    /**
     * <p>
     * Field log: 日志
     * </p>
     */
    private static final Logger LOG = LogManager.getLogger();

    private ReflectHelper() {

    }

    public static Field getFieldByFieldName(Object obj, String fieldName) {
        for (Class<?> superClass = obj.getClass(); superClass != Object.class; superClass = superClass
                .getSuperclass()) {
            try {
                return superClass.getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
                LOG.trace("ReflectHelper.getFieldByFieldName", e);
            }
        }
        return null;
    }

    public static Object getValueByFieldName(Object obj, String fieldName) throws SystemException {
        try {
            Field field = getFieldByFieldName(obj, fieldName);
            Object value = null;
            if (field != null) {
                if (field.isAccessible()) {
                    value = field.get(obj);
                } else {
                    field.setAccessible(true);
                    value = field.get(obj);
                    field.setAccessible(false);
                }
            }
            return value;
        } catch (Exception e) {
            throw new SystemException(e);
        }
    }

    public static void setValueByFieldName(Object obj, String fieldName, Object value) throws SystemException {
        try {
            Field field = obj.getClass().getDeclaredField(fieldName);
            if (field.isAccessible()) {
                field.set(obj, value);
            } else {
                field.setAccessible(true);
                field.set(obj, value);
                field.setAccessible(false);
            }
        } catch (Exception e) {
            throw new SystemException(e);
        }
    }

}
