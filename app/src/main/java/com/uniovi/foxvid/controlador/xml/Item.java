package com.uniovi.foxvid.controlador.xml;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.Root;


@Root(name = "item", strict = false)
public class Item {

    @Element(name="title")
    private String title;

    @Element(name = "description")
    private String descripcion;

    @Element(name="link")
    private String url;

    @Element(name="content")
    @Namespace(reference="http://search.yahoo.com/mrss/", prefix="media")
    private Content content;



    public Item() {
    }

    public Item(String title, String descripcion, String url, Content content) {
        this.title = title;
        this.descripcion = descripcion;
        this.url = url;
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getLink() {
        return url;
    }

    public Content getContent() {
        return content;
    }
}
