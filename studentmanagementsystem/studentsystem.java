package studentmanagementsystem;
import java.util.ArrayList;
import java.util.Scanner;
public class studentsystem {
    static Scanner sc=new Scanner(System.in);
    public static void main(String[] args){
        ArrayList<Student> list=new ArrayList<>();
        loop:while(true){
        System.out.println("欢迎来到学生管理系统");
        System.out.println("1：添加学生");
        System.out.println("2：删除学生");
        System.out.println("3：修改学生");
        System.out.println("4：查询学生");
        System.out.println("5：退出");
        String choose=sc.next();
        switch (choose){
            case "1"-> addStudent(list);
            case "2"-> deleteStudent(list);
            case "3"-> reviseStudent(list);
            case "4"-> queryStudent(list);
            case "5"->{
                System.out.println("退出");
                break loop;
            }
            default-> System.out.println("无此选项");
        }
    }
}
    public static void addStudent(ArrayList<Student> list){
        Student st=new Student();
        System.out.println("请输入添加学生的id");
        while(true){
        String id=sc.next();
        boolean bool=ifcontain(list,id);
        if(bool){
            System.out.println("该id已存在，请重新输入");
        }
        else{
            st.setid(id);
            break;
        }
    }
        System.out.println("请输入添加学生的name");
        String name=sc.next();
        st.setname(name);
        System.out.println("请输入添加学生的address");
        String address=sc.next();
        st.setaddress(address);
        System.out.println("请输入添加学生的age");
        int age=sc.nextInt();
        st.setage(age);
        list.add(st);
        System.out.println("添加成功");
}
    public static void queryStudent(ArrayList<Student> list){
    if(list.size()==0){
        System.out.println("当前无学生信息");
        return;
    }
    else{
        System.out.println("id\tname\taddress\tage\t");
        int length=list.size();
        for(int i=0;i<length;i++){
            Student st=list.get(i);
            System.out.println(st.getid()+"\t"+st.getname()+"\t"+st.getaddress()+"\t"+st.getage());

        }
    }
    }
    public static void deleteStudent(ArrayList<Student> list){
        System.out.println("请输入要删除的学生id");
        String id=sc.next();
        int index=getindex(list,id);
        if(index>=0){
            list.remove(index);
            System.out.println("您要删除的学生已删除，id为"+id);
        }
        else{
            System.out.println("要删除的学生id不存在");
    }
    }
    public static void reviseStudent(ArrayList<Student> list){
        System.out.println("请输入要修改的学生id");
        Student st=new Student();
        String id=sc.next();
        int index=getindex(list,id);
        if(index>=0){
            st=list.get(index);
            System.out.println("请输入要修改的学生名字");
            String name=sc.next();
            st.setname(name);
            System.out.println("请输入要修改的学生住址");
            String address=sc.next();
            st.setaddress(address);
            System.out.println("请输入要修改的学生年龄");
            int age=sc.nextInt();
            st.setage(age);
            System.out.println("id为"+id+"的学生信息修改成功");
        }
        else{
            System.out.println("要修改的学生不存在");
        }
    }
    public static boolean ifcontain(ArrayList<Student> list,String id){
        return getindex(list,id)>=0;
    }
    public static int getindex(ArrayList<Student> list,String id){
        for(int i=0;i<list.size();i++){
            Student st=list.get(i);
            if(st.getid().equals(id)){
                return i;
            }
        }
        return -1;
    }
}
