package account.configuration;

import account.domain.Role;
import account.domain.entity.Group;
import account.repository.GroupRepository;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class DataLoader {

    private GroupRepository groupRepository;

    public DataLoader(GroupRepository groupRepository) {
        this.groupRepository = groupRepository;
        createRoles();
    }

    public void createRoles() {
        Arrays.stream(Role.values()).filter(role -> !groupRepository.existsByName(role.toString())).forEach(role ->
                groupRepository.save(new Group(role.toString())));
    }

}
