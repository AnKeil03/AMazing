package com.seProject.groupProject7;


import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.seProject.groupProject7.login.Cryptography;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class DatabaseAppTests {
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void shouldReturnDefaultMessage() throws Exception {
        this.mockMvc.perform(get("/user/all")).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("id")))
                .andExpect(content().string(containsString("name")))
                .andExpect(content().string(containsString("email")));
    }

    @Test
    public void testCookiesByHTTP() throws Exception {
        this.mockMvc.perform(get("/user/add?user=jkljkljkljkljkl&" +
               "pass="+Cryptography.cipherMatchJS("poingasdflkj") +
                "&email=me@you.com")).andDo(print());
        this.mockMvc.perform(get("/user/get?name=jkljkljkljkljkl")).andDo(print());
        this.mockMvc.perform(get("/user/checkLogin?cval=-1&user=jkljkljkljkljkl&" +
                "pass="+Cryptography.cipherMatchJS("poingasdflkj")))
                .andDo(print())
                .andExpect(content().string(containsString("loginsuccess")));
        this.mockMvc.perform(delete("/user/removeUser?"+"username=jkljkljkljkljkl"))
                .andDo(print())
                .andExpect(status().is(200));

    }


}
