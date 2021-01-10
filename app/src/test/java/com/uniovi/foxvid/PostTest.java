package com.uniovi.foxvid;

import com.google.firebase.Timestamp;
import com.uniovi.foxvid.modelo.Post;

import org.junit.Assert;
import org.junit.Test;

import java.util.Calendar;
import java.util.Date;

public class PostTest {

    @Test
    public void getTimeTests() {
        Calendar calendar = Calendar.getInstance();

        Post post = new Post();

        //Post creado hace un minuto
        calendar.setTimeInMillis(new Date().getTime()- (1000L * 60L));
        post.setDate(new Timestamp(calendar.getTime()));

        Assert.assertEquals("1 min", post.getTime());

        //Post creado hace una hora
        calendar.setTimeInMillis(new Date().getTime()- (1000L * 60L * 60L));
        post.setDate(new Timestamp(calendar.getTime()));

        Assert.assertEquals("1 h", post.getTime());

        //Post creado hace un día
        calendar.setTimeInMillis(new Date().getTime()- (1000L * 60L * 60L * 24L));
        post.setDate(new Timestamp(calendar.getTime()));

        Assert.assertEquals("1 d", post.getTime());

    }
}
