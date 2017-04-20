package tone.analyzer.auth.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import tone.analyzer.domain.entity.User;
import tone.analyzer.domain.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

/** Created by mozammal on 4/18/17. */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {
  @Autowired private UserRepository userRepository;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    User user = userRepository.findByName(username);
    if (user == null) throw new UsernameNotFoundException("User not found");

    List<GrantedAuthority> role = new ArrayList<>();
    return new org.springframework.security.core.userdetails.User(
        user.getName(), user.getPassword(), role);
  }
}
