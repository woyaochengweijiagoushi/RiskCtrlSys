package com.juege.RiskCtrl.api.controller;

import com.juege.RiskCtrlSys.api.Application;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * author: Juege
 * description: 使用 Junit5 测试 Web api
 * date: 2024
 */

/* **********************
 * Junit4需要@RunWith(SpringRunner.class)
 * Junit5用@extendWith(SpringExtension.class)代替了RunWith(),@SpringBootTest已包含了@extendWith()
 *
 * Junit4有@Before, Junit5用@BeforeEach代替
 *
 * *********************/

/* **********************
 *
 * Mock对象：
 * 模拟对象，可以模拟真实对象的行为
 * 如果在单元测试中无法使用真实对象，可以使用Mock对象
 *
 * MockMvc对象：
 * 模拟Http请求，能够对Controller web api进行单元测试
 *
 * *********************/

/* **********************
 * 注意：
 *
 * @SpringBootTest默认去寻找Spring的启动类,
 * 因为模块没有建启动类：也就是没有指明@SpringBootApplication
 * 所以需要告诉@SpringBootTest启动的是ApiTest这个测试类
 *
 * *********************/

//注意，这里必须启动RiskEngine-api的启动类，否则MockMvc请求不到URL
@SpringBootTest(classes = Application.class)
/* **********************
 *
 * @TestMethodOrder作用：
 * 测试方法的优先级,
 * 需要配置@Order使用
 *
 * *********************/
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
/* **********************
 *
 * @AutoConfigureMockMvc作用：
 *
 * 自动配置MockMvc,
 * 需要搭配 @Autowired
 *
 * *********************/
@AutoConfigureMockMvc
public class HelloControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @DisplayName("测试Junit5 Hello World")
    @Test
    @Order(2)
    public void testHello2() {
        System.out.println("juege-hello2");
    }

    @DisplayName("测试方法优先级")
    @Test
    @Order(1)
    public void testHello1() {
        System.out.println("juege-hello1");
    }

    @BeforeEach
    public void setup(){
        System.out.println("====== start =======");
    }

    @DisplayName("测试MockMvc模拟Api调用")
    @Test
    @Order(3)
    public void testMockMvc() throws Exception {
        //构造Request
        MvcResult result = mockMvc.perform(
                post("/hello/test")
                        //设置内容类型
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                //断言
                .andExpect(status().isOk())
                //打印请求信息
                .andDo(print())
                .andReturn();

        //打印接口返回信息
        System.out.println(result.getResponse().getContentAsString());


    }


}
