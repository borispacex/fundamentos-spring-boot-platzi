package com.fundamentosplatzi.springboot.fundamentos.bean;

import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

public class MyBeanWithDependencyImplement implements MyBeanWithDependency{

    Log LOGGER = LogFactory.getLog(MyBeanWithDependencyImplement.class);

    private MyOperation myOperation;

    public MyBeanWithDependencyImplement(MyOperation myOperation) {
        this.myOperation = myOperation;
    }

    @Override
    public void printWithDependency() {
        LOGGER.info("Hemos ingresado al metodo PrintwithDependency");
        int numero = 1;
        LOGGER.debug("El numero enviado como parametro a la dependecia operation es " + numero);
        System.out.println(this.myOperation.sum(1));
        System.out.println("Hola desde la implementacion de un bean con dependencia");
    }
}
