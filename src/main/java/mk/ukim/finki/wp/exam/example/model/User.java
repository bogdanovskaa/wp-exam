package mk.ukim.finki.wp.exam.example.model;

import lombok.Data;

@Data
public class User {

    public User(String username, String password, Role role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    private String username;

    private String password;

    private Role role;
}
