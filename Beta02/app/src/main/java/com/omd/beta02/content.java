package com.omd.beta02;

public class content {

    private int id;
    private String tags1;
    private String tags2;
    private String tags3;
    private String uri;

    public content(String tags1, String tags2,String tags3, String uri, int id){
        this.tags1 = tags1;
        this.tags2 = tags2;
        this.tags3 = tags3;
        this.uri = uri;
        this.id = id;
    }

    public int getId(){
        return id;
    }
    public void setId(int id){
        this.id = id;
    }

    public String getTags1(){
        return tags1;
    }
    public void setTags1(String tags1){
        this.tags1 = tags1;
    }

    public String getTags2(){
        return tags2;
    }
    public void setTags2(String tags2){
        this.tags2 = tags2;
    }

    public String getTags3(){
        return tags3;
    }
    public void setTags3(String tags3){
        this.tags3 = tags3;
    }

    public String getUri(){
        return uri;
    }
    public void setUri(String uri){
        this.uri = uri;
    }

}
