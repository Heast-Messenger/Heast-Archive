package heast.chatserver.permissionengine.permissions;

public class SimplePermission implements Permission {
    private String name;
    private String description;
    private int id;

    public SimplePermission(String name, String readableName, int id) {
        this.name = name;
        this.description = readableName;
        this.id = id;
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public int getID() {
        return this.id;
    }
}