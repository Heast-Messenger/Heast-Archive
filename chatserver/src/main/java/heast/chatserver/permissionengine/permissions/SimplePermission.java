package heast.chatserver.permissionengine.permissions;

public class SimplePermission implements Permission {
    private String name;
    private String readableName;
    private int id;

    public SimplePermission(String name, String readableName, int id) {
        this.name = name;
        this.readableName = readableName;
        this.id = id;
    }

    @Override
    public String getReadableName() {
        return this.readableName;
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