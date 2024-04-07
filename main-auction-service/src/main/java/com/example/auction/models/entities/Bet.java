package com.example.auction.models.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.proxy.HibernateProxy;

import java.util.Objects;

@Entity
@Table(name = "bet")
public class Bet {
  @Column(updatable = false)
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @JoinColumn(name = "bet_maker")
  @ManyToOne(fetch = FetchType.LAZY)
  private User user;

  @JoinColumn(name = "bet_lot")
  @ManyToOne(fetch = FetchType.LAZY)
  private Lot lot;

  @Embedded
  @NotNull
  private Money value;

  protected Bet() {
  }

  public Bet(Long id, User user, Lot lot, Money value) {
    this.id = id;
    this.user = user;
    this.lot = lot;
    this.value = value;
  }

  public Bet(User user, Lot lot, Money value) {
    this.user = user;
    this.lot = lot;
    this.value = value;
  }

  public Long getId() {
    return id;
  }

  protected void setId(Long id) {
    this.id = id;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public Lot getLot() {
    return lot;
  }

  public void setLot(Lot lot) {
    this.lot = lot;
  }

  public Money getValue() {
    return value;
  }

  public void setValue(Money value) {
    this.value = value;
  }

  @Override
  public final boolean equals(Object o) {
    if (this == o) return true;
    if (o == null) return false;
    Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
    Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
    if (thisEffectiveClass != oEffectiveClass) return false;
    Bet bet = (Bet) o;
    return getId() != null && Objects.equals(getId(), bet.getId());
  }

  @Override
  public final int hashCode() {
    return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
  }
}
