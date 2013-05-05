package com.zibea.parser.model.domain;

/**
 * @author: Mikhail Bragin
 */
public class Apartment extends RealtyObject {

    public Apartment(long id, String title, String urlParam) {
        super(id, title, urlParam);
    }


    @Override
    public String toString() {
        return "Apartment{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", urlParam='" + urlParam + '\'' +
                '}';
    }
}
