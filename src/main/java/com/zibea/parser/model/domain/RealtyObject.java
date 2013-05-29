package com.zibea.parser.model.domain;

/**
 * @author: Mikhail Bragin
 */
public abstract class RealtyObject {

    protected long id;

    protected String title;

    protected String urlParam;

    public RealtyObject(long id, String title, String urlParam) {
        this.id = id;
        this.title = title;
        this.urlParam = urlParam;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrlParam() {
        return urlParam;
    }

    public void setUrlParam(String urlParam) {
        this.urlParam = urlParam;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RealtyObject that = (RealtyObject) o;

        if (id != that.id) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }
}
