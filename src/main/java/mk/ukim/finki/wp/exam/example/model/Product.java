package mk.ukim.finki.wp.exam.example.model;

import lombok.Data;

import java.util.List;

@Data
public class Product {

    private Long id;

    private String name;

    private Double price;

    private Integer quantity;

    private List<Category> categories;

    private User creator;
}
