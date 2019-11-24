package com.example.zone;

//게시판 항목들을 가지고 있는 BoardModel Class ( 게시판 번호, 태그, 등록날짜,등록유저, 제목, 내용, 아이콘 )
public class BoardCommentModel {
    int area_no;
    String board_no;
    String reg_user;
    String reg_date;
    String reg_ctnt;


    //constructor

    public BoardCommentModel(int area_no,String review_no,String reg_user,String reg_date,String reg_ctnt,String point)
    {
        this.area_no =area_no;
        this.board_no =review_no;
        this.reg_user =reg_user;
        this.reg_date = reg_date;
        this.reg_ctnt = reg_ctnt;
    }

    public BoardCommentModel(String reg_date,String reg_user,String reg_ctnt)
    {
        this.reg_user =reg_user;
        this.reg_date = reg_date;
        this.reg_ctnt = reg_ctnt;
    }

    //getters
    public int getArea_no() {
        return area_no;
    }

    public String getBoard_no() {
        return board_no;
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


}
