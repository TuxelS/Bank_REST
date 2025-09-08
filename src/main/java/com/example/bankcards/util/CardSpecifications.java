package com.example.bankcards.util;

import com.example.bankcards.entity.Card;
import com.example.bankcards.enumeration.CardStatus;
import org.springframework.data.jpa.domain.Specification;

public class CardSpecifications {
    public static Specification<Card> hasOwner(String username) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("owner").get("username"), username);
    }

    public static Specification<Card> hasStatus(String status) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("status"), status);
    }

    public static Specification<Card> hasLastFour(String lastFour) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(root.get("lastFour"), "%" + lastFour + "%");
    }
}
