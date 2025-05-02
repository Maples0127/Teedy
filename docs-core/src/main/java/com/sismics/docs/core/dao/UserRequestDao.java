package com.sismics.docs.core.dao;

import com.sismics.docs.core.dao.criteria.UserRequestCriteria;
import com.sismics.docs.core.dao.dto.UserRequestDto;
import com.sismics.docs.core.model.jpa.UserRequest;
import com.sismics.util.context.ThreadLocalContext;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.Timestamp;
import java.util.*;

public class UserRequestDao {
    private static final Logger log = LoggerFactory.getLogger(UserRequestDao.class);
    /**
     * 创建注册请求
     */
    public String create(UserRequest userRequest) {
        EntityManager em = ThreadLocalContext.get().getEntityManager();

        // 检查用户名唯一性（仿照UserDao.create()）
        Query q = em.createQuery("select ur from UserRequest ur where ur.username = :username");
        q.setParameter("username", userRequest.getUsername());
        if (!q.getResultList().isEmpty()) {
            throw new RuntimeException("Username already exists");
        }

        // 设置默认值（仿照UserDao.create()）
        userRequest.setId(UUID.randomUUID().toString());
        userRequest.setCreateDate(new Date());
        userRequest.setStatus(UserRequest.Status.PENDING);

        em.persist(userRequest);
        return userRequest.getId();
    }

    /**
     * 根据条件查询请求（仿照UserDao.findByCriteria()）
     */
    public List<UserRequestDto> findByCriteria(UserRequestCriteria criteria) {
        EntityManager em = ThreadLocalContext.get().getEntityManager();
        StringBuilder sb = new StringBuilder("select ur.URQ_ID_C, ur.URQ_USERNAME_C, ur.URQ_EMAIL_C, ur.URQ_CREATEDATE_D, ur.URQ_STATUS_C from T_USER_REQUEST ur");

        // 构建查询条件...

        Query query = em.createNativeQuery(sb.toString());
        List<Object[]> resultList = query.getResultList();

        List<UserRequestDto> dtoList = new ArrayList<>();
        for (Object[] o : resultList) {
            UserRequestDto dto = new UserRequestDto();
            dto.setId((String) o[0]);
            dto.setUsername((String) o[1]);
            dto.setEmail((String) o[2]);
            dto.setCreateTimestamp(((Timestamp) o[3]).getTime());
            dto.setStatus((String) o[4]);
            dtoList.add(dto);
        }
        return dtoList;
    }

    /**
     * 更新请求状态（仿照UserDao.update()）
     */
    public void updateStatus(String requestId, UserRequest.Status status) {
        EntityManager em = ThreadLocalContext.get().getEntityManager();
        UserRequest userRequest = em.find(UserRequest.class, requestId);
        if (userRequest != null) {
            userRequest.setStatus(status);
        }
    }
}