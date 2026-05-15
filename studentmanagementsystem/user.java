package studentmanagementsystem;
public class user{
    private String username;
    private String password;
    private String phonenumber;
    private String identity;
    public user(){
    }
    public user(String username,String password,String phonenumber,String identity){
        this.username=username;
        this.password=password;
        this.phonenumber=phonenumber;
        this.identity=identity;
    }
    public void setUsername(String username){
        this.username=username;
    }
    public String getUsername(){
        return username;
    }
    public void setPassword(String password){
        this.password=password;
    }
    public String getPassword(){
        return password;
    }
    public void setPhonenumber(String phonenumber){
        this.phonenumber=phonenumber;
    }
    public String getPhonenumber(){
        return phonenumber;
    }
    public void setIdentity(String identity){
        this.identity=identity;
    }
    public String getIdentity(){
        return identity;
    }
}