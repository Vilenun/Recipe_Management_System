package recipes.persistence;

import org.springframework.data.repository.CrudRepository;
import recipes.businesslayer.RecipeUser;

import java.util.Optional;

public interface RecipeUserRepository extends CrudRepository<RecipeUser, Long> {

    public Optional<RecipeUser> findRecipeUserByUsername(String username);
}
