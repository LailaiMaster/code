package net.qiujuer.web.italker.push.bean.api.group;

import com.google.gson.annotations.Expose;

import java.util.HashSet;
import java.util.Set;

/**
 * @author qiujuer Email:qiujuer@live.cn
 * @version 1.0.0
 */
public class GroupMemberAddModel {

    @Expose
    private Set<String> users = new HashSet<>();

    public Set<String> getUsers() {
        return users;
    }

    public void setUsers(Set<String> users) {
        this.users = users;
    }

    public static boolean check(GroupMemberAddModel model) {
        return !(model.users == null
                || model.users.size() == 0);
    }
}
