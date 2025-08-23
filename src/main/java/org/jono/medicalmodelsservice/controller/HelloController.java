package org.jono.medicalmodelsservice.controller;

import lombok.extern.slf4j.Slf4j;
import org.jono.medicalmodelsservice.model.Pet;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@CrossOrigin
@RestController
public class HelloController {

    @Value("${snake.name}")
    private String snakeName;

    @GetMapping(path = "/hello",
            produces = "application/json")
    @ResponseBody
    public Pet handleGet() {
        return new Pet("Coppy", "Snake");
    }

    @PostMapping(path = "/hello")
    public void handlePost(@RequestBody final Pet pet) {
        System.out.println("Name");
        System.out.println(pet.getName());
        System.out.println("Species");
        System.out.println(pet.getSpecies());
    }

    @GetMapping(path = "/hello2",
            produces = "application/json")
    @ResponseBody
    public Pet handleGet2() {
        return new Pet(this.snakeName, "Snake");
    }
}
