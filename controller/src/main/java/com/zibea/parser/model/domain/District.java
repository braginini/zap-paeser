package com.zibea.parser.model.domain;

/**
 * @author: Mikhail Bragin
 */
public class District extends RealtyObject {

    private long cityId;

    private City city;

    public District(long id, String title, String urlParam, long cityId) {
        super(id, title, urlParam);
        this.cityId = cityId;
    }

    public District(long id, String title, String urlParam, City city) {
        super(id, title, urlParam);
        this.cityId = city.getId();
        this.city = city;
    }

    public long getCityId() {
        return cityId;
    }

    public void setCityId(long stateId) {
        this.cityId = stateId;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    @Override
    public String toString() {
        return "District{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", urlParam='" + urlParam + '\'' +
                ", cityId='" + cityId + '\'' +
                '}';
    }
}
