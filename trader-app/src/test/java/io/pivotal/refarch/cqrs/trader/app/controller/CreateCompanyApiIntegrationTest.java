package io.pivotal.refarch.cqrs.trader.app.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jsonSchema.JsonSchemaGenerator;
import io.pivotal.refarch.cqrs.trader.coreapi.company.CompanyId;
import io.pivotal.refarch.cqrs.trader.coreapi.company.CreateCompanyCommand;
import io.pivotal.refarch.cqrs.trader.coreapi.users.UserId;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = CommandController.class)
@ContextConfiguration(classes = {CompanyApiTestConfiguration.class, SecurityOverrideConfig.class})
public class CreateCompanyApiIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private CommandGateway commandGateway;

  @MockBean
  private CompletableFuture<Object> mockCompletableFuture;

  private UUID uuid;
  private CreateCompanyCommand createCompanyCommand;

  @Before
  public void setup(){
    this.uuid = UUID.randomUUID();
    CompanyId companyId = new CompanyId();
    UserId userId = new UserId();
    createCompanyCommand = new CreateCompanyCommand(companyId,userId,"COMPANY-"+uuid.toString(),0,0);
  }

  @Test
  public void shouldCreateCompanyWhenGivenValidCreateCompanyCommand() throws Exception{

    when(commandGateway.send(any()))
            .thenReturn(mockCompletableFuture);

    mockMvc.perform(
            post("/command/CreateCompanyCommand")
              .contentType(MediaType.APPLICATION_JSON)
              .content(asJsonString(createCompanyCommand))
              .accept(MediaType.TEXT_PLAIN)
            ).andDo(print())
            .andExpect(status().isOk());
  }

  public static String asJsonString(final Object obj) {
    try {
      final ObjectMapper mapper = new ObjectMapper();
      final String jsonContent = mapper.writeValueAsString(obj);
      System.out.println(jsonContent);
      return jsonContent;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}

@TestConfiguration
class CompanyApiTestConfiguration {

  @Bean
  public ObjectMapper objectMapper() {
    return new ObjectMapper();
  }

  @Bean
  public JsonSchemaGenerator jsonSchemaGenerator(ObjectMapper objectMapper) {
    return new JsonSchemaGenerator(objectMapper);
  }

}

@TestConfiguration
class SecurityOverrideConfig extends WebSecurityConfigurerAdapter {

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.csrf().disable()
            .authorizeRequests().anyRequest().permitAll();
  }
}


