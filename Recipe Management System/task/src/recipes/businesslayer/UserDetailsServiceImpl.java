package recipes.businesslayer;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import recipes.persistence.RecipeUserRepository;

@Service
public class UserDetailsServiceImpl  implements UserDetailsService {

    private final RecipeUserRepository repository;

    public UserDetailsServiceImpl(RecipeUserRepository repository) {
        this.repository = repository;
    }


    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {
        RecipeUser user = repository
                .findRecipeUserByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return new RecipeUserAdapter(user);
    }
}
