package io.example.config.mapper;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.modelmapper.ModelMapper;
import org.springframework.util.StringUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class AutoMapper extends ModelMapper {

	private static Map<Class, PropertyDescriptor[]> descriptorsMap = new HashMap<Class, PropertyDescriptor[]>();

	public <D> D map(Object originalObject, Object updateObject, Class<D> destinationType) {
		Object resultObject = null;
		try {
			if (originalObject == null || updateObject == null) {
				throw new NullPointerException("A null paramter was passed into updateObject");
			}
			resultObject = updateObject.getClass().newInstance();
			Class orignalClass = originalObject.getClass();
			Class updateClass = updateObject.getClass();
			if (!orignalClass.equals(updateClass)) {
				throw new IllegalArgumentException("Received parameters are not the same type of class, but must be");
			}

			PropertyDescriptor[] descriptors = PropertyUtils.getPropertyDescriptors(orignalClass);
			if (descriptors == null) {
				descriptors = PropertyUtils.getPropertyDescriptors(orignalClass);
				descriptorsMap.put(orignalClass, descriptors);
			}

			for (PropertyDescriptor descriptor : descriptors) {
				if (PropertyUtils.isReadable(originalObject, descriptor.getName()) && PropertyUtils.isWriteable(originalObject,
					descriptor.getName())) {
					Method readMethod = descriptor.getReadMethod();
					Object originalValue = readMethod.invoke(originalObject);
					Object updateValue = readMethod.invoke(updateObject);
					Object resultValue = originalValue;
					if (originalValue == null || originalValue == updateValue) {
						resultValue = updateValue;
					}
					if (originalValue instanceof String) {
						if (StringUtils.isEmpty((String)originalValue)) {
							resultValue = updateValue;
						}
					} else if (originalValue instanceof Number) {
						if (NumberUtils.createNumber((Number)originalValue + "").intValue() == 0) {
							resultValue = updateValue;
						}
					}
					Method writeMethod = descriptor.getWriteMethod();
					writeMethod.invoke(resultObject, resultValue);
				}
			}
		} catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
			e.printStackTrace();
		}
		return destinationType.cast(resultObject);
	}
}