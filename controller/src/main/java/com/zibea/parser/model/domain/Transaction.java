package com.zibea.parser.model.domain;

/**
 * @author: Mikhail Bragin
 */
public class Transaction extends RealtyObject {

    public Transaction(long id, String title, String urlParam) {
        super(id, title, urlParam);
    }


    @Override
    public String toString() {
        return "Transaction{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", urlParam='" + urlParam + '\'' +
                '}';
    }
}
