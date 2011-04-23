import com.greetim.Role

class BootStrap {
    def init = { servletContext ->
        Role.withTransaction {
            if (Role.count() <= 0) {
                new Role(authority: 'ROLE_USER').save();
                new Role(authority: 'ROLE_ADMIN').save();
            }
        }
    }

    def destroy = {
    }
}
