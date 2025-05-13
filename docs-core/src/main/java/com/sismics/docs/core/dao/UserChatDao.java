// UserChatDao.java
package com.sismics.docs.core.dao;

import com.google.common.base.Joiner;
import com.sismics.docs.core.dao.criteria.UserChatCriteria;
import com.sismics.docs.core.dao.dto.UserChatDto;
import com.sismics.docs.core.model.jpa.UserChat;
import com.sismics.docs.core.util.jpa.QueryParam;
import com.sismics.docs.core.util.jpa.QueryUtil;
import com.sismics.docs.core.util.jpa.SortCriteria;
import com.sismics.util.context.ThreadLocalContext;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.Query;
import java.sql.Timestamp;
import java.util.*;

/**
 * User chat DAO.
 */
public class UserChatDao {
    /**
     * Creates a new chat message.
     *
     * @param userChat Chat message to create
     * @return Chat ID
     */
    public String create(UserChat userChat) {
        // Create the chat ID
        userChat.setId(UUID.randomUUID().toString());

        // Set send date
        userChat.setSendDate(new Date());

        EntityManager em = ThreadLocalContext.get().getEntityManager();
        em.persist(userChat);

        return userChat.getId();
    }

//    /**
//     * Gets a chat message by ID.
//     *
//     * @param id Chat ID
//     * @return Chat message
//     */
//    public UserChat getById(String id) {
//        EntityManager em = ThreadLocalContext.get().getEntityManager();
//        try {
//            return em.find(UserChat.class, id);
//        } catch (NoResultException e) {
//            return null;
//        }
//    }
//
//    /**
//     * Deletes a chat message.
//     *
//     * @param id Chat ID
//     */
//    public void delete(String id) {
//        EntityManager em = ThreadLocalContext.get().getEntityManager();
//        UserChat userChat = em.find(UserChat.class, id);
//        if (userChat != null) {
//            em.remove(userChat);
//        }
//    }

    /**
     * Returns the list of chat messages matching criteria.
     *
     * @param criteria Search criteria
     * @param sortCriteria Sort criteria
     * @return List of chat messages
     */
    public List<UserChatDto> findByCriteria(UserChatCriteria criteria, SortCriteria sortCriteria) {
        Map<String, Object> parameterMap = new HashMap<>();
        List<String> criteriaList = new ArrayList<>();

        StringBuilder sb = new StringBuilder("select uc.UCH_ID_C as c0, uc.UCH_SENDER_NAME_C as c1, uc.UCH_RECEIVER_NAME_C as c2, uc.UCH_MESSAGE_C as c3, uc.UCH_SENDDATE_D as c4");
        sb.append(" from T_USER_CHAT uc ");


        // 主条件：发送者=A 且 接收者=B
        if (criteria.getSenderName() != null && criteria.getReceiverName() != null) {
            criteriaList.add("(uc.UCH_SENDER_NAME_C = :senderName AND uc.UCH_RECEIVER_NAME_C = :receiverName)");
            parameterMap.put("senderName", criteria.getSenderName());
            parameterMap.put("receiverName", criteria.getReceiverName());
        }

        // 组合条件：发送者=B 且 接收者=A（反向匹配）
        if (criteria.getCombinedCriteria() != null) {
            criteriaList.add("(uc.UCH_SENDER_NAME_C = :combinedSender AND uc.UCH_RECEIVER_NAME_C = :combinedReceiver)");
            parameterMap.put("combinedSender", criteria.getCombinedCriteria().getSenderName());
            parameterMap.put("combinedReceiver", criteria.getCombinedCriteria().getReceiverName());
        }

        // 构建 WHERE 子句
        if (!criteriaList.isEmpty()) {
            sb.append(" where ");
            sb.append(Joiner.on(" OR ").join(criteriaList)); // 正确使用 OR 连接
        }

        QueryParam queryParam = QueryUtil.getSortedQueryParam(new QueryParam(sb.toString(), parameterMap), sortCriteria);

        @SuppressWarnings("unchecked")
        List<Object[]> l = QueryUtil.getNativeQuery(queryParam).getResultList();

        // Assemble results
        List<UserChatDto> dtoList = new ArrayList<>();
        for (Object[] o : l) {
            int i = 0;
            UserChatDto dto = new UserChatDto();
            dto.setId((String) o[i++]);
            dto.setSenderName((String) o[i++]);
            dto.setReceiverName((String) o[i++]);
            dto.setMessage((String) o[i++]);
            dto.setSendTimestamp(((Timestamp) o[i]).getTime());
            dtoList.add(dto);
        }
        return dtoList;
    }
}



