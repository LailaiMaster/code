package com.lin.sleeve.vo;

import com.github.dozermapper.core.DozerBeanMapperBuilder;
import com.github.dozermapper.core.Mapper;

import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Ztiany
 * Email ztiany3@gmail.com
 * Date 2021/1/23 16:08
 */
public class PagingDozer<T,K> extends Paging{

    public PagingDozer(Page<T> page,Class<K> clazz) {
        this.initPageParameters(page);
        List<T> content = page.getContent();
        Mapper mapper = DozerBeanMapperBuilder.buildDefault();
        List<K> list = new ArrayList<>();
        content.forEach(spu -> list.add(mapper.map(spu, clazz)));
       this.setItems(list);
    }

}
