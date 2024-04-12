package com.example.auction.models.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.Fetch;
import org.hibernate.proxy.HibernateProxy;

import java.util.*;

import static jakarta.persistence.CascadeType.PERSIST;
import static org.hibernate.annotations.FetchMode.SUBSELECT;

@Entity
@Table(name = "account")
public class User {
  @Column(updatable = false)
  @Id
  private UUID id;

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

  @OneToMany(mappedBy = "user", orphanRemoval = true, fetch = FetchType.LAZY, cascade = {PERSIST})
  @Fetch(SUBSELECT)
  private List<Lot> lots = new ArrayList<>();

  @OneToMany(mappedBy = "user", orphanRemoval = true, fetch = FetchType.LAZY, cascade = {PERSIST})
  @Fetch(SUBSELECT)
  private List<Bet> bets = new ArrayList<>();

  protected User() {
  }

  public User(UUID id, String login, String firstName, String lastName, String passwordHash, String email, List<Lot> lots, List<Bet> bets) {
    this.id = id;
    this.login = login;
    this.firstName = firstName;
    this.lastName = lastName;
    this.passwordHash = passwordHash;
    this.email = email;
    this.lots = lots;
    this.bets = bets;
  }

  public User(String login, String firstName, String lastName, String passwordHash, String email) {
    this.id = UUID.randomUUID();
    this.login = login;
    this.firstName = firstName;
    this.lastName = lastName;
    this.passwordHash = passwordHash;
    this.email = email;
  }

  public UUID getId() {
    return id;
  }

  protected void setId(UUID id) {
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

  public void addLot(Lot lot) {
    lots.add(lot);
  }

  public void addBet(Bet bet) {
    bets.add(bet);
  }

  @Override
  public final boolean equals(Object o) {
    if (this == o) return true;
    if (o == null) return false;
    Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
    Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
    if (thisEffectiveClass != oEffectiveClass) return false;
    User user = (User) o;
    return getId() != null && Objects.equals(getId(), user.getId());
  }

  @Override
  public final int hashCode() {
    return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
  }
}
