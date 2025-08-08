package com.application.QueryGrid.io.Utils;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.beans.PropertyDescriptor;
import java.util.HashSet;
import java.util.Set;

public class PatchUtils {
    public static String[] getNullPropertyNames(Object source){
        final BeanWrapper wrapped = new BeanWrapperImpl(source);

        PropertyDescriptor[] pds = wrapped.getPropertyDescriptors();

        Set<String> nullProps = new HashSet<>();
        for (PropertyDescriptor pd : pds) {
            Object val = wrapped.getPropertyValue(pd.getName());
            if (val == null) {
                nullProps.add(pd.getName());
            }
        }
        return nullProps.toArray(new String[0]);
    }

    public static void copyNonNullProperties(Object src, Object target){
        BeanUtils.copyProperties(src, target, getNullPropertyNames(src));
    }
}
