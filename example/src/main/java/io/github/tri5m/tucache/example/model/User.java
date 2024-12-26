package io.github.tri5m.tucache.example.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @title: User
 * @author: trifolium.wang
 * @date: 2022/7/5
 * @modified :
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {

    private String name;

    private int age;
}
