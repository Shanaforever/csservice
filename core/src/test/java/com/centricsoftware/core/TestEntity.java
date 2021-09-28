package com.centricsoftware.core;

public class TestEntity {
    private String name;
    private String age;
    private String gender;
    private String province;

    public TestEntity() {
    }

    public TestEntity(String name, String age, String gender, String province) {
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.province = province;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    @Override
    public String toString() {
        return "TestEntity{" +
                "name='" + name + '\'' +
                ", age='" + age + '\'' +
                ", gender='" + gender + '\'' +
                ", province='" + province + '\'' +
                '}';
    }
}
