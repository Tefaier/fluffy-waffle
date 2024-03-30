package com.example.auction.models.entities;

import com.example.auction.models.enums.LotState;
import io.hypersistence.utils.hibernate.type.array.StringArrayType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.Type;

import java.sql.Time;

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

  @NotNull
  @Embedded
  @AttributeOverrides({
      @AttributeOverride(name = "value", column = @Column(name = "initial_price_value")),
      @AttributeOverride(name = "valueDecimal", column = @Column(name = "initial_price_value_decimal")),
      @AttributeOverride(name = "currency", column = @Column(name = "initial_price_currency"))
  })
  private Money initialPrice;

  @NotNull
  @Embedded
  @AttributeOverrides({
      @AttributeOverride(name = "value", column = @Column(name = "minimum_price_value")),
      @AttributeOverride(name = "valueDecimal", column = @Column(name = "minimum_price_value_decimal")),
      @AttributeOverride(name = "currency", column = @Column(name = "minimum_price_currency"))
  })
  private Money minimumIncrease;

  @Column(name = "start_time")
  @NotNull
  private Time startTime;

  @Column(name = "finish_time")
  @NotNull
  private Time finishTime;

  @Column
  @NotNull
  private String description;

  @Column(columnDefinition = "text[]")
  @Type(StringArrayType.class)
  @NotNull
  private String[] images;

  @Column(name = "state")
  @Enumerated(EnumType.STRING)
  @NotNull
  private LotState lotState;

  protected Lot() {
  }

  public Lot(Long id, User user, Money initialPrice, Money minimumIncrease, Time startTime, Time finishTime, String description, @NotNull String[] images, LotState lotState) {
    this.id = id;
    this.user = user;
    this.initialPrice = initialPrice;
    this.minimumIncrease = minimumIncrease;
    this.startTime = startTime;
    this.finishTime = finishTime;
    this.description = description;
    this.images = images;
    this.lotState = lotState;
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

  public Time getStartTime() {
    return startTime;
  }

  public void setStartTime(Time startTime) {
    this.startTime = startTime;
  }

  public Time getFinishTime() {
    return finishTime;
  }

  public void setFinishTime(Time finishTime) {
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
}
