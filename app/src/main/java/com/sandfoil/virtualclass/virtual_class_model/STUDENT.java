package com.sandfoil.virtualclass.virtual_class_model;

public class STUDENT {
    private String Name;
    private String Stream;
    private String Batch;
    private String College_Roll;
    private String Mother_Name;
    private String Father_Name;
    private String Password;
    private String studentId;

    public STUDENT(){

    }

    public STUDENT(String Name, String Stream, String Batch, String College_Roll,
                   String Mother_Name, String Father_Name ,String Password, String studentId){

        this.Name = Name;
        this.Stream = Stream;
        this.Batch = Batch;
        this.College_Roll = College_Roll;
        this.Mother_Name = Mother_Name;
        this.Father_Name = Father_Name;
        this.Password = Password;
        this.studentId = studentId;
    }

    public String getName() {
        return Name;
    }
    public void setName(String name) {
        Name = name;
    }

    public String getStream() {
        return Stream;
    }
    public void setStream(String stream) {
        Stream = stream;
    }

    public String getBatch() {
        return Batch;
    }
    public void setBatch(String batch) {
        Batch = batch;
    }

    public String getCollege_Roll() {
        return College_Roll;
    }
    public void setCollege_Roll(String college_Roll) {
        College_Roll = college_Roll;
    }

    public String getMother_Name() {
        return Mother_Name;
    }
    public void setMother_Name(String mother_Name) {
        Mother_Name = mother_Name;
    }

    public String getFather_Name() {
        return Father_Name;
    }
    public void setFather_Name(String father_Name) {
        Father_Name = father_Name;
    }

    public String getPassword() {
        return Password;
    }
    public void setPassword(String password) {
        Password = password;
    }

    public String getStudentId() {
        return studentId;
    }
    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }
}
