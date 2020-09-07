package com.example.lab3;

public class ModelTest {


    public void LightTest() {
        LightsModel model = new LightsModel(5);
        System.out.println(model);
        System.out.println(" ");
        model.flipLines(2,2);
        System.out.println(model);
        System.out.println(" ");
        model.flipLines(2,2);
        System.out.println(model);
        System.out.println(" ");
    }
}
