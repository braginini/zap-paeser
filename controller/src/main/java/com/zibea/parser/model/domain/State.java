package com.zibea.parser.model.domain;

/**
 * @author: Mikhail Bragin
 */
public class State extends RealtyObject {

    public State(long id, String title, String urlParam) {
        super(id, title, urlParam);
    }

    @Override
    public String toString() {
        return "State{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", urlParam='" + urlParam + '\'' +
                '}';
    }
}
