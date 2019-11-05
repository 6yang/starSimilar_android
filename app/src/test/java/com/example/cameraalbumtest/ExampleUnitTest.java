package com.example.cameraalbumtest;

import com.example.cameraalbumtest.entity.SearchOut;
import com.google.gson.Gson;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }


    /*
    *
    * {
	"success_tag": "success",
	"face_token": "883d4e8ab8e35fab9b229051fe7b1786",
	"group_id": "star_woman_asia",
	"user_id": "12b27710_a050_4ca4_8292_cc60a57296c3",
	"user_info": "周笔畅",
	"score": "55",
	"img_url": "http://112.124.46.10:8080/face/images/12b27710_a050_4ca4_8292_cc60a57296c3.jpg"
}
    *
    * */
    @Test
    public void testGsonToJson() {
        String json = "{\"success_tag\":\"success\",\"face_token\":\"883d4e8ab8e35fab9b229051fe7b1786\",\"group_id\":\"star_woman_asia\",\"user_id\":\"12b27710_a050_4ca4_8292_cc60a57296c3\",\"user_info\":\"周笔畅\",\"score\":\"55\",\"img_url\":\"http://112.124.46.10:8080/face/images/12b27710_a050_4ca4_8292_cc60a57296c3.jpg\"}";
//        System.out.println(json);
        Gson gson = new Gson();
        SearchOut searchOut = gson.fromJson(json, SearchOut.class);
        System.out.println(searchOut);


    }
}