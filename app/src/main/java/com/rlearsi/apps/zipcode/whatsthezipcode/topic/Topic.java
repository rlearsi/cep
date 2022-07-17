package com.rlearsi.apps.zipcode.whatsthezipcode.topic;

import java.io.Serializable;

public class Topic implements Serializable {

    private int id, exist, active;
    private String zipcode, uf, city, neighborhood, logradouro;

    Topic(int id, String zipcode, String uf, String city, String neighborhood, String logradouro, int exist, int active){
        this.id         = id;
        this.zipcode        = zipcode;
        this.uf         = uf;
        this.city     = city;
        this.neighborhood     = neighborhood;
        this.logradouro = logradouro;
        this.exist = exist;
        this.active = active;

    }

    public int getId(){ return this.id; }
    String getCep(){ return this.zipcode; }
    String getUf(){ return this.uf; }
    String getCidade(){ return this.city; }
    String getBairro(){ return this.neighborhood; }
    String getLogradouro(){ return this.logradouro; }
    boolean getExist(){ return this.exist == 1; }
    boolean getActive(){ return this.active == 1; }

    @Override
    public boolean equals(Object o){
        return this.id == ((Topic)o).id;
    }

    @Override
    public int hashCode(){
        return this.id;
    }
}