package com.sismics.docs.rest.resource;

import com.google.common.base.Strings;
import com.sismics.docs.core.dao.UserChatDao;
import com.sismics.docs.core.dao.UserDao;
import com.sismics.docs.core.dao.criteria.UserChatCriteria;
import com.sismics.docs.core.dao.dto.UserChatDto;
import com.sismics.docs.core.model.jpa.User;
import com.sismics.docs.core.model.jpa.UserChat;
import com.sismics.docs.core.model.jpa.UserRegistration;
import com.sismics.docs.core.util.jpa.SortCriteria;
import com.sismics.rest.exception.ClientException;
import com.sismics.rest.exception.ForbiddenClientException;
import com.sismics.rest.util.ValidationUtil;
import com.sismics.util.JsonUtil;

import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObjectBuilder;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

/**
 * User chat REST resources.
 */
@Path("/user")
public class UserChatResource extends BaseResource {
    /**
     * 发送聊天消息
     * @api {post} /user/chat/:username 发送消息
     */
    @PUT
    @Path("{username: [a-zA-Z0-9_@.-]+}/chat")
//    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response sendMessage(
            @PathParam("username") String username,
            @FormParam("message") String message) {
        // 验证用户登录
        if (!authenticate()) {
            throw new ForbiddenClientException();
        }

        // 校验参数
//        receiver = ValidationUtil.validateStringNotBlank(receiver, "receiver");
        message = ValidationUtil.validateLength(message, "message", 1, 4000);

        // 检查接收用户是否存在
        UserDao userDao = new UserDao();
        User receiverUser = userDao.getActiveByUsername(username);
        if (receiverUser == null) {
            throw new ClientException("UserNotFound", "Receiver user not found");
        }

        // 创建聊天记录
        UserChat chat = new UserChat()
                .setSenderName(principal.getName())
                .setReceiverName(username)
                .setMessage(message);

        UserChatDao userChatDao = new UserChatDao();
        userChatDao.create(chat);

        // Always return OK
        JsonObjectBuilder response = Json.createObjectBuilder()
                .add("status", "ok");
        return Response.ok().entity(response.build()).build();
    }

    /**
     * 获取与指定用户的聊天记录
     * @api {get} /user/chat/:username 获取聊天记录
     */
    @GET
    @Path("{username: [a-zA-Z0-9_@.-]+}/chat")
//    @Produces(MediaType.APPLICATION_JSON)
    public Response getChatHistory(
            @PathParam("username") String targetUser) {
        // 验证用户登录
        if (!authenticate()) {
            throw new ForbiddenClientException();
        }

        // 校验对方用户存在
        UserDao userDao = new UserDao();
        User target = userDao.getActiveByUsername(targetUser);
        if (target == null) {
            throw new ClientException("UserNotFound", "Target user not found");
        }


        // 构建查询条件（双向查询）
        UserChatCriteria criteria = new UserChatCriteria()
                .setSenderName(principal.getName())
                .setReceiverName(targetUser);

        // 添加反向条件
        UserChatCriteria reverseCriteria = new UserChatCriteria()
                .setSenderName(targetUser)
                .setReceiverName(principal.getName());

        // 组合查询条件
        criteria.setCombinedCriteria(reverseCriteria);

        // 设置分页和排序
        SortCriteria sortCriteria = new SortCriteria(4, null);

        // 执行查询
        UserChatDao userChatDao = new UserChatDao();
        List<UserChatDto> chatList = userChatDao.findByCriteria(criteria, sortCriteria);

        JsonArrayBuilder userHistory = Json.createArrayBuilder();

        for (UserChatDto userChatDto : chatList) {
            userHistory.add(Json.createObjectBuilder()
                    .add("id", userChatDto.getId())
                    .add("sender", userChatDto.getSenderName())
                    .add("receiver", userChatDto.getReceiverName())
                    .add("message", userChatDto.getMessage())
                    .add("time",userChatDto.getSendTimestamp()));
        }

        // 构建响应
        JsonObjectBuilder response = Json.createObjectBuilder()
                .add("userHistory", userHistory);
        return Response.ok().entity(response.build()).build();
    }
}