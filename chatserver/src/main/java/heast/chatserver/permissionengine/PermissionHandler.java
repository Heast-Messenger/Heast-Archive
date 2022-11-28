package heast.chatserver.permissionengine;

import heast.chatserver.json.JSONArray;
import heast.chatserver.json.JSONObject;
import heast.chatserver.json.parser.JSONParser;
import heast.chatserver.permissionengine.permissions.*;

import java.io.FileReader;
import java.util.HashMap;

//TODO rename and comment
public class PermissionHandler {

    private static HashMap<Integer, Permission> globalPermissions;
    private static HashMap<Integer, Permission> channelPermissions;

    public static void initialize(){
        initVars();
        readJSON();
    }

    private static void initVars() {
        globalPermissions = new HashMap<>();
        channelPermissions = new HashMap<>();
    }

    private static void readJSON(){
        try {
            JSONParser parser = new JSONParser();
            Object file = parser.parse(new FileReader(PermissionHandler.class.getResource("/permissions.json").getFile()));
            JSONObject jsonFile = (JSONObject) file;
            JSONObject permissions = (JSONObject) jsonFile.get("Permissions");

            JSONArray targeted = (JSONArray) permissions.get("Targeted");
            JSONArray global = (JSONArray) permissions.get("Global");
            JSONArray channel = (JSONArray) permissions.get("Channel");

            initializePermissions(targeted, global, channel);

        }catch (Exception e){
            //TODO change to error message
            e.printStackTrace();
        }
    }

    /**
     * Initializes the permissions <br>
     * Adds all the targeted and global permissions to the globalPermissions HashMap <br>
     * Adds all the channel permissions to the channelPermissions HashMap <br>
     * <p>
     *     Global Permissions and Targeted Permissions are not saved in the same HashMap because the have different id ranges
     * </p>
     *
     *
     * @param targeted
     * @param global
     * @param channel
     */
    private static void initializePermissions(JSONArray targeted, JSONArray global, JSONArray channel){
        for(Object o : targeted){
            JSONObject jobject = (JSONObject) o;
            Permission p = new SimpleTargetPermission(String.valueOf(jobject.get(0)), String.valueOf(jobject.get(1))
                                                      , Integer.parseInt(jobject.get(2).toString()),
                                                      PermissionTarget.getTarget(Integer.parseInt(jobject.get(3).toString())));

            globalPermissions.put(p.getID(), p);
        }

        for(Object o : global){
            JSONObject jobject = (JSONObject) o;
            Permission p = new SimplePermission(String.valueOf(jobject.get(0)), String.valueOf(jobject.get(1)), Integer.parseInt(jobject.get(2).toString()));

            globalPermissions.put(p.getID(), p);
        }

        //TODO implement ChanelPermissions
        /*for(Object o : channel){
            JSONObject jobject = (JSONObject) o;
            Permission p = new SimpleChannelPermission(String.valueOf(jobject.get(0)), String.valueOf(jobject.get(1)), Integer.parseInt(jobject.get(2).toString()));

            channelPermissions.put(p.getID(), p);
        }*/
    }

    /**
     * @usage haspermission(5212, MessagePermissions.DELETE);
     * @param uid the user id
     * @param permissionID the identifier of the permission
     * @return whether the user has the permission
     */
    public static boolean hasPermission(int uid, int permissionID){
        //TODO check in database (or cache) if the user has the permission
        return false;
    }

    /**
     * Gets the amount of global permissions (User Permissions)
     * @return the amount of global permissions
     */
    public static int getGlobalPermissionAmount(){
        return globalPermissions.size();
    }


}
