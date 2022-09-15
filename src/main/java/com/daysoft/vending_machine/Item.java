package com.daysoft.vending_machine;

import java.math.BigDecimal;
import java.math.RoundingMode;
import lombok.Getter;
import lombok.Setter;
import java.util.Objects;

public class Item {

  @Getter
  @Setter
  private BigDecimal price;

  @Getter
  @Setter
  private String name;
  private int hashCode;

  public Item(String name, Double price) {
    this.name = name;
    this.price = BigDecimal.valueOf(price);
    this.hashCode = Objects.hash(name, price);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Item that = (Item) o;
    return name == that.name && price == that.price;
  }

  @Override
  public int hashCode() {
    return this.hashCode;
  }
}
