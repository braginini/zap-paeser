package com.zibea.parser.core.task;

import com.zibea.parser.model.domain.*;

/**
 * @author: Mikhail Bragin
 */
public class Task {

    protected State state;

    protected City city;

    protected Apartment apartment;

    protected Transaction transaction;

    protected District district;

    protected String url;

    public Task() {
    }

    public Task(State state, City city, Apartment apartment, Transaction transaction, District district, String url) {
        this.state = state;
        this.city = city;
        this.apartment = apartment;
        this.transaction = transaction;
        this.district = district;
        this.url = url;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public Apartment getApartment() {
        return apartment;
    }

    public void setApartment(Apartment apartment) {
        this.apartment = apartment;
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }

    public District getDistrict() {
        return district;
    }

    public void setDistrict(District district) {
        this.district = district;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "Task{" +
                "state=" + state +
                ", city=" + city +
                ", apartment=" + apartment +
                ", transaction=" + transaction +
                ", district=" + district +
                ", url='" + url + '\'' +
                '}';
    }
}
