package uz.javharbek.oauth.service;

import uz.javharbek.oauth.entity.AuthUser;
import uz.javharbek.oauth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String userName) {
        AuthUser user = getUserByLogin(userName);
        List<GrantedAuthority> authorities_roles = user.getRoles()
                .stream().map(role -> new SimpleGrantedAuthority(role.getRoleName()))
                .collect(Collectors.toList());
        List<GrantedAuthority> authorities_permissions = user.getPermissions()
                .stream().map(permission -> new SimpleGrantedAuthority(permission.getPermissionName()))
                .collect(Collectors.toList());
        List<GrantedAuthority> authorities = Stream.of(authorities_roles, authorities_permissions)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
        return new User(user.getUserName(), user.getPassword(), authorities);

    }

    private AuthUser getUserByLogin(String login) {
        // it will be called at access token generation time
        Optional<AuthUser> optUser = userRepository.findByUserName(login);
        if (!optUser.isPresent()) {
            Optional<AuthUser> optUserPhone = userRepository.findByPhone(login);
            if(!optUserPhone.isPresent()){
                throw new RuntimeException("user not exist");
            }
            return optUserPhone.get();
        }else{
            return optUser.get();
        }
    }


}
