package org.loopz.seaterrenos.manager;

public class Friend {
    private final String name;
    private final Permissions permissions;

    public Friend(String name, Permissions permissions) {
        this.name = name;
        this.permissions = permissions;
    }

    public String getName() {
        return name;
    }

    public Permissions getPermissions() {
        return permissions;
    }
}
