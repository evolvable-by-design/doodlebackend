package fr.istic.tlc.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String username;

    @JsonIgnore
    @ManyToMany(mappedBy = "users")
    List<Choice> userChoices = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy="user", cascade = CascadeType.ALL)
    List<Comment> userComments = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy="user", cascade = CascadeType.ALL)
    List<MealPreference> userMealPreferences = new ArrayList<>();

    public User(){}

    public User(String username) {
        this.username = username;
    }

    public void addChoice(Choice choice){
        this.userChoices.add(choice);
    }

    public void removeChoice(Choice choice){
        this.userChoices.remove(choice);
    }

    public void addComment (Comment comment) {this.userComments.add(comment);}

    public void removeComment (Comment comment) {this.userComments.remove(comment);}

    public void addMealPreference (MealPreference mealPreference) {this.userMealPreferences.add(mealPreference);}

    public void removeMealPreference (MealPreference mealPreference) {this.userMealPreferences.remove(mealPreference);}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<Choice> getUserChoices() {
        return userChoices;
    }

    public List<MealPreference> getUserMealPreferences() {
        return userMealPreferences;
    }

    public void setUserMealPreferences(List<MealPreference> userMealPreferences) {
        this.userMealPreferences = userMealPreferences;
    }

    public void setUserChoices(List<Choice> userChoices) {
        this.userChoices = userChoices;
    }

    public List<Comment> getUserComments() {
        return userComments;
    }

    public void setUserComments(List<Comment> userComments) {
        this.userComments = userComments;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                '}';
    }
}
