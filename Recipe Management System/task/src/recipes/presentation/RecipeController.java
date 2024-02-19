package recipes.presentation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import recipes.businesslayer.Recipe;
import recipes.businesslayer.RecipeService;
import recipes.businesslayer.RecipeUser;
import recipes.persistence.RecipeUserRepository;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

@RestController
@Validated
public class RecipeController {
    @Autowired
    RecipeService recipeService;

    private final RecipeUserRepository repository;

    private final PasswordEncoder passwordEncoder;

    public RecipeController(RecipeUserRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }


    @GetMapping(path = "/api/test")
    public ResponseEntity test (){
        return ResponseEntity.ok().body("You actually logged in!");
    }
    @GetMapping(path = "/api/check")
    public ResponseEntity check (){
        return ResponseEntity.ok().body("You should get here easily");
    }

    @PostMapping(path = "/api/register")
    public ResponseEntity register(@RequestBody @Valid RegistrationRequest request) {

        if(repository.findRecipeUserByUsername(request.email()).isPresent()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        var user = new RecipeUser();
        user.setUsername(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setAuthority("ROLE_USER");

        repository.save(user);

        return ResponseEntity.ok().build();
    }
    record RegistrationRequest(@Email(regexp = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@"
            + "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$")String email,@Size(min = 8)@NotBlank String password) { }





    @PostMapping("api/recipe/new")
    public ResponseEntity saveRecipe(@AuthenticationPrincipal UserDetails details, @RequestBody Recipe recipe){
//        recipeService.save(recipe);
//        return ResponseEntity.ok().body(Map.of("id",recipe.getId()));
        if (!(details.getUsername()==null)) {
            try {
                RecipeUser user = repository.findRecipeUserByUsername(details.getUsername()).get();
                recipe.setUser(user);
                long newId = recipeService.save(recipe);
                Map<String, Long> returnId = new HashMap<String, Long>(1) {{
                    put("id", newId);
                }};
                return ResponseEntity.ok().body(returnId);
            } catch (Exception e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
            }
        } else throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);

    }
    @GetMapping("api/recipe/{id}")
    public ResponseEntity getRecipe(@PathVariable long id) {

        try {
            if (recipeService.findRecipeById(id).isPresent()) {
                Recipe recipeFound = recipeService.findRecipeById(id).get();
                Map<String, Object> recipeToReturn = new LinkedHashMap<>();
                recipeToReturn.put("name", recipeFound.getName());
                recipeToReturn.put("category", recipeFound.getCategory());
                recipeToReturn.put("date", recipeFound.getDate());
                recipeToReturn.put("description", recipeFound.getDescription());
                recipeToReturn.put("ingredients", recipeFound.getIngredients());
                recipeToReturn.put("directions", recipeFound.getDirections());
                return ResponseEntity.ok().body(recipeToReturn);
            } else {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }
    @DeleteMapping("api/recipe/{id}")
    public ResponseEntity deleteRecipe(@AuthenticationPrincipal UserDetails userDetails, @Valid @PathVariable Long id){

        if (recipeService.findRecipeById(id).isPresent()) {
            if (!Objects.equals(userDetails.getUsername(), recipeService.findRecipeById(id).get().getUser().getUsername())){
                throw new ResponseStatusException(HttpStatus.FORBIDDEN);
            }
                recipeService.delete(id);
        }else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
    @PutMapping("api/recipe/{id}")
    public ResponseEntity updateRecipe(@AuthenticationPrincipal UserDetails userDetails, @Valid @PathVariable Long id,@RequestBody Recipe recipe) {

        if (recipeService.findRecipeById(id).isPresent()) {
            if (!Objects.equals(userDetails.getUsername(), recipeService.findRecipeById(id).get().getUser().getUsername())){
                throw new ResponseStatusException(HttpStatus.FORBIDDEN);
            }
            try {
                recipeService.update(id, recipe);
            }
            catch(Exception e){
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
            }
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();

    }
    @GetMapping("/api/recipe/search/")
    public ResponseEntity searchRecipe(@RequestParam(required = false) String name,@RequestParam(required = false) String category) {
        if ((name == null || name.isEmpty()) ^ (category == null || category.isEmpty())) {
            if (name == null || name.isEmpty()){
                return ResponseEntity.ok().body(recipeService.findCategory(category));
            } else {
                return ResponseEntity.ok().body(recipeService.findName(name));
            }
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
    }







}
