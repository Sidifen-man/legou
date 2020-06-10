package com.legou.search.client;

import com.legou.item.client.ItemClient;
import com.legou.item.dto.CategoryDTO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ItemClientTest {

    @Autowired
    private ItemClient itemClient;

    @Test
    public void test(){
        List<CategoryDTO> list = itemClient.queryCategoryByList(Arrays.asList(1L, 2L, 3L));
        list.forEach(System.out::println);

    }
}
