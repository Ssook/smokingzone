package com.example.zone;

//게시판 항목들을 가지고 있는 BoardModel Class ( 게시판 번호, 태그, 등록날짜,등록유저, 제목, 내용, 아이콘 )
public class BoardModel {
    int board_no;
    String tag;
    String reg_date;
    String reg_user;
    String title;
    String desc;
    int icon;

    //constructor



    public BoardModel(int board_no,String tag, String reg_date, String reg_user, String title, String desc, int icon)
    {
        this.board_no = board_no;
        this.tag =tag;
        this.reg_date =reg_date;
        this.reg_user =reg_user;
        this.title = title;
        this.desc = desc;
        this.icon = icon;
    }

    public BoardModel(BoardModel boardModel){
        this.tag = boardModel.tag;
        this.reg_date = boardModel.reg_date;
        this.reg_user = boardModel.reg_user;
        this.title = boardModel.title;
        this.desc = boardModel.desc;
        this.icon = boardModel.icon;
    }
    public BoardModel(){
        this.tag ="";
        this.reg_date ="";
        this.reg_user ="";
        this.title = "";
        this.desc = "";
        this.icon = 0;
    }

    //getters

    public String getTitle() {
        return this.title;
    }

    public String getDesc() {
        return this.desc;
    }

    public int getIcon() {
        return this.icon;
    }

    public int getBoard_no() {
        return this.board_no;
    }

    public String getTag() {
        return this.tag;
    }

    public String getReg_date() {
        return this.reg_date;
    }

    public String getReg_user() {
        return this.reg_user;
    }
}
