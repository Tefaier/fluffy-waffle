package com.example.auction.models.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.Fetch;

import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.CascadeType.PERSIST;
import static org.hibernate.annotations.FetchMode.SUBSELECT;

@Entity
@Table(name = "account")
public class User {
  @Column(updatable = false)
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column
  @NotEmpty
  private String login;

  @Column(name = "firstname")
  @NotEmpty
  private String firstName;

  @Column(name = "lastname")
  @NotEmpty
  private String lastName;

  // may be changed later
  @Column(name = "password_hash")
  @NotEmpty
  private String passwordHash;

  @Column
  @NotEmpty
  private String email;

  @Embedded
  @NotNull
  private Money money;

  @OneToMany(mappedBy = "user", orphanRemoval = true, fetch = FetchType.LAZY, cascade = {PERSIST})
  @Fetch(SUBSELECT)
  private List<Lot> lots = new ArrayList<>();

  @OneToMany(mappedBy = "user", orphanRemoval = true, fetch = FetchType.LAZY, cascade = {PERSIST})
  @Fetch(SUBSELECT)
  private List<Bet> bets = new ArrayList<>();

  protected User() {
  }

  public User(Long id, String login, String firstName, String lastName, String passwordHash, String email, Money money, List<Lot> lots, List<Bet> bets) {
    this.id = id;
    this.login = login;
    this.firstName = firstName;
    this.lastName = lastName;
    this.passwordHash = passwordHash;
    this.email = email;
    this.money = money;
    this.lots = lots;
    this.bets = bets;
  }

  public Long getId() {
    return id;
  }

  protected void setId(Long id) {
    this.id = id;
  }

  public String getLogin() {
    return login;
  }

  public void setLogin(String login) {
    this.login = login;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public String getPasswordHash() {
    return passwordHash;
  }

  public void setPasswordHash(String passwordHash) {
    this.passwordHash = passwordHash;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public Money getMoney() {
    return money;
  }

  public void setMoney(Money money) {
    this.money = money;
  }

  public List<Lot> getLots() {
    return lots;
  }

  public void setLots(List<Lot> lots) {
    this.lots = lots;
  }

  public List<Bet> getBets() {
    return bets;
  }

  public void setBets(List<Bet> bets) {
    this.bets = bets;
  }
}
