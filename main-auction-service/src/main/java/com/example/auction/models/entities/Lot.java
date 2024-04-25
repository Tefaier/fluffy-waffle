package com.example.auction.models.entities;

import com.example.auction.models.enums.LotState;
import io.hypersistence.utils.hibernate.type.array.StringArrayType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.Type;
import org.hibernate.proxy.HibernateProxy;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static jakarta.persistence.CascadeType.PERSIST;
import static org.hibernate.annotations.FetchMode.SUBSELECT;

@Entity
@Table(name = "lot")
public class Lot {
  @Column(updatable = false)
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @JoinColumn(name = "lot_dealer")
  @ManyToOne(fetch = FetchType.LAZY)
  private User user;

  @Embedded
  @AttributeOverrides({
      @AttributeOverride(name = "integerPart", column = @Column(name = "initial_price_value")),
      @AttributeOverride(name = "decimalPart", column = @Column(name = "initial_price_value_decimal")),
      @AttributeOverride(name = "currency", column = @Column(name = "initial_price_currency"))
  })
  private Money initialPrice;

  @Embedded
  @AttributeOverrides({
      @AttributeOverride(name = "integerPart", column = @Column(name = "minimum_price_value")),
      @AttributeOverride(name = "decimalPart", column = @Column(name = "minimum_price_value_decimal")),
      @AttributeOverride(name = "currency", column = @Column(name = "minimum_price_currency"))
  })
  private Money minimumIncrease;

  @Column(name = "start_time")
  private Timestamp startTime;

  @Column(name = "finish_time")
  private Timestamp finishTime;

  @Column
  private String description;

  @Column(columnDefinition = "text[]")
  @Type(StringArrayType.class)
  private String[] images;

  @Column(name = "state")
  @Enumerated(EnumType.STRING)
  private LotState lotState;

  @OneToMany(mappedBy = "lot", orphanRemoval = true, fetch = FetchType.LAZY, cascade = {PERSIST})
  @Fetch(SUBSELECT)
  private List<Bet> lotBets = new ArrayList<>();

  protected Lot() {
  }

  public Lot(Long id, User user, Money initialPrice, Money minimumIncrease, Timestamp startTime, Timestamp finishTime, String description, @NotNull String[] images, LotState lotState, List<Bet> lotBets) {
    this.id = id;
    this.user = user;
    this.initialPrice = initialPrice;
    this.minimumIncrease = minimumIncrease;
    this.startTime = startTime;
    this.finishTime = finishTime;
    this.description = description;
    this.images = images;
    this.lotState = lotState;
    this.lotBets = lotBets;
  }

  public Lot(User user, Money initialPrice, Money minimumIncrease, Timestamp startTime, Timestamp finishTime, String description, @NotNull String[] images) {
    this.user = user;
    this.initialPrice = initialPrice;
    this.minimumIncrease = minimumIncrease;
    this.startTime = startTime;
    this.finishTime = finishTime;
    this.description = description;
    this.images = images;
    this.lotState = LotState.NOT_SOLD;
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

  public Money getInitialPrice() {
    return initialPrice;
  }

  public void setInitialPrice(Money initialPrice) {
    this.initialPrice = initialPrice;
  }

  public Money getMinimumIncrease() {
    return minimumIncrease;
  }

  public void setMinimumIncrease(Money minimumIncrease) {
    this.minimumIncrease = minimumIncrease;
  }

  public Timestamp getStartTime() {
    return startTime;
  }

  public void setStartTime(Timestamp startTime) {
    this.startTime = startTime;
  }

  public Timestamp getFinishTime() {
    return finishTime;
  }

  public void setFinishTime(Timestamp finishTime) {
    this.finishTime = finishTime;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String[] getImages() {
    return images;
  }

  public void setImages(String[] images) {
    this.images = images;
  }

  public LotState getLotState() {
    return lotState;
  }

  public void setLotState(LotState lotState) {
    this.lotState = lotState;
  }

  public List<Bet> getLotBets() {
    return lotBets;
  }

  public void setLotBets(List<Bet> lotBets) {
    this.lotBets = lotBets;
  }
  public void addLotBet(Bet bet) {
    lotBets.add(bet);
  }

  @Override
  public final boolean equals(Object o) {
    if (this == o) return true;
    if (o == null) return false;
    Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
    Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
    if (thisEffectiveClass != oEffectiveClass) return false;
    Lot lot = (Lot) o;
    return getId() != null && Objects.equals(getId(), lot.getId());
  }

  @Override
  public final int hashCode() {
    return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
  }
}
