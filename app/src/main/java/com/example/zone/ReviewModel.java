package com.example.zone;

//게시판 항목들을 가지고 있는 BoardModel Class ( 게시판 번호, 태그, 등록날짜,등록유저, 제목, 내용, 아이콘 )
public class ReviewModel {
    int area_no;
    String review_no;
    String reg_user;
    String reg_date;
    String reg_ctnt;
    String point;

    //constructor

    public ReviewModel(int area_no,String review_no,String reg_user,String reg_date,String reg_ctnt,String point)
    {
        this.area_no =area_no;
        this.review_no =review_no;
        this.reg_user =reg_user;
        this.reg_date = reg_date;
        this.reg_ctnt = reg_ctnt;
        this.point = point;
    }

    public ReviewModel(String reg_date,String reg_user,String reg_ctnt)
    {
        this.reg_user =reg_user;
        this.reg_date = reg_date;
        this.reg_ctnt = reg_ctnt;
    }
    public ReviewModel(ReviewModel ReviewModel){
        this.area_no =ReviewModel.area_no;
        this.review_no =ReviewModel.review_no;
        this.reg_user =ReviewModel.reg_user;
        this.reg_date = ReviewModel.reg_date;
        this.reg_ctnt =ReviewModel.reg_ctnt;
        this.point = ReviewModel.point;
    }
    public ReviewModel(){
        this.area_no =0;
        this.review_no ="";
        this.reg_user ="";
        this.reg_date = "";
        this.reg_ctnt = "";
        this.point = "";
    }

    //getters
    public int getArea_no() {
        return area_no;
    }

    public String getReview_no() {
        return review_no;
    }

    public String getReg_user() {
        return reg_user;
    }

    public String getReg_date() {
        return reg_date;
    }

    public String getReg_ctnt() {
        return reg_ctnt;
    }

    public String getPoint() {
        return point;
    }

}
