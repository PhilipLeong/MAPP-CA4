package mapp.com.sg.bookhub.Models;

public class User {
    private String account;
    private String schoolcourse;
    private String bio;
    private String profileimg;
    private String email;

    public User(){}

    public User(String account, String schoolcourse, String bio, String profileimg, String email ){
        this.account = account;
        this.schoolcourse = schoolcourse;
        this.bio = bio;
        this.profileimg = profileimg;
        this.email = email;
    }

    public String getProfileimg(){
        return this.profileimg;
    }

    public void setProfileimg(String profileimg){
        this.profileimg = profileimg;
    }

    public String getEmail(){
        return this.email;
    }

    public void setEmail(String email){
        this.email = email;
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
