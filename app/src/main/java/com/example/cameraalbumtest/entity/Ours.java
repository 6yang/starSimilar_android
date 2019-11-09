package com.example.cameraalbumtest.entity;


import java.util.ArrayList;
import java.util.List;

public class Ours {
    public String error_msg;
    public Result result = new Result();
    public class Result{
        public String face_num;
        public List<Face_list> face_list  = new ArrayList<>();
        public class Face_list{
            public String face_token;
            public Location location = new Location();
            public class Location{
                public String left;
                public String top ;
                public String width;
                public String height;
                public String rotation;

                @Override
                public String toString() {
                    return "Location{" +
                            "left='" + left + '\'' +
                            ", top='" + top + '\'' +
                            ", width='" + width + '\'' +
                            ", height='" + height + '\'' +
                            ", rotation='" + rotation + '\'' +
                            '}';
                }
            }
            public List<User_list> user_list = new ArrayList<>();
            public class User_list{
                public String group_id;
                public String user_id;
                public String user_info;
                public String score;

                @Override
                public String toString() {
                    return "User_list{" +
                            "group_id='" + group_id + '\'' +
                            ", user_id='" + user_id + '\'' +
                            ", user_info='" + user_info + '\'' +
                            ", score='" + score + '\'' +
                            '}';
                }
            }

            @Override
            public String toString() {
                return "Face_list{" +
                        "face_token='" + face_token + '\'' +
                        ", location=" + location +
                        ", user_list=" + user_list +
                        '}';
            }
        }

        @Override
        public String
        toString() {
            return "Result{" +
                    "face_num='" + face_num + '\'' +
                    ", face_list=" + face_list +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "Ours{" +
                "error_msg='" + error_msg + '\'' +
                ", result=" + result +
                '}';
    }
}
