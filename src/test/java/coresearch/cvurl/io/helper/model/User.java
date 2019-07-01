package coresearch.cvurl.io.helper.model;

import java.util.Objects;
import java.util.Set;

public class User {
    private String name;
    private int age;
    private Set<String> skills;
    private User child;

    public User() {
    }

    public User(String name, int age, Set<String> skills, User child) {
        this.name = name;
        this.age = age;
        this.skills = skills;
        this.child = child;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public Set<String> getSkills() {
        return skills;
    }

    public void setSkills(Set<String> skills) {
        this.skills = skills;
    }

    public User getChild() {
        return child;
    }

    public void setChild(User child) {
        this.child = child;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return age == user.age &&
                Objects.equals(name, user.name) &&
                Objects.equals(skills, user.skills) &&
                Objects.equals(child, user.child);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, age, skills, child);
    }
}
