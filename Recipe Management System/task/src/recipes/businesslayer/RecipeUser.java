package recipes.businesslayer;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class RecipeUser {
    @Id
    @GeneratedValue
    private Integer id;
    private String username;
    private String password;
    private String authority;
    @OneToMany(mappedBy = "user")
    @OrderColumn
    private List<Recipe> recipes = new ArrayList<>();


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAuthority() {
        return authority;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }

    public List<Recipe> getRecipes() {
        return recipes;
    }

    public void setRecipes(List<Recipe> recipes) {
        this.recipes = recipes;
    }
}
