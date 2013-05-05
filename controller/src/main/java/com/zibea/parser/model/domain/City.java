package com.zibea.parser.model.domain;

/**
 * @author: Mikhail Bragin
 */
public class City extends RealtyObject {

    private long stateId;

    private State state;

    public City(long id, String title, String urlParam, long stateId) {
        super(id, title, urlParam);
        this.stateId = stateId;
    }

    public City(long id, String title, String urlParam, State state) {
        super(id, title, urlParam);
        this.stateId = state.getId();
        this.state = state;
    }

    public long getStateId() {
        return stateId;
    }

    public void setStateId(long stateId) {
        this.stateId = stateId;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    @Override
    public String toString() {
        return "City{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", urlParam='" + urlParam + '\'' +
                ", stateId='" + stateId + '\'' +
                '}';
    }
}
