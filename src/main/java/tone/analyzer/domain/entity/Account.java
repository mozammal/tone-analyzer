package tone.analyzer.domain.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by mozammal on 4/18/17.
 */
@Document
public class Account {

    @Id
    private String id;

    @Indexed(unique = true)
    private String name;

    private String password;

    private List<Role> role;

    private boolean enabled;

    Set<String> buddyList;

    public Account() {
    }

    public Account(String name, String password) {
        this.name = name.toLowerCase();
        this.password = password;
        this.buddyList = new HashSet<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name.toLowerCase();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Role> getRole() {
        return role;
    }

    public void setRole(List<Role> role) {
        this.role = role;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Set<String> getBuddyList() {
        return buddyList;
    }

    public void setBuddyList(Set<String> buddyList) {
        this.buddyList = buddyList;
    }
}
