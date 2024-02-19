package recipes.businesslayer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import recipes.persistence.RecipesRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class RecipeService {
    @Autowired
    RecipesRepository recipeRepository;

    public List<Recipe> getRecipeAll() {
        List<Recipe> recipeAll = new ArrayList<>();
        recipeRepository.findAll().forEach(recipeAll::add);
        return recipeAll;
    }


    public long count(){
        return recipeRepository.count();
    }
    public boolean doWeHaveRecipies(){
        return recipeRepository.count() >= 1;
    }
    public Optional<Recipe> findRecipeById(long id) {
        if (recipeRepository.findById(id).isPresent()) {
            return Optional.of(recipeRepository.findById(id).get());
        } else {
            return Optional.empty();
        }
    }
    public long save(Recipe recipe) {
        Recipe savedRecipe = recipeRepository.save(recipe);
        return savedRecipe.getId();
    }
    public void delete(long id) {
        recipeRepository.deleteById(id);
    }
    public void update(long id,Recipe recipe){
        Recipe recipeToUpdate = recipeRepository.findById(id).orElseThrow();
        recipeToUpdate.setName(recipe.getName());
        recipeToUpdate.setCategory(recipe.getCategory());
        recipeToUpdate.setDescription(recipe.getDescription());
        recipeToUpdate.setIngredients(recipe.getIngredients());
        recipeToUpdate.setDirections(recipe.getDirections());
        recipeRepository.save(recipeToUpdate);
    }
    public List<Recipe> findName(String name){
       return recipeRepository.findByNameIgnoreCaseContainsOrderByDateDesc(name);
    }
    public List<Recipe> findCategory(String category){
        return recipeRepository.findByCategoryIgnoreCaseOrderByDateDesc(category);
    }
}
