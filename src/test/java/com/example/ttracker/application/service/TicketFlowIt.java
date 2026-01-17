package com.example.ttracker.application.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class TicketFlowIt extends MySqlTestcontainerBase{
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper om;
    @Test
    void register_login_createTicket_success() throws Exception{
       mvc.perform(post("/auth/register")
           .contentType(MediaType.APPLICATION_JSON)
           .content("""
               {"email":"it_user@test.com","password":"pass123"}
               """))
           .andExpect(status().isOk());

       String loginResponse=mvc.perform(post("/auth/login")
           .contentType(MediaType.APPLICATION_JSON)
           .content("""
               {"email":"it_user@test.com","password":"pass123"}
               """))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.token").exists())
           .andReturn()
           .getResponse()
           .getContentAsString();

       String token=om.readTree(loginResponse).get("token").asText();

       mvc.perform(post("/tickets")
               .header("Authorization", "Bearer " + token)
           .contentType(MediaType.APPLICATION_JSON)
           .content("""
                {"title":"Hello","description":"World"}
               """))
           .andExpect(status().isOk());

    }

}
