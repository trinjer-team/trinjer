package com.arturjoshi.account;

import com.arturjoshi.AbstractTest;
import com.arturjoshi.authentication.controller.AuthenticationController;
import com.arturjoshi.authentication.dto.AccountRegistrationDto;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by arturjoshi on 08-Jan-17.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class AccountTest extends AbstractTest {

    @Test
    public void registerNewAccountTest() throws Exception {
        AccountRegistrationDto account = getDefaultTestAccount();

        MvcResult accountMvcResult = mockMvc.perform(post("/api/register/")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(this.json(account)))
                .andExpect(status().isOk())
                .andReturn();

        Integer accountId = getIdFromJson(accountMvcResult.getResponse().getContentAsString());

        mockMvc.perform(get("/api/accounts/" + accountId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(accountId)))
                .andExpect(jsonPath("$.username", is(ACCOUNT_USERNAME)))
                .andExpect(jsonPath("$.email", is(ACCOUNT_EMAIL)));
    }

    @Test
    public void updateAccountUsernameTest() throws Exception {
        AccountRegistrationDto account = getDefaultTestAccount();

        MvcResult accountMvcResult = mockMvc.perform(post("/api/register/")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(this.json(account)))
                .andExpect(status().isOk())
                .andReturn();

        Integer accountId = getIdFromJson(accountMvcResult.getResponse().getContentAsString());

        mockMvc.perform(get("/api/accounts/" + accountId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(accountId)))
                .andExpect(jsonPath("$.username", is(ACCOUNT_USERNAME)))
                .andExpect(jsonPath("$.email", is(ACCOUNT_EMAIL)));

        String accountToken = createToken(account.getAccountFromDto());
        account.setUsername(account.getUsername() + TMP_SUFFIX);

        mockMvc.perform(patch("/api/" + accountId + "/updateAccount/username")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(this.json(account))
                .header(X_AUTH_TOKEN_HEADER, accountToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is(ACCOUNT_USERNAME + TMP_SUFFIX)));

        mockMvc.perform(post("/api/authenticate/")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(this.json(account)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.account.username", is(ACCOUNT_USERNAME + TMP_SUFFIX)));
    }

    @Test
    public void updateAccountEmailTest() throws Exception {
        AccountRegistrationDto account = getDefaultTestAccount();

        MvcResult accountMvcResult = mockMvc.perform(post("/api/register/")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(this.json(account)))
                .andExpect(status().isOk())
                .andReturn();

        Integer accountId = getIdFromJson(accountMvcResult.getResponse().getContentAsString());

        mockMvc.perform(get("/api/accounts/" + accountId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(accountId)))
                .andExpect(jsonPath("$.username", is(ACCOUNT_USERNAME)))
                .andExpect(jsonPath("$.email", is(ACCOUNT_EMAIL)));

        String accountToken = createToken(account.getAccountFromDto());
        account.setEmail(account.getEmail() + TMP_SUFFIX);

        mockMvc.perform(patch("/api/" + accountId + "/updateAccount/email")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(this.json(account))
                .header(X_AUTH_TOKEN_HEADER, accountToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email", is(ACCOUNT_EMAIL + TMP_SUFFIX)));

        mockMvc.perform(post("/api/authenticate/")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(this.json(account)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.account.email", is(ACCOUNT_EMAIL + TMP_SUFFIX)));
    }

    @Test
    public void updateAccountPasswordTest() throws Exception {
        AccountRegistrationDto account = getDefaultTestAccount();

        MvcResult accountMvcResult = mockMvc.perform(post("/api/register/")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(this.json(account)))
                .andExpect(status().isOk())
                .andReturn();

        Integer accountId = getIdFromJson(accountMvcResult.getResponse().getContentAsString());

        mockMvc.perform(get("/api/accounts/" + accountId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(accountId)))
                .andExpect(jsonPath("$.username", is(ACCOUNT_USERNAME)))
                .andExpect(jsonPath("$.email", is(ACCOUNT_EMAIL)));

        String accountToken = createToken(account.getAccountFromDto());
        account.setPassword(account.getPassword() + TMP_SUFFIX);

        mockMvc.perform(patch("/api/" + accountId + "/updateAccount/password")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(this.json(account))
                .header(X_AUTH_TOKEN_HEADER, accountToken))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/authenticate/")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(this.json(account)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", notNullValue()));
    }

    @Test
    public void registerWithExistingEmailTest() throws Exception {
        AccountRegistrationDto account = getDefaultTestAccount();

        mockMvc.perform(post("/api/register/")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(this.json(account)))
                .andExpect(status().isOk());

        account.setUsername(ACCOUNT_USERNAME + "tmp");
        mockMvc.perform(post("/api/register/")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(this.json(account)))
                .andExpect(status().is5xxServerError())
                .andExpect(jsonPath("$",
                        is(AuthenticationController.AuthenticationControllerConstants.ACCOUNT_EMAIL_EXISTS)));

    }

    @Test
    public void registerWithExistingUsernameTest() throws Exception {
        AccountRegistrationDto account = getDefaultTestAccount();

        mockMvc.perform(post("/api/register/")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(this.json(account)))
                .andExpect(status().isOk());

        account.setEmail(ACCOUNT_EMAIL + "tmp");
        mockMvc.perform(post("/api/register/")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(this.json(account)))
                .andExpect(status().is5xxServerError())
                .andExpect(jsonPath("$",
                        is(AuthenticationController.AuthenticationControllerConstants.ACCOUNT_USERNAME_EXISTS)));

    }

    @Test
    public void authenticateTest() throws Exception {
        AccountRegistrationDto account = getDefaultTestAccount();
        mockMvc.perform(post("/api/register/")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(this.json(account)))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/authenticate/")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(this.json(account)))
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(jsonPath("$",
                        not(AuthenticationController.AuthenticationControllerConstants.BAD_CREDENTIALS)))
                .andExpect(jsonPath("$",
                        not(AuthenticationController.AuthenticationControllerConstants.NO_SUCH_ACCOUNT)))
                .andExpect(status().isOk());
    }

    @Test
    public void authenticateBadCredentialsTest() throws Exception {
        AccountRegistrationDto account = getDefaultTestAccount();
        mockMvc.perform(post("/api/register/")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(this.json(account)))
                .andExpect(status().isOk());

        AccountRegistrationDto wrongAccount = getDefaultTestAccount();
        wrongAccount.setPassword(ACCOUNT_PASSWORD + TMP_SUFFIX);
        mockMvc.perform(post("/api/authenticate/")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(this.json(wrongAccount)))
                .andExpect(jsonPath("$",
                        is(AuthenticationController.AuthenticationControllerConstants.BAD_CREDENTIALS)))
                .andExpect(status().is5xxServerError());
    }

    @Test
    public void authenticateNonExistingAccountCredentialsTest() throws Exception {
        AccountRegistrationDto account = getDefaultTestAccount();
        mockMvc.perform(post("/api/register/")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(this.json(account)))
                .andExpect(status().isOk());

        account.setUsername(ACCOUNT_USERNAME + "tmp");
        mockMvc.perform(post("/api/authenticate/")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(this.json(account)))
                .andExpect(jsonPath("$",
                        is(AuthenticationController.AuthenticationControllerConstants.NO_SUCH_ACCOUNT)))
                .andExpect(status().is5xxServerError());
    }

    @Test
    public void searchByEmailTest() throws Exception {
        AccountRegistrationDto account = getDefaultTestAccount();

        MvcResult accountMvcResult = mockMvc.perform(post("/api/register/")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(this.json(account)))
                .andExpect(status().isOk())
                .andReturn();

        Integer accountId = getIdFromJson(accountMvcResult.getResponse().getContentAsString());

        mockMvc.perform(get("/api/accounts/" + accountId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(accountId)))
                .andExpect(jsonPath("$.username", is(ACCOUNT_USERNAME)))
                .andExpect(jsonPath("$.email", is(ACCOUNT_EMAIL)));

        mockMvc.perform(get("/api/accounts/search/searchByEmail?email=" + ACCOUNT_EMAIL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.accounts[0].id", is(accountId)))
                .andExpect(jsonPath("$._embedded.accounts[0].username", is(ACCOUNT_USERNAME)))
                .andExpect(jsonPath("$._embedded.accounts[0].email", is(ACCOUNT_EMAIL)));
    }

    @Test
    public void searchByUsernameTest() throws Exception {
        AccountRegistrationDto account = getDefaultTestAccount();

        MvcResult accountMvcResult = mockMvc.perform(post("/api/register/")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(this.json(account)))
                .andExpect(status().isOk())
                .andReturn();

        Integer accountId = getIdFromJson(accountMvcResult.getResponse().getContentAsString());

        mockMvc.perform(get("/api/accounts/" + accountId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(accountId)))
                .andExpect(jsonPath("$.username", is(ACCOUNT_USERNAME)))
                .andExpect(jsonPath("$.email", is(ACCOUNT_EMAIL)));

        mockMvc.perform(get("/api/accounts/search/searchByUsername?username=" + ACCOUNT_USERNAME))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.accounts[0].id", is(accountId)))
                .andExpect(jsonPath("$._embedded.accounts[0].username", is(ACCOUNT_USERNAME)))
                .andExpect(jsonPath("$._embedded.accounts[0].email", is(ACCOUNT_EMAIL)));
    }
}
