package dev.practice.order.domain.item;

import com.google.common.collect.Lists;
import dev.practice.order.common.exception.InvalidParamException;
import dev.practice.order.common.util.TokenGenerator;
import dev.practice.order.domain.AbstractEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.*;
import java.util.List;

@Entity
@NoArgsConstructor
@Table(name = "items")
@Getter
public class Item extends AbstractEntity {
    private static final String ITEM_PREFIX = "itm_";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String itemToken;
    private Long partnerId;
    private Long price;
    private String itemName;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST, mappedBy = "item")
    List<ItemOptionGroup> itemOptionGroups = Lists.newArrayList();

    @Enumerated(EnumType.STRING)
    private Status status;

    @Getter
    @RequiredArgsConstructor
    public enum Status {
        PREPARE("판매준비중"),
        ON_SALES("판매중"),
        END_OF_SALES("판매종료");

        private final String description;
    }

    @Builder
    public Item(Long partnerId, Long itemPrice, String itemName) {
        if (partnerId == null) throw new InvalidParamException();
        if (StringUtils.isEmpty(itemName)) throw new InvalidParamException();
        if (itemPrice == null) throw new InvalidParamException();

        this.itemToken = TokenGenerator.randomCharacterWithPrefix(ITEM_PREFIX);
        this.partnerId = partnerId;
        this.price = price;
        this.itemName = itemName;
        this.status = Status.PREPARE;
    }

    public void changePrepare() {
        this.status = Status.PREPARE;
    }

    public void changeOnSales() {
        this.status = Status.ON_SALES;
    }

    public void endOfSales() {
        this.status = Status.END_OF_SALES;
    }
}

