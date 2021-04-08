package net.qiujuer.web.italker.push.service;

import com.google.common.base.Strings;
import net.qiujuer.web.italker.push.bean.api.base.ResponseModel;
import net.qiujuer.web.italker.push.bean.api.group.GroupCreateModel;
import net.qiujuer.web.italker.push.bean.api.group.GroupMemberAddModel;
import net.qiujuer.web.italker.push.bean.api.group.GroupMemberUpdateModel;
import net.qiujuer.web.italker.push.bean.card.ApplyCard;
import net.qiujuer.web.italker.push.bean.card.GroupCard;
import net.qiujuer.web.italker.push.bean.card.GroupMemberCard;
import net.qiujuer.web.italker.push.bean.db.Group;
import net.qiujuer.web.italker.push.bean.db.GroupMember;
import net.qiujuer.web.italker.push.bean.db.User;
import net.qiujuer.web.italker.push.factory.GroupFactory;
import net.qiujuer.web.italker.push.factory.PushFactory;
import net.qiujuer.web.italker.push.factory.UserFactory;
import net.qiujuer.web.italker.push.provider.LocalDateTimeConverter;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 群组的接口的入口
 *
 * @author qiujuer Email:qiujuer@live.cn
 * @version 1.0.0
 */
@Path("/group")
public class GroupService extends BaseService {
    /**
     * 创建群
     *
     * @param model 基本参数
     * @return 群信息
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseModel<GroupCard> create(GroupCreateModel model) {
        if (!GroupCreateModel.check(model)) {
            return ResponseModel.buildParameterError();
        }

        // 创建者
        User creator = getSelf();
        // 创建者并不在列表中
        model.getUsers().remove(creator.getId());
        if (model.getUsers().size() == 0) {
            return ResponseModel.buildParameterError();
        }

        // 检查是否已有
        if (GroupFactory.findByName(model.getName()) != null) {
            return ResponseModel.buildHaveNameError();
        }

        List<User> users = new ArrayList<>();
        for (String s : model.getUsers()) {
            User user = UserFactory.findById(s);
            if (user == null)
                continue;
            users.add(user);
        }
        // 没有一个成员
        if (users.size() == 0) {
            return ResponseModel.buildParameterError();
        }

        Group group = GroupFactory.create(creator, model, users);
        if (group == null) {
            // 服务器异常
            return ResponseModel.buildServiceError();
        }

        // 拿管理员的信息（自己的信息）
        GroupMember creatorMember = GroupFactory.getMember(creator.getId(), group.getId());
        if (creatorMember == null) {
            // 服务器异常
            return ResponseModel.buildServiceError();
        }

        // 拿到群的成员，给所有的群成员发送信息，已经被添加到群的信息
        Set<GroupMember> members = GroupFactory.getMembers(group);
        if (members == null) {
            // 服务器异常
            return ResponseModel.buildServiceError();
        }

        members = members.stream()
                .filter(groupMember -> !groupMember.getId().equalsIgnoreCase(creatorMember.getId()))
                .collect(Collectors.toSet());

        // 开始发起推送
        PushFactory.pushJoinGroup(members);

        return ResponseModel.buildOk(new GroupCard(creatorMember));
    }

    /**
     * 查找群，没有传递参数就是搜索最近所有的群
     *
     * @param name 搜索的参数
     * @return 群信息列表
     */
    @GET
    @Path("/search/{name:(.*)?}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseModel<List<GroupCard>> search(@PathParam("name") @DefaultValue("") String name) {
        User self = getSelf();
        List<Group> groups = GroupFactory.search(name);
        if (groups != null && groups.size() > 0) {
            List<GroupCard> groupCards = groups.stream()
                    .map(group -> {
                        GroupMember member = GroupFactory.getMember(self.getId(), group.getId());
                        return new GroupCard(group, member);
                    }).collect(Collectors.toList());
            return ResponseModel.buildOk(groupCards);
        }
        return ResponseModel.buildOk();
    }

    /**
     * 拉取自己当前的群的列表
     *
     * @param dateStr 时间字段，不传递，则返回全部当前的群列表；有时间，则返回这个时间之后的加入的群
     * @return 群信息列表
     */
    @GET
    @Path("/list/{date:(.*)?}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseModel<List<GroupCard>> list(@DefaultValue("") @PathParam("date") String dateStr) {
        User self = getSelf();

        // 拿时间
        LocalDateTime dateTime = null;
        if (!Strings.isNullOrEmpty(dateStr)) {
            try {
                dateTime = LocalDateTime.parse(dateStr, LocalDateTimeConverter.FORMATTER);
            } catch (Exception e) {
                dateTime = null;
            }
        }

        Set<GroupMember> members = GroupFactory.getMembers(self);
        if (members == null || members.size() == 0)
            return ResponseModel.buildOk();


        final LocalDateTime finalDateTime = dateTime;
        List<GroupCard> groupCards = members.stream()
                .filter(groupMember -> finalDateTime == null // 时间如果为null则不做限制
                        || groupMember.getUpdateAt().isAfter(finalDateTime) // 时间不为null,你需要在我的这个时间之后
                )
                .map(GroupCard::new) // 转换操作
                .collect(Collectors.toList());

        return ResponseModel.buildOk(groupCards);
    }

