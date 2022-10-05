package heast.chatserver.permissionengine.permissions.identifications;

public enum VoiceChannelPermissions {
    CREATE(5), DELETE(6), EDIT(7);

    private int id;

    public int getID()
    {
        return this.id;
    }

    private VoiceChannelPermissions(int id)
    {
        this.id = id;
    }
}
