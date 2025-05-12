package com.sismics.docs.core.dao;

import com.sismics.docs.core.constant.AuditLogType;
import com.sismics.docs.core.dao.criteria.UserRegistrationCriteria;
import com.sismics.docs.core.model.jpa.UserRegistration;
import com.sismics.docs.core.util.AuditLogUtil;
import com.sismics.docs.core.util.jpa.QueryParam;
import com.sismics.docs.core.util.jpa.QueryUtil;
import com.sismics.util.context.ThreadLocalContext;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.Timestamp;
import java.util.*;
import com.sismics.docs.core.util.jpa.SortCriteria;

public class UserRegistrationDao {
    private static final Logger log = LoggerFactory.getLogger(UserRegistrationDao.class);
    /**
     * 创建注册请求
     */
    public String create(UserRegistration userRegistration) throws Exception {
        EntityManager em = ThreadLocalContext.get().getEntityManager();

        // 检查用户名唯一性（仿照UserDao.create()）
        Query q = em.createQuery("select ur from UserRegistration ur where ur.username = :username and ur.deleteDate is null");
        q.setParameter("username", userRegistration.getUsername());
        if (!q.getResultList().isEmpty()) {
            throw new Exception("AlreadyExistingUsername");
        }
        Query q1 = em.createQuery("select u from User u where u.username = :username and u.deleteDate is null");
        q1.setParameter("username", userRegistration.getUsername());
        if (!q1.getResultList().isEmpty()) {
            throw new Exception("AlreadyExistingUsername");
        }

        // 设置默认值（仿照UserDao.create()）
        userRegistration.setId(UUID.randomUUID().toString());
        userRegistration.setCreateDate(new Date());
        userRegistration.setStatus("pending");

        em.persist(userRegistration);
        return userRegistration.getId();
    }

    /**
     * 根据条件查询请求（仿照UserDao.findByCriteria()）
     */
    public List<UserRegistration> findByCriteria(UserRegistrationCriteria criteria, SortCriteria sortCriteria) {
        Map<String, Object> parameterMap = new HashMap<>();
        List<String> criteriaList = new ArrayList<>();

        StringBuilder sb = new StringBuilder(
                "SELECT ur.URQ_ID_C as c0, ur.URQ_USERNAME_C as c1, ur.URQ_EMAIL_C as c2, ur.URQ_CREATEDATE_D as c3, ur.URQ_STATUS_C as c4" +
                        " FROM T_USER_REGISTRATION ur");

        // 添加搜索条件
        if (criteria.getSearch() != null) {
            criteriaList.add("(LOWER(ur.URQ_USERNAME_C) LIKE LOWER(:search) OR LOWER(ur.URQ_EMAIL_C) LIKE LOWER(:search))");
            parameterMap.put("search", "%" + criteria.getSearch() + "%");
        }
        if (criteria.getUserName() != null) {
            criteriaList.add("ur.URQ_USERNAME_C = :username");
            parameterMap.put("username", criteria.getUserName());
        }
        if (criteria.getStatus() != null) {
            criteriaList.add("ur.URQ_STATUS_C = :status");
            parameterMap.put("status", criteria.getStatus());
        }

        // 添加固定条件（例如：未被删除的记录，假设存在URQ_DELETEDATE_D字段）
        criteriaList.add("ur.URQ_DELETEDATE_D IS NULL");

        // 拼接WHERE子句
        if (!criteriaList.isEmpty()) {
            sb.append(" WHERE ").append(String.join(" AND ", criteriaList));
        }

        // 处理排序
        QueryParam queryParam = QueryUtil.getSortedQueryParam(new QueryParam(sb.toString(), parameterMap), sortCriteria);

        // 执行查询
//        EntityManager em = ThreadLocalContext.get().getEntityManager();
        @SuppressWarnings("unchecked")
        List<Object[]> resultList = QueryUtil.getNativeQuery(queryParam).getResultList();

        // 转换为DTO列表
        List<UserRegistration> urList = new ArrayList<>();
        for (Object[] o : resultList) {
            int i = 0;
            UserRegistration userRegistration = new UserRegistration();
            userRegistration.setId((String) o[i++]);
            userRegistration.setUsername((String) o[i++]);
            userRegistration.setEmail((String) o[i++]);
            userRegistration.setCreateDate(((Date) o[i++]));
            userRegistration.setStatus((String) o[i]);
            urList.add(userRegistration);
        }
        return urList;
    }

//    public List<UserRegistration> getUserRegistrations() {
//        EntityManager em = ThreadLocalContext.get().getEntityManager();
//        TypedQuery<UserRegistration> q = em.createQuery("select ur from UserRegistration ur", UserRegistration.class);
//        return q.getResultList();
//    }


    /**
     * 更新请求状态（仿照UserDao.update()）
     */
    public UserRegistration updateStatus(UserRegistration userRegistration, String status) {
        EntityManager em = ThreadLocalContext.get().getEntityManager();

        Query q = em.createQuery("select ur from UserRegistration ur where ur.username = :username and ur.deleteDate is null");
        q.setParameter("username", userRegistration.getUsername());
        UserRegistration userRegistrationDb = (UserRegistration) q.getSingleResult();

        userRegistrationDb.setStatus(status);

        return userRegistration;
    }

    public UserRegistration getUserRegistrationByURN(String userRegistrationName) {
        EntityManager em = ThreadLocalContext.get().getEntityManager();
        try {
            Query q = em.createQuery("select ur from UserRegistration ur where ur.username = :username and ur.deleteDate is null");
            q.setParameter("username", userRegistrationName);
            return (UserRegistration) q.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
}