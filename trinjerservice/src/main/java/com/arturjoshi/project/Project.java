package com.arturjoshi.project;

import com.arturjoshi.account.Account;
import com.arturjoshi.project.entities.ProjectAccountPermission;
import com.arturjoshi.project.entities.ProjectAccountProfile;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by arturjoshi on 07-Jan-17.
 */
@NoArgsConstructor
@Data
@Entity
@EqualsAndHashCode(of = {"name", "isVisible"})
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Boolean isVisible = true;

    @ManyToOne
    private Account projectOwner;

    @ManyToMany
    @JsonIgnore
    private Set<Account> members = new HashSet<>();

    @ManyToMany
    @JsonIgnore
    private Set<Account> outboxInvitations = new HashSet<>();

    @ManyToMany
    @JsonIgnore
    private Set<Account> inboxInvitations = new HashSet<>();

    @OneToMany(mappedBy = "project")
    @JsonIgnore
    private Set<ProjectAccountProfile> projectAccountProfiles = new HashSet<>();

    @OneToMany(mappedBy = "project")
    @JsonIgnore
    private Set<ProjectAccountPermission> projectAccountPermissions = new HashSet<>();
}
