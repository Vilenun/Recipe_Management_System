package recipes.persistence;

import org.springframework.stereotype.Repository;
import org.springframework.data.repository.CrudRepository;
import recipes.businesslayer.Recipe;

import java.util.List;

@Repository
public interface RecipesRepository extends CrudRepository<Recipe,Long> {
    List<Recipe> findByCategoryIgnoreCaseOrderByDateDesc(String category);
    List<Recipe> findByNameIgnoreCaseContainsOrderByDateDesc(String name);
}
