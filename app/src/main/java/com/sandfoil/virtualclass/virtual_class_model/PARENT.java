package com.sandfoil.virtualclass.virtual_class_model;

public class PARENT {
    private String Name;
    private String Child_Name;
    private String Stream;
    private String Batch;
    private String College_Roll;
    private String Password;
    private String parentId;

    public PARENT(){

    }

    public PARENT(String Name, String Child_Name, String Stream, String Batch,
                  String College_Roll, String Password, String parentId){
        this.Name = Name;
        this.Child_Name = Child_Name;
        this.Stream = Stream;
        this.Batch = Batch;
        this.College_Roll = College_Roll;
        this.Password = Password;
        this.parentId = parentId;
    }

    public String getName() {
        return Name;
    }
    public void setName(String name) {
        Name = name;
    }

    public String getChild_Name() {
        return Child_Name;
    }
    public void setChild_Name(String child_Name) {
        Child_Name = child_Name;
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

    public String getPassword() {
        return Password;
    }
    public void setPassword(String password) {
        Password = password;
    }

    public String getParentId() {
        return parentId;
    }
    public void setParentId(String parentId) {
        this.parentId = parentId;
    }
}
