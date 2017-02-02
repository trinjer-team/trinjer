package com.arturjoshi;

import com.arturjoshi.account.Account;
import com.arturjoshi.account.repository.AccountRepository;
import com.arturjoshi.authentication.AccountDetails;
import com.arturjoshi.authentication.dto.AccountRegistrationDto;
import com.arturjoshi.authentication.token.TokenHandler;
import com.arturjoshi.milestones.Milestone;
import com.arturjoshi.milestones.repository.MilestoneRepository;
import com.arturjoshi.project.Project;
import com.arturjoshi.project.repository.ProjectAccountPermissionRepository;
import com.arturjoshi.project.repository.ProjectAccountProfileRepository;
import com.arturjoshi.project.repository.ProjectRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.After;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.http.MockHttpOutputMessage;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

public abstract class AbstractTest implements TestConst {

    private HttpMessageConverter mappingJackson2HttpMessageConverter;
    private ObjectMapper objectMapper = new ObjectMapper();
    protected MockMvc mockMvc;

    @Autowired
    protected WebApplicationContext webApplicationContext;

    @Autowired
    private TokenHandler tokenHandler;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private MilestoneRepository milestoneRepository;

    @Autowired
    private ProjectAccountPermissionRepository projectAccountPermissionRepository;

    @Autowired
    private ProjectAccountProfileRepository projectAccountProfileRepository;

    @Autowired
    void setConverters(HttpMessageConverter<?>[] converters) {
        this.mappingJackson2HttpMessageConverter = Arrays.asList(converters).stream()
                .filter(hmc -> hmc instanceof MappingJackson2HttpMessageConverter)
                .findAny()
                .orElse(null);
    }

    @Before
    public void setup() {
        this.mockMvc = webAppContextSetup(webApplicationContext).build();
        milestoneRepository.deleteAll();
        projectAccountPermissionRepository.deleteAll();
        projectAccountProfileRepository.deleteAll();
        projectRepository.deleteAll();
        accountRepository.deleteAll();
    }

    @After
    public void clean() {
        milestoneRepository.deleteAll();
        projectAccountPermissionRepository.deleteAll();
        projectAccountProfileRepository.deleteAll();
        projectRepository.deleteAll();
        accountRepository.deleteAll();
    }

    protected String json(Object o) throws IOException {
        MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
        this.mappingJackson2HttpMessageConverter.write(
                o, MediaType.APPLICATION_JSON, mockHttpOutputMessage);
        return mockHttpOutputMessage.getBodyAsString();
    }

    protected Integer getIdFromJson(String json) throws IOException {
        Map<String, Integer> map = objectMapper.readValue(json, HashMap.class);
        return map.get("id");
    }

    protected AccountRegistrationDto getDefaultTestAccount() {
        AccountRegistrationDto account = new AccountRegistrationDto();
        account.setUsername(ACCOUNT_USERNAME);
        account.setEmail(ACCOUNT_EMAIL);
        account.setPassword(ACCOUNT_PASSWORD);
        return account;
    }

    protected AccountRegistrationDto getDefaultTestAccount(String prefix) {
        AccountRegistrationDto account = new AccountRegistrationDto();
        account.setPassword(ACCOUNT_PASSWORD + prefix);
        account.setUsername(ACCOUNT_USERNAME + prefix);
        account.setEmail(ACCOUNT_EMAIL + prefix);
        return account;
    }

    protected Project getDefaultProject() {
        Project project = new Project();
        project.setName(PROJECT_NAME);
        project.setIsVisible(VISIBLE_PROJECT);
        return project;
    }

    protected Milestone getDefaultMilestone(Milestone.MilestoneType milestoneType) {
        Milestone milestone = new Milestone();
        milestone.setDescription(MILESTONE_DESCRIPTION);
        milestone.setType(milestoneType);
        milestone.setStartDate(LocalDate.now());
        milestone.setEndDate(LocalDate.now().plusWeeks(1));
        return milestone;
    }

    protected String createToken(Account account) {
        return tokenHandler.createTokenForUser(new AccountDetails(account));
    }
}
