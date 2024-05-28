package com.example.demo.card;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Card { // 더미데이터
    @Id
    @SequenceGenerator(name = "seq_gen", sequenceName = "seq_card", allocationSize = 1) // 시퀀스 생성
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_card")
    private int num;
    private String cardnum; // 카드번호
    private int csv;
    private int pwd; // 카드 비번
    private int price;

}