package io.github.tri5m.tucache.example.controller;

import io.github.tri5m.tucache.example.model.User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @title: GenericInterfacesTest
 * @author: trifolium.wang
 * @date: 2024/4/18
 * @modified: none
 */
@Service
public class GenericInterfacesTest <T>{

    public List<T> getUsers(){

        return (List<T>)Stream.of(new User(), new User(), new User(), new User()).collect(Collectors.toList());
    }

    public T getUser(){

        return (T) new User();
    }
}
