/*
 * Copyright (c) 2019 ekino (https://www.ekino.com/)
 */
package com.ekino.oss.jcv.core.initializer;

import com.ekino.oss.jcv.core.validator.ValidatorTemplateManager;
import org.skyscreamer.jsonassert.ValueMatcher;

/**
 * Comparator intializer given tempated validator information.
 *
 * @param <T> the field value type
 *
 * @author Leo Millon
 */
@FunctionalInterface
public interface TemplatedComparatorInitializer<T> {

    /**
     * Init a comparator using the current templated validator info.
     *
     * @param validatorTemplateManager the validator templated manager
     *
     * @return the initalized comparator
     */
    ValueMatcher<T> initComparator(ValidatorTemplateManager validatorTemplateManager);
}
