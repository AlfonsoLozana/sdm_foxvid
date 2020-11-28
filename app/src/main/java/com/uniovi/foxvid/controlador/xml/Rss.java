package com.uniovi.foxvid.controlador.xml;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.Root;


@Root(name = "rss", strict = false)
@Namespace(reference="http://search.yahoo.com/mrss/")
public class Rss {


    @Element
    private Channel channel;

    public Rss() {
    }

    public Rss(Channel channel) {
        this.channel = channel;
    }

    public Channel getChannel() {
        return channel;
    }
}
