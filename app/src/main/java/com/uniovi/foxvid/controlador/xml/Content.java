package com.uniovi.foxvid.controlador.xml;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;


@Root(name="content", strict = false)
public class Content {

    @Attribute(name = "url")
    private String url;

    public Content() {
    }

    public Content(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }
}
