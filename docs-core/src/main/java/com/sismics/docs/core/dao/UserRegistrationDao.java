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
            throw new RuntimeException("AlreadyExistingUsernameInUserRegistration");
        }
        Query q1 = em.createQuery("select u from User u where u.username = :username and u.deleteDate is null");
        q1.setParameter("username", userRegistration.getUsername());
        if (!q1.getResultList().isEmpty()) {
            throw new Exception("AlreadyExistingUsername");
        }

        // 设置默认值（仿照UserDao.create()）
        userRegistration.setId(UUID.randomUUID().toString());
        userRegistration.setCreateDate(new Date());
//        userRegistration.setPassword(userRegistration.getPassword());
//        userRegistration.setStorageCurrent(0L);
        userRegistration.setStatus("pending");

        em.persist(userRegistration);
        return userRegistration.getId();
    }

//    /**
//     * 根据条件查询请求（仿照UserDao.findByCriteria()）
//     */
//    public List<UserRegistrationDto> findByCriteria(UserRegistrationCriteria criteria) {
//        EntityManager em = ThreadLocalContext.get().getEntityManager();
//        StringBuilder sb = new StringBuilder("select ur.URQ_ID_C, ur.URQ_USERNAME_C, ur.URQ_EMAIL_C, ur.URQ_CREATEDATE_D, ur.URQ_STATUS_C from T_USER_REQUEST ur");
//
//        // 构建查询条件...
//
//        Query query = em.createNativeQuery(sb.toString());
//        List<Object[]> resultList = query.getResultList();
//
//        List<UserRegistrationDto> dtoList = new ArrayList<>();
//        for (Object[] o : resultList) {
//            UserRegistrationDto dto = new UserRegistrationDto();
//            dto.setId((String) o[0]);
//            dto.setUsername((String) o[1]);
//            dto.setEmail((String) o[2]);
//            dto.setCreateTimestamp(((Timestamp) o[3]).getTime());
//            dto.setStatus((String) o[4]);
//            dtoList.add(dto);
//        }
//        return dtoList;
//    }

    /**
     * 根据条件查询请求（仿照UserDao.findByCriteria()）
     */
    public List<UserRegistration> findByCriteria(UserRegistrationCriteria criteria, SortCriteria sortCriteria) {
        Map<String, Object> parameterMap = new HashMap<>();
        List<String> criteriaList = new ArrayList<>();

        StringBuilder sb = new StringBuilder("SELECT ur.URQ_ID_C, ur.URQ_USERNAME_C, ur.URQ_EMAIL_C, ur.URQ_CREATEDATE_D, ur.URQ_STATUS_C FROM T_USER_REQUEST ur");

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
            sb.append(" WHERE ");
            sb.append(String.join(" AND ", criteriaList));
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
            userRegistration.setStatus((String) o[i++]);
            urList.add(userRegistration);
        }
        return urList;
    }

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

    public void delete(String username, String id) {
        EntityManager em = ThreadLocalContext.get().getEntityManager();

        // Get the user
        Query q = em.createQuery("select ur from UserRegistration ur where ur.username = :username and ur.deleteDate is null");
        q.setParameter("username", username);
        UserRegistration userRegistrationDb = (UserRegistration) q.getSingleResult();

        // Delete the user
        Date dateNow = new Date();
        userRegistrationDb.setDeleteDate(dateNow);

        // Create audit log
        AuditLogUtil.create(userRegistrationDb, AuditLogType.DELETE, id);
    }

    public UserRegistration update(UserRegistration registration, String id) {
        EntityManager em = ThreadLocalContext.get().getEntityManager();

        // Get the user
        Query q = em.createQuery("select u from User u where u.id = :id and u.deleteDate is null");
        q.setParameter("id", registration.getId());
        UserRegistration userRegistrationDb = (UserRegistration) q.getSingleResult();

        // Update the user (except password)
        userRegistrationDb.setEmail(registration.getEmail());
//        userRegistrationDb.setStorageQuota(registration.getStorageQuota());
//        userDb.setStorageCurrent(registration.getStorageCurrent());
//        userDb.setTotpKey(registration.getTotpKey());
        userRegistrationDb.setDisableDate(registration.getDisableDate());

        // Create audit log
        AuditLogUtil.create(userRegistrationDb, AuditLogType.UPDATE, id);

        return registration;
    }

//    public UserRegistration updatePassword(UserRegistration registration, String id) {
//        EntityManager em = ThreadLocalContext.get().getEntityManager();
//
//        // Get the user
//        Query q = em.createQuery("select u from User u where u.id = :id and u.deleteDate is null");
//        q.setParameter("id", registration.getId());
//        UserRegistration userRegistrationDb = (UserRegistration) q.getSingleResult();
//
//        // Update the user
//        userRegistrationDb.setPassword(registration.getPassword());
//
//        // Create audit log
//        AuditLogUtil.create(userRegistrationDb, AuditLogType.UPDATE, id);
//
//        return registration;
//    }
}