    /**
     * 获取一个群的信息, 你必须是群的成员
     *
     * @param id 群的Id
     * @return 群的信息
     */
    @GET
    @Path("/{groupId}")
    //http:.../api/group/0000-0000-0000-0000
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseModel<GroupCard> getGroup(@PathParam("groupId") String id) {
        if (Strings.isNullOrEmpty(id))
            return ResponseModel.buildParameterError();

        User self = getSelf();
        GroupMember member = GroupFactory.getMember(self.getId(), id);
        if (member == null) {
            return ResponseModel.buildNotFoundGroupError(null);
        }

        return ResponseModel.buildOk(new GroupCard(member));
    }

    /**
     * 拉取一个群的所有成员，你必须是成员之一
     *
     * @param groupId 群id
     * @return 成员列表
     */
    @GET
    @Path("/{groupId}/member")
    //http:.../api/group/0000-0000-0000-0000/member
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseModel<List<GroupMemberCard>> members(@PathParam("groupId") String groupId) {
        User self = getSelf();

        // 没有这个群
        Group group = GroupFactory.findById(groupId);
        if (group == null)
            return ResponseModel.buildNotFoundGroupError(null);

        // 检查权限
        GroupMember selfMember = GroupFactory.getMember(self.getId(), groupId);
        if (selfMember == null)
            return ResponseModel.buildNoPermissionError();

        // 所有的成员
        Set<GroupMember> members = GroupFactory.getMembers(group);
        if (members == null)
            return ResponseModel.buildServiceError();

        // 返回
        List<GroupMemberCard> memberCards = members
                .stream()
                .map(GroupMemberCard::new)
                .collect(Collectors.toList());

        return ResponseModel.buildOk(memberCards);
    }

    /**
     * 给群添加成员的接口
     *
     * @param groupId 群Id，你必须是这个群的管理者之一
     * @param model   添加成员的Model
     * @return 添加成员列表
     */
    @POST
    @Path("/{groupId}/member")
    //http:.../api/group/0000-0000-0000-0000/member
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseModel<List<GroupMemberCard>> memberAdd(@PathParam("groupId") String groupId, GroupMemberAddModel model) {
        if (Strings.isNullOrEmpty(groupId) || !GroupMemberAddModel.check(model))
            return ResponseModel.buildParameterError();

        // 拿到我的信息
        User self = getSelf();

        // 移除我之后再进行判断数量
        model.getUsers().remove(self.getId());
        if (model.getUsers().size() == 0)
            return ResponseModel.buildParameterError();

        // 没有这个群
        Group group = GroupFactory.findById(groupId);
        if (group == null)
            return ResponseModel.buildNotFoundGroupError(null);

        // 我必须是成员, 同时是管理员及其以上级别
        GroupMember selfMember = GroupFactory.getMember(self.getId(), groupId);
        if (selfMember == null || selfMember.getPermissionType() == GroupMember.PERMISSION_TYPE_NONE)
            return ResponseModel.buildNoPermissionError();


        // 已有的成员
        Set<GroupMember> oldMembers = GroupFactory.getMembers(group);
        Set<String> oldMemberUserIds = oldMembers.stream()
                .map(GroupMember::getUserId)
                .collect(Collectors.toSet());


        List<User> insertUsers = new ArrayList<>();
        for (String s : model.getUsers()) {
            // 找人
            User user = UserFactory.findById(s);
            if (user == null)
                continue;
            // 已经在群里了
            if(oldMemberUserIds.contains(user.getId()))
                continue;

            insertUsers.add(user);
        }
        // 没有一个新增的成员
        if (insertUsers.size() == 0) {
            return ResponseModel.buildParameterError();
        }

        // 进行添加操作
        Set<GroupMember> insertMembers =  GroupFactory.addMembers(group, insertUsers);
        if(insertMembers==null)
            return ResponseModel.buildServiceError();


        // 转换
        List<GroupMemberCard> insertCards = insertMembers.stream()
                .map(GroupMemberCard::new)
                .collect(Collectors.toList());

        // 通知，两部曲
        // 1.通知新增的成员，你被加入了XXX群
        PushFactory.pushJoinGroup(insertMembers);

        // 2.通知群中老的成员，有XXX，XXX加入群
        PushFactory.pushGroupMemberAdd(oldMembers, insertCards);

        return ResponseModel.buildOk(insertCards);
    }


    /**
     * 更改成员信息，请求的人要么是管理员，要么就是成员本人
     *
     * @param memberId 成员Id，可以查询对应的群，和人
     * @param model    修改的Model
     * @return 当前成员的信息
     */
    @PUT
    @Path("/member/{memberId}")
    //http:.../api/group/member/0000-0000-0000-0000
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseModel<GroupMemberCard> modifyMember(@PathParam("memberId") String memberId, GroupMemberUpdateModel model) {
        return null;
    }


    /**
     * 申请加入一个群，
     * 此时会创建一个加入的申请，并写入表；然后会给管理员发送消息
     * 管理员同意，其实就是调用添加成员的接口把对应的用户添加进去
     *
     * @param groupId 群Id
     * @return 申请的信息
     */
    @POST
    @Path("/applyJoin/{groupId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseModel<ApplyCard> join(@PathParam("groupId") String groupId) {
        return null;
    }

}
