package mapp.com.sg.bookhub.Models;

public class User {
    private String account;
    private String schoolcourse;
    private String bio;

    public User(){}

    public User(String account, String schoolcourse, String bio ){
        this.account = account;
        this.schoolcourse = schoolcourse;
        this.bio = bio;
    }

    public String getAccount(){
        return this.account;
    }

    public void setAccount(String account){
        this.account = account;
    }

    public String getSchoolCourse(){
        return this.schoolcourse;
    }

    public void setSchoolCourse(String schoolcourse){
        this.schoolcourse = schoolcourse;
    }

    public String getBio(){
        return this.bio;
    }

    public void setBio(String bio){
        this.bio = bio;
    }
}
