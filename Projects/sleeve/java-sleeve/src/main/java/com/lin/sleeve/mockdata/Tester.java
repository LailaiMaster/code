package com.lin.sleeve.mockdata;

import com.lin.sleeve.model.Spec;
import com.lin.sleeve.util.GenericAndJson;

import org.springframework.boot.context.event.ApplicationReadyEvent;

import java.util.Collections;
import java.util.List;

/**
 * @author Ztiany
 * Email ztiany3@gmail.com
 * Date 2021/1/25 14:29
 */
public class Tester {

    public static void start(ApplicationReadyEvent event) {
        //doTest(event);
        doDataInsert(event);
    }

    private static void doTest(ApplicationReadyEvent event) {
        Spec oSpec = new Spec();
        oSpec.setKey("1");
        oSpec.setKeyId(1L);
        oSpec.setValue("2");
        oSpec.setValueId(2L);
        String json = GenericAndJson.objectToJson(Collections.singletonList(oSpec));

        List<Object> objects = GenericAndJson.jsonToList(json);
        System.out.println(objects);
        System.out.println(objects.get(0).getClass());

        List<Spec> specs = GenericAndJson.jsonToList(json);
        System.out.println(specs);
        System.out.println(specs.get(0).getClass());
    }

    private static void doDataInsert(ApplicationReadyEvent event) {
        event.getApplicationContext().getBean(MockDataSource.class).insertData();
    }

}
