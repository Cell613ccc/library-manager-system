package studentmanagementsystem;
public class Student {
    private String id;
    private String name;
    private String address;
    private int age;
    public Student(){}
    public Student(String id,String name,String address,int age){
        this.id=id;
        this.name=name;
        this.address=address;
        this.age=age;
    }
    public String getid(){
        return id;
    }
    public void setid(String id){
        this.id=id;
    }
    public String getname(){
        return name;
    }
    public void setname(String name){
        this.name=name;
    }
    public String getaddress(){
        return address;
    }
    public void setaddress(String address){
        this.address=address;
    }
    public int getage(){
        return age;
    }
    public void setage(int age){
        this.age=age;
    }
}
