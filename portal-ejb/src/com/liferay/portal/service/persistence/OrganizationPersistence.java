/**
 * Copyright (c) 2000-2006 Liferay, LLC. All rights reserved.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.liferay.portal.service.persistence;

import com.liferay.portal.NoSuchOrganizationException;
import com.liferay.portal.SystemException;
import com.liferay.portal.model.Organization;
import com.liferay.portal.service.persistence.BasePersistence;
import com.liferay.portal.spring.hibernate.HibernateUtil;

import com.liferay.util.StringPool;
import com.liferay.util.dao.hibernate.OrderByComparator;
import com.liferay.util.dao.hibernate.QueryPos;
import com.liferay.util.dao.hibernate.QueryUtil;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;

import org.springframework.dao.DataAccessException;

import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.object.MappingSqlQuery;
import org.springframework.jdbc.object.SqlUpdate;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import java.util.Iterator;
import java.util.List;

/**
 * <a href="OrganizationPersistence.java.html"><b><i>View Source</i></b></a>
 *
 * @author  Brian Wing Shun Chan
 *
 */
public class OrganizationPersistence extends BasePersistence {
	public Organization create(String organizationId) {
		Organization organization = new Organization();
		organization.setNew(true);
		organization.setPrimaryKey(organizationId);

		return organization;
	}

	public Organization remove(String organizationId)
		throws NoSuchOrganizationException, SystemException {
		Session session = null;

		try {
			session = openSession();

			Organization organization = (Organization)session.get(Organization.class,
					organizationId);

			if (organization == null) {
				if (_log.isWarnEnabled()) {
					_log.warn("No Organization exists with the primary key " +
						organizationId.toString());
				}

				throw new NoSuchOrganizationException(
					"No Organization exists with the primary key " +
					organizationId.toString());
			}

			return remove(organization);
		}
		catch (HibernateException he) {
			throw new SystemException(he);
		}
		finally {
			closeSession(session);
		}
	}

	public Organization remove(Organization organization)
		throws SystemException {
		Session session = null;

		try {
			session = openSession();
			session.delete(organization);
			session.flush();
			clearGroups.clear(organization.getPrimaryKey());
			clearUsers.clear(organization.getPrimaryKey());
			OrganizationPool.removeByC_N(organization.getCompanyId(),
				organization.getName());

			return organization;
		}
		catch (HibernateException he) {
			throw new SystemException(he);
		}
		finally {
			closeSession(session);
		}
	}

	public com.liferay.portal.model.Organization update(
		com.liferay.portal.model.Organization organization)
		throws SystemException {
		Session session = null;

		try {
			session = openSession();
			session.saveOrUpdate(organization);
			session.flush();
			organization.setNew(false);

			return organization;
		}
		catch (HibernateException he) {
			throw new SystemException(he);
		}
		finally {
			closeSession(session);
		}
	}

	public Organization findByPrimaryKey(String organizationId)
		throws NoSuchOrganizationException, SystemException {
		Organization organization = fetchByPrimaryKey(organizationId);

		if (organization == null) {
			if (_log.isWarnEnabled()) {
				_log.warn("No Organization exists with the primary key " +
					organizationId.toString());
			}

			throw new NoSuchOrganizationException(
				"No Organization exists with the primary key " +
				organizationId.toString());
		}

		return organization;
	}

	public Organization fetchByPrimaryKey(String organizationId)
		throws SystemException {
		Session session = null;

		try {
			session = openSession();

			return (Organization)session.get(Organization.class, organizationId);
		}
		catch (HibernateException he) {
			throw new SystemException(he);
		}
		finally {
			closeSession(session);
		}
	}

	public List findByCompanyId(String companyId) throws SystemException {
		Session session = null;

		try {
			session = openSession();

			StringBuffer query = new StringBuffer();
			query.append("FROM com.liferay.portal.model.Organization WHERE ");

			if (companyId == null) {
				query.append("companyId IS NULL");
			}
			else {
				query.append("companyId = ?");
			}

			query.append(" ");
			query.append("ORDER BY ");
			query.append("name ASC");

			Query q = session.createQuery(query.toString());
			int queryPos = 0;

			if (companyId != null) {
				q.setString(queryPos++, companyId);
			}

			return q.list();
		}
		catch (HibernateException he) {
			throw new SystemException(he);
		}
		finally {
			closeSession(session);
		}
	}

	public List findByCompanyId(String companyId, int begin, int end)
		throws SystemException {
		return findByCompanyId(companyId, begin, end, null);
	}

	public List findByCompanyId(String companyId, int begin, int end,
		OrderByComparator obc) throws SystemException {
		Session session = null;

		try {
			session = openSession();

			StringBuffer query = new StringBuffer();
			query.append("FROM com.liferay.portal.model.Organization WHERE ");

			if (companyId == null) {
				query.append("companyId IS NULL");
			}
			else {
				query.append("companyId = ?");
			}

			query.append(" ");

			if (obc != null) {
				query.append("ORDER BY " + obc.getOrderBy());
			}
			else {
				query.append("ORDER BY ");
				query.append("name ASC");
			}

			Query q = session.createQuery(query.toString());
			int queryPos = 0;

			if (companyId != null) {
				q.setString(queryPos++, companyId);
			}

			return QueryUtil.list(q, getDialect(), begin, end);
		}
		catch (HibernateException he) {
			throw new SystemException(he);
		}
		finally {
			closeSession(session);
		}
	}

	public Organization findByCompanyId_First(String companyId,
		OrderByComparator obc)
		throws NoSuchOrganizationException, SystemException {
		List list = findByCompanyId(companyId, 0, 1, obc);

		if (list.size() == 0) {
			String msg = "No Organization exists with the key ";
			msg += StringPool.OPEN_CURLY_BRACE;
			msg += "companyId=";
			msg += companyId;
			msg += StringPool.CLOSE_CURLY_BRACE;
			throw new NoSuchOrganizationException(msg);
		}
		else {
			return (Organization)list.get(0);
		}
	}

	public Organization findByCompanyId_Last(String companyId,
		OrderByComparator obc)
		throws NoSuchOrganizationException, SystemException {
		int count = countByCompanyId(companyId);
		List list = findByCompanyId(companyId, count - 1, count, obc);

		if (list.size() == 0) {
			String msg = "No Organization exists with the key ";
			msg += StringPool.OPEN_CURLY_BRACE;
			msg += "companyId=";
			msg += companyId;
			msg += StringPool.CLOSE_CURLY_BRACE;
			throw new NoSuchOrganizationException(msg);
		}
		else {
			return (Organization)list.get(0);
		}
	}

	public Organization[] findByCompanyId_PrevAndNext(String organizationId,
		String companyId, OrderByComparator obc)
		throws NoSuchOrganizationException, SystemException {
		Organization organization = findByPrimaryKey(organizationId);
		int count = countByCompanyId(companyId);
		Session session = null;

		try {
			session = openSession();

			StringBuffer query = new StringBuffer();
			query.append("FROM com.liferay.portal.model.Organization WHERE ");

			if (companyId == null) {
				query.append("companyId IS NULL");
			}
			else {
				query.append("companyId = ?");
			}

			query.append(" ");

			if (obc != null) {
				query.append("ORDER BY " + obc.getOrderBy());
			}
			else {
				query.append("ORDER BY ");
				query.append("name ASC");
			}

			Query q = session.createQuery(query.toString());
			int queryPos = 0;

			if (companyId != null) {
				q.setString(queryPos++, companyId);
			}

			Object[] objArray = QueryUtil.getPrevAndNext(q, count, obc,
					organization);
			Organization[] array = new Organization[3];
			array[0] = (Organization)objArray[0];
			array[1] = (Organization)objArray[1];
			array[2] = (Organization)objArray[2];

			return array;
		}
		catch (HibernateException he) {
			throw new SystemException(he);
		}
		finally {
			closeSession(session);
		}
	}

	public List findByLocations(String companyId) throws SystemException {
		Session session = null;

		try {
			session = openSession();

			StringBuffer query = new StringBuffer();
			query.append("FROM com.liferay.portal.model.Organization WHERE ");

			if (companyId == null) {
				query.append("companyId IS NULL");
			}
			else {
				query.append("companyId = ?");
			}

			query.append(" AND parentOrganizationId != '-1' ");
			query.append("ORDER BY ");
			query.append("name ASC");

			Query q = session.createQuery(query.toString());
			int queryPos = 0;

			if (companyId != null) {
				q.setString(queryPos++, companyId);
			}

			return q.list();
		}
		catch (HibernateException he) {
			throw new SystemException(he);
		}
		finally {
			closeSession(session);
		}
	}

	public List findByLocations(String companyId, int begin, int end)
		throws SystemException {
		return findByLocations(companyId, begin, end, null);
	}

	public List findByLocations(String companyId, int begin, int end,
		OrderByComparator obc) throws SystemException {
		Session session = null;

		try {
			session = openSession();

			StringBuffer query = new StringBuffer();
			query.append("FROM com.liferay.portal.model.Organization WHERE ");

			if (companyId == null) {
				query.append("companyId IS NULL");
			}
			else {
				query.append("companyId = ?");
			}

			query.append(" AND parentOrganizationId != '-1' ");

			if (obc != null) {
				query.append("ORDER BY " + obc.getOrderBy());
			}
			else {
				query.append("ORDER BY ");
				query.append("name ASC");
			}

			Query q = session.createQuery(query.toString());
			int queryPos = 0;

			if (companyId != null) {
				q.setString(queryPos++, companyId);
			}

			return QueryUtil.list(q, getDialect(), begin, end);
		}
		catch (HibernateException he) {
			throw new SystemException(he);
		}
		finally {
			closeSession(session);
		}
	}

	public Organization findByLocations_First(String companyId,
		OrderByComparator obc)
		throws NoSuchOrganizationException, SystemException {
		List list = findByLocations(companyId, 0, 1, obc);

		if (list.size() == 0) {
			String msg = "No Organization exists with the key ";
			msg += StringPool.OPEN_CURLY_BRACE;
			msg += "companyId=";
			msg += companyId;
			msg += StringPool.CLOSE_CURLY_BRACE;
			throw new NoSuchOrganizationException(msg);
		}
		else {
			return (Organization)list.get(0);
		}
	}

	public Organization findByLocations_Last(String companyId,
		OrderByComparator obc)
		throws NoSuchOrganizationException, SystemException {
		int count = countByLocations(companyId);
		List list = findByLocations(companyId, count - 1, count, obc);

		if (list.size() == 0) {
			String msg = "No Organization exists with the key ";
			msg += StringPool.OPEN_CURLY_BRACE;
			msg += "companyId=";
			msg += companyId;
			msg += StringPool.CLOSE_CURLY_BRACE;
			throw new NoSuchOrganizationException(msg);
		}
		else {
			return (Organization)list.get(0);
		}
	}

	public Organization[] findByLocations_PrevAndNext(String organizationId,
		String companyId, OrderByComparator obc)
		throws NoSuchOrganizationException, SystemException {
		Organization organization = findByPrimaryKey(organizationId);
		int count = countByLocations(companyId);
		Session session = null;

		try {
			session = openSession();

			StringBuffer query = new StringBuffer();
			query.append("FROM com.liferay.portal.model.Organization WHERE ");

			if (companyId == null) {
				query.append("companyId IS NULL");
			}
			else {
				query.append("companyId = ?");
			}

			query.append(" AND parentOrganizationId != '-1' ");

			if (obc != null) {
				query.append("ORDER BY " + obc.getOrderBy());
			}
			else {
				query.append("ORDER BY ");
				query.append("name ASC");
			}

			Query q = session.createQuery(query.toString());
			int queryPos = 0;

			if (companyId != null) {
				q.setString(queryPos++, companyId);
			}

			Object[] objArray = QueryUtil.getPrevAndNext(q, count, obc,
					organization);
			Organization[] array = new Organization[3];
			array[0] = (Organization)objArray[0];
			array[1] = (Organization)objArray[1];
			array[2] = (Organization)objArray[2];

			return array;
		}
		catch (HibernateException he) {
			throw new SystemException(he);
		}
		finally {
			closeSession(session);
		}
	}

	public List findByC_P(String companyId, String parentOrganizationId)
		throws SystemException {
		Session session = null;

		try {
			session = openSession();

			StringBuffer query = new StringBuffer();
			query.append("FROM com.liferay.portal.model.Organization WHERE ");

			if (companyId == null) {
				query.append("companyId IS NULL");
			}
			else {
				query.append("companyId = ?");
			}

			query.append(" AND ");

			if (parentOrganizationId == null) {
				query.append("parentOrganizationId IS NULL");
			}
			else {
				query.append("parentOrganizationId = ?");
			}

			query.append(" ");
			query.append("ORDER BY ");
			query.append("name ASC");

			Query q = session.createQuery(query.toString());
			int queryPos = 0;

			if (companyId != null) {
				q.setString(queryPos++, companyId);
			}

			if (parentOrganizationId != null) {
				q.setString(queryPos++, parentOrganizationId);
			}

			return q.list();
		}
		catch (HibernateException he) {
			throw new SystemException(he);
		}
		finally {
			closeSession(session);
		}
	}

	public List findByC_P(String companyId, String parentOrganizationId,
		int begin, int end) throws SystemException {
		return findByC_P(companyId, parentOrganizationId, begin, end, null);
	}

	public List findByC_P(String companyId, String parentOrganizationId,
		int begin, int end, OrderByComparator obc) throws SystemException {
		Session session = null;

		try {
			session = openSession();

			StringBuffer query = new StringBuffer();
			query.append("FROM com.liferay.portal.model.Organization WHERE ");

			if (companyId == null) {
				query.append("companyId IS NULL");
			}
			else {
				query.append("companyId = ?");
			}

			query.append(" AND ");

			if (parentOrganizationId == null) {
				query.append("parentOrganizationId IS NULL");
			}
			else {
				query.append("parentOrganizationId = ?");
			}

			query.append(" ");

			if (obc != null) {
				query.append("ORDER BY " + obc.getOrderBy());
			}
			else {
				query.append("ORDER BY ");
				query.append("name ASC");
			}

			Query q = session.createQuery(query.toString());
			int queryPos = 0;

			if (companyId != null) {
				q.setString(queryPos++, companyId);
			}

			if (parentOrganizationId != null) {
				q.setString(queryPos++, parentOrganizationId);
			}

			return QueryUtil.list(q, getDialect(), begin, end);
		}
		catch (HibernateException he) {
			throw new SystemException(he);
		}
		finally {
			closeSession(session);
		}
	}

	public Organization findByC_P_First(String companyId,
		String parentOrganizationId, OrderByComparator obc)
		throws NoSuchOrganizationException, SystemException {
		List list = findByC_P(companyId, parentOrganizationId, 0, 1, obc);

		if (list.size() == 0) {
			String msg = "No Organization exists with the key ";
			msg += StringPool.OPEN_CURLY_BRACE;
			msg += "companyId=";
			msg += companyId;
			msg += ", ";
			msg += "parentOrganizationId=";
			msg += parentOrganizationId;
			msg += StringPool.CLOSE_CURLY_BRACE;
			throw new NoSuchOrganizationException(msg);
		}
		else {
			return (Organization)list.get(0);
		}
	}

	public Organization findByC_P_Last(String companyId,
		String parentOrganizationId, OrderByComparator obc)
		throws NoSuchOrganizationException, SystemException {
		int count = countByC_P(companyId, parentOrganizationId);
		List list = findByC_P(companyId, parentOrganizationId, count - 1,
				count, obc);

		if (list.size() == 0) {
			String msg = "No Organization exists with the key ";
			msg += StringPool.OPEN_CURLY_BRACE;
			msg += "companyId=";
			msg += companyId;
			msg += ", ";
			msg += "parentOrganizationId=";
			msg += parentOrganizationId;
			msg += StringPool.CLOSE_CURLY_BRACE;
			throw new NoSuchOrganizationException(msg);
		}
		else {
			return (Organization)list.get(0);
		}
	}

	public Organization[] findByC_P_PrevAndNext(String organizationId,
		String companyId, String parentOrganizationId, OrderByComparator obc)
		throws NoSuchOrganizationException, SystemException {
		Organization organization = findByPrimaryKey(organizationId);
		int count = countByC_P(companyId, parentOrganizationId);
		Session session = null;

		try {
			session = openSession();

			StringBuffer query = new StringBuffer();
			query.append("FROM com.liferay.portal.model.Organization WHERE ");

			if (companyId == null) {
				query.append("companyId IS NULL");
			}
			else {
				query.append("companyId = ?");
			}

			query.append(" AND ");

			if (parentOrganizationId == null) {
				query.append("parentOrganizationId IS NULL");
			}
			else {
				query.append("parentOrganizationId = ?");
			}

			query.append(" ");

			if (obc != null) {
				query.append("ORDER BY " + obc.getOrderBy());
			}
			else {
				query.append("ORDER BY ");
				query.append("name ASC");
			}

			Query q = session.createQuery(query.toString());
			int queryPos = 0;

			if (companyId != null) {
				q.setString(queryPos++, companyId);
			}

			if (parentOrganizationId != null) {
				q.setString(queryPos++, parentOrganizationId);
			}

			Object[] objArray = QueryUtil.getPrevAndNext(q, count, obc,
					organization);
			Organization[] array = new Organization[3];
			array[0] = (Organization)objArray[0];
			array[1] = (Organization)objArray[1];
			array[2] = (Organization)objArray[2];

			return array;
		}
		catch (HibernateException he) {
			throw new SystemException(he);
		}
		finally {
			closeSession(session);
		}
	}

	public Organization findByC_N(String companyId, String name)
		throws NoSuchOrganizationException, SystemException {
		Organization organization = fetchByC_N(companyId, name);

		if (organization == null) {
			String msg = "No Organization exists with the key ";
			msg += StringPool.OPEN_CURLY_BRACE;
			msg += "companyId=";
			msg += companyId;
			msg += ", ";
			msg += "name=";
			msg += name;
			msg += StringPool.CLOSE_CURLY_BRACE;

			if (_log.isWarnEnabled()) {
				_log.warn(msg);
			}

			throw new NoSuchOrganizationException(msg);
		}

		return organization;
	}

	public Organization fetchByC_N(String companyId, String name)
		throws SystemException {
		String pk = OrganizationPool.getByC_N(companyId, name);

		if (pk != null) {
			Organization organization = fetchByPrimaryKey(pk);

			if (organization != null) {
				return organization;
			}
		}

		Session session = null;

		try {
			session = openSession();

			StringBuffer query = new StringBuffer();
			query.append("FROM com.liferay.portal.model.Organization WHERE ");

			if (companyId == null) {
				query.append("companyId IS NULL");
			}
			else {
				query.append("companyId = ?");
			}

			query.append(" AND ");

			if (name == null) {
				query.append("name IS NULL");
			}
			else {
				query.append("name = ?");
			}

			query.append(" ");
			query.append("ORDER BY ");
			query.append("name ASC");

			Query q = session.createQuery(query.toString());
			int queryPos = 0;

			if (companyId != null) {
				q.setString(queryPos++, companyId);
			}

			if (name != null) {
				q.setString(queryPos++, name);
			}

			List list = q.list();

			if (list.size() == 0) {
				return null;
			}

			Organization organization = (Organization)list.get(0);
			OrganizationPool.putByC_N(companyId, name,
				organization.getPrimaryKey());

			return organization;
		}
		catch (HibernateException he) {
			throw new SystemException(he);
		}
		finally {
			closeSession(session);
		}
	}

	public List findAll() throws SystemException {
		Session session = null;

		try {
			session = openSession();

			StringBuffer query = new StringBuffer();
			query.append("FROM com.liferay.portal.model.Organization ");
			query.append("ORDER BY ");
			query.append("name ASC");

			Query q = session.createQuery(query.toString());

			return q.list();
		}
		catch (HibernateException he) {
			throw new SystemException(he);
		}
		finally {
			closeSession(session);
		}
	}

	public void removeByCompanyId(String companyId) throws SystemException {
		Iterator itr = findByCompanyId(companyId).iterator();

		while (itr.hasNext()) {
			Organization organization = (Organization)itr.next();
			remove(organization);
		}
	}

	public void removeByLocations(String companyId) throws SystemException {
		Iterator itr = findByLocations(companyId).iterator();

		while (itr.hasNext()) {
			Organization organization = (Organization)itr.next();
			remove(organization);
		}
	}

	public void removeByC_P(String companyId, String parentOrganizationId)
		throws SystemException {
		Iterator itr = findByC_P(companyId, parentOrganizationId).iterator();

		while (itr.hasNext()) {
			Organization organization = (Organization)itr.next();
			remove(organization);
		}
	}

	public void removeByC_N(String companyId, String name)
		throws NoSuchOrganizationException, SystemException {
		Organization organization = findByC_N(companyId, name);
		remove(organization);
	}

	public int countByCompanyId(String companyId) throws SystemException {
		Session session = null;

		try {
			session = openSession();

			StringBuffer query = new StringBuffer();
			query.append("SELECT COUNT(*) ");
			query.append("FROM com.liferay.portal.model.Organization WHERE ");

			if (companyId == null) {
				query.append("companyId IS NULL");
			}
			else {
				query.append("companyId = ?");
			}

			query.append(" ");

			Query q = session.createQuery(query.toString());
			int queryPos = 0;

			if (companyId != null) {
				q.setString(queryPos++, companyId);
			}

			Iterator itr = q.list().iterator();

			if (itr.hasNext()) {
				Long count = (Long)itr.next();

				if (count != null) {
					return count.intValue();
				}
			}

			return 0;
		}
		catch (HibernateException he) {
			throw new SystemException(he);
		}
		finally {
			closeSession(session);
		}
	}

	public int countByLocations(String companyId) throws SystemException {
		Session session = null;

		try {
			session = openSession();

			StringBuffer query = new StringBuffer();
			query.append("SELECT COUNT(*) ");
			query.append("FROM com.liferay.portal.model.Organization WHERE ");

			if (companyId == null) {
				query.append("companyId IS NULL");
			}
			else {
				query.append("companyId = ?");
			}

			query.append(" AND parentOrganizationId != '-1' ");

			Query q = session.createQuery(query.toString());
			int queryPos = 0;

			if (companyId != null) {
				q.setString(queryPos++, companyId);
			}

			Iterator itr = q.list().iterator();

			if (itr.hasNext()) {
				Long count = (Long)itr.next();

				if (count != null) {
					return count.intValue();
				}
			}

			return 0;
		}
		catch (HibernateException he) {
			throw new SystemException(he);
		}
		finally {
			closeSession(session);
		}
	}

	public int countByC_P(String companyId, String parentOrganizationId)
		throws SystemException {
		Session session = null;

		try {
			session = openSession();

			StringBuffer query = new StringBuffer();
			query.append("SELECT COUNT(*) ");
			query.append("FROM com.liferay.portal.model.Organization WHERE ");

			if (companyId == null) {
				query.append("companyId IS NULL");
			}
			else {
				query.append("companyId = ?");
			}

			query.append(" AND ");

			if (parentOrganizationId == null) {
				query.append("parentOrganizationId IS NULL");
			}
			else {
				query.append("parentOrganizationId = ?");
			}

			query.append(" ");

			Query q = session.createQuery(query.toString());
			int queryPos = 0;

			if (companyId != null) {
				q.setString(queryPos++, companyId);
			}

			if (parentOrganizationId != null) {
				q.setString(queryPos++, parentOrganizationId);
			}

			Iterator itr = q.list().iterator();

			if (itr.hasNext()) {
				Long count = (Long)itr.next();

				if (count != null) {
					return count.intValue();
				}
			}

			return 0;
		}
		catch (HibernateException he) {
			throw new SystemException(he);
		}
		finally {
			closeSession(session);
		}
	}

	public int countByC_N(String companyId, String name)
		throws SystemException {
		Session session = null;

		try {
			session = openSession();

			StringBuffer query = new StringBuffer();
			query.append("SELECT COUNT(*) ");
			query.append("FROM com.liferay.portal.model.Organization WHERE ");

			if (companyId == null) {
				query.append("companyId IS NULL");
			}
			else {
				query.append("companyId = ?");
			}

			query.append(" AND ");

			if (name == null) {
				query.append("name IS NULL");
			}
			else {
				query.append("name = ?");
			}

			query.append(" ");

			Query q = session.createQuery(query.toString());
			int queryPos = 0;

			if (companyId != null) {
				q.setString(queryPos++, companyId);
			}

			if (name != null) {
				q.setString(queryPos++, name);
			}

			Iterator itr = q.list().iterator();

			if (itr.hasNext()) {
				Long count = (Long)itr.next();

				if (count != null) {
					return count.intValue();
				}
			}

			return 0;
		}
		catch (HibernateException he) {
			throw new SystemException(he);
		}
		finally {
			closeSession(session);
		}
	}

	public List getGroups(String pk)
		throws NoSuchOrganizationException, SystemException {
		return getGroups(pk, QueryUtil.ALL_POS, QueryUtil.ALL_POS);
	}

	public List getGroups(String pk, int begin, int end)
		throws NoSuchOrganizationException, SystemException {
		return getGroups(pk, begin, end, null);
	}

	public List getGroups(String pk, int begin, int end, OrderByComparator obc)
		throws NoSuchOrganizationException, SystemException {
		Session session = null;

		try {
			session = HibernateUtil.openSession();

			String sql = _SQL_GETGROUPS;

			if (obc != null) {
				sql += ("ORDER BY " + obc.getOrderBy());
			}
			else {
				sql += "ORDER BY ";
				sql += "Group_.name ASC";
			}

			SQLQuery q = session.createSQLQuery(sql);
			q.addEntity("Group_", com.liferay.portal.model.Group.class);

			QueryPos qPos = QueryPos.getInstance(q);
			qPos.add(pk);

			return QueryUtil.list(q, HibernateUtil.getDialect(), begin, end);
		}
		catch (Exception e) {
			throw new SystemException(e);
		}
		finally {
			HibernateUtil.closeSession(session);
		}
	}

	public int getGroupsSize(String pk) throws SystemException {
		Session session = null;

		try {
			session = openSession();

			SQLQuery q = session.createSQLQuery(_SQL_GETGROUPSSIZE);
			q.addScalar(HibernateUtil.getCountColumnName(), Hibernate.LONG);

			QueryPos qPos = QueryPos.getInstance(q);
			qPos.add(pk);

			Iterator itr = q.list().iterator();

			if (itr.hasNext()) {
				Long count = (Long)itr.next();

				if (count != null) {
					return count.intValue();
				}
			}

			return 0;
		}
		catch (HibernateException he) {
			throw new SystemException(he);
		}
		finally {
			closeSession(session);
		}
	}

	public boolean containsGroup(String pk, String groupPK)
		throws SystemException {
		try {
			return containsGroup.contains(pk, groupPK);
		}
		catch (DataAccessException dae) {
			throw new SystemException(dae);
		}
	}

	public boolean containsGroups(String pk) throws SystemException {
		if (getGroupsSize(pk) > 0) {
			return true;
		}
		else {
			return false;
		}
	}

	public void addGroup(String pk, String groupPK)
		throws NoSuchOrganizationException, 
			com.liferay.portal.NoSuchGroupException, SystemException {
		try {
			addGroup.add(pk, groupPK);
		}
		catch (DataAccessException dae) {
			throw new SystemException(dae);
		}
	}

	public void addGroup(String pk, com.liferay.portal.model.Group group)
		throws NoSuchOrganizationException, 
			com.liferay.portal.NoSuchGroupException, SystemException {
		try {
			addGroup.add(pk, group.getPrimaryKey());
		}
		catch (DataAccessException dae) {
			throw new SystemException(dae);
		}
	}

	public void addGroups(String pk, String[] groupPKs)
		throws NoSuchOrganizationException, 
			com.liferay.portal.NoSuchGroupException, SystemException {
		try {
			for (int i = 0; i < groupPKs.length; i++) {
				addGroup.add(pk, groupPKs[i]);
			}
		}
		catch (DataAccessException dae) {
			throw new SystemException(dae);
		}
	}

	public void addGroups(String pk, List groups)
		throws NoSuchOrganizationException, 
			com.liferay.portal.NoSuchGroupException, SystemException {
		try {
			for (int i = 0; i < groups.size(); i++) {
				com.liferay.portal.model.Group group = (com.liferay.portal.model.Group)groups.get(i);
				addGroup.add(pk, group.getPrimaryKey());
			}
		}
		catch (DataAccessException dae) {
			throw new SystemException(dae);
		}
	}

	public void clearGroups(String pk)
		throws NoSuchOrganizationException, SystemException {
		try {
			clearGroups.clear(pk);
		}
		catch (DataAccessException dae) {
			throw new SystemException(dae);
		}
	}

	public void removeGroup(String pk, String groupPK)
		throws NoSuchOrganizationException, 
			com.liferay.portal.NoSuchGroupException, SystemException {
		try {
			removeGroup.remove(pk, groupPK);
		}
		catch (DataAccessException dae) {
			throw new SystemException(dae);
		}
	}

	public void removeGroup(String pk, com.liferay.portal.model.Group group)
		throws NoSuchOrganizationException, 
			com.liferay.portal.NoSuchGroupException, SystemException {
		try {
			removeGroup.remove(pk, group.getPrimaryKey());
		}
		catch (DataAccessException dae) {
			throw new SystemException(dae);
		}
	}

	public void removeGroups(String pk, String[] groupPKs)
		throws NoSuchOrganizationException, 
			com.liferay.portal.NoSuchGroupException, SystemException {
		try {
			for (int i = 0; i < groupPKs.length; i++) {
				removeGroup.remove(pk, groupPKs[i]);
			}
		}
		catch (DataAccessException dae) {
			throw new SystemException(dae);
		}
	}

	public void removeGroups(String pk, List groups)
		throws NoSuchOrganizationException, 
			com.liferay.portal.NoSuchGroupException, SystemException {
		try {
			for (int i = 0; i < groups.size(); i++) {
				com.liferay.portal.model.Group group = (com.liferay.portal.model.Group)groups.get(i);
				removeGroup.remove(pk, group.getPrimaryKey());
			}
		}
		catch (DataAccessException dae) {
			throw new SystemException(dae);
		}
	}

	public void setGroups(String pk, String[] groupPKs)
		throws NoSuchOrganizationException, 
			com.liferay.portal.NoSuchGroupException, SystemException {
		try {
			clearGroups.clear(pk);

			for (int i = 0; i < groupPKs.length; i++) {
				addGroup.add(pk, groupPKs[i]);
			}
		}
		catch (DataAccessException dae) {
			throw new SystemException(dae);
		}
	}

	public void setGroups(String pk, List groups)
		throws NoSuchOrganizationException, 
			com.liferay.portal.NoSuchGroupException, SystemException {
		try {
			clearGroups.clear(pk);

			for (int i = 0; i < groups.size(); i++) {
				com.liferay.portal.model.Group group = (com.liferay.portal.model.Group)groups.get(i);
				addGroup.add(pk, group.getPrimaryKey());
			}
		}
		catch (DataAccessException dae) {
			throw new SystemException(dae);
		}
	}

	public List getUsers(String pk)
		throws NoSuchOrganizationException, SystemException {
		return getUsers(pk, QueryUtil.ALL_POS, QueryUtil.ALL_POS);
	}

	public List getUsers(String pk, int begin, int end)
		throws NoSuchOrganizationException, SystemException {
		return getUsers(pk, begin, end, null);
	}

	public List getUsers(String pk, int begin, int end, OrderByComparator obc)
		throws NoSuchOrganizationException, SystemException {
		Session session = null;

		try {
			session = HibernateUtil.openSession();

			String sql = _SQL_GETUSERS;

			if (obc != null) {
				sql += ("ORDER BY " + obc.getOrderBy());
			}

			SQLQuery q = session.createSQLQuery(sql);
			q.addEntity("User_", com.liferay.portal.model.User.class);

			QueryPos qPos = QueryPos.getInstance(q);
			qPos.add(pk);

			return QueryUtil.list(q, HibernateUtil.getDialect(), begin, end);
		}
		catch (Exception e) {
			throw new SystemException(e);
		}
		finally {
			HibernateUtil.closeSession(session);
		}
	}

	public int getUsersSize(String pk) throws SystemException {
		Session session = null;

		try {
			session = openSession();

			SQLQuery q = session.createSQLQuery(_SQL_GETUSERSSIZE);
			q.addScalar(HibernateUtil.getCountColumnName(), Hibernate.LONG);

			QueryPos qPos = QueryPos.getInstance(q);
			qPos.add(pk);

			Iterator itr = q.list().iterator();

			if (itr.hasNext()) {
				Long count = (Long)itr.next();

				if (count != null) {
					return count.intValue();
				}
			}

			return 0;
		}
		catch (HibernateException he) {
			throw new SystemException(he);
		}
		finally {
			closeSession(session);
		}
	}

	public boolean containsUser(String pk, String userPK)
		throws SystemException {
		try {
			return containsUser.contains(pk, userPK);
		}
		catch (DataAccessException dae) {
			throw new SystemException(dae);
		}
	}

	public boolean containsUsers(String pk) throws SystemException {
		if (getUsersSize(pk) > 0) {
			return true;
		}
		else {
			return false;
		}
	}

	public void addUser(String pk, String userPK)
		throws NoSuchOrganizationException, 
			com.liferay.portal.NoSuchUserException, SystemException {
		try {
			addUser.add(pk, userPK);
		}
		catch (DataAccessException dae) {
			throw new SystemException(dae);
		}
	}

	public void addUser(String pk, com.liferay.portal.model.User user)
		throws NoSuchOrganizationException, 
			com.liferay.portal.NoSuchUserException, SystemException {
		try {
			addUser.add(pk, user.getPrimaryKey());
		}
		catch (DataAccessException dae) {
			throw new SystemException(dae);
		}
	}

	public void addUsers(String pk, String[] userPKs)
		throws NoSuchOrganizationException, 
			com.liferay.portal.NoSuchUserException, SystemException {
		try {
			for (int i = 0; i < userPKs.length; i++) {
				addUser.add(pk, userPKs[i]);
			}
		}
		catch (DataAccessException dae) {
			throw new SystemException(dae);
		}
	}

	public void addUsers(String pk, List users)
		throws NoSuchOrganizationException, 
			com.liferay.portal.NoSuchUserException, SystemException {
		try {
			for (int i = 0; i < users.size(); i++) {
				com.liferay.portal.model.User user = (com.liferay.portal.model.User)users.get(i);
				addUser.add(pk, user.getPrimaryKey());
			}
		}
		catch (DataAccessException dae) {
			throw new SystemException(dae);
		}
	}

	public void clearUsers(String pk)
		throws NoSuchOrganizationException, SystemException {
		try {
			clearUsers.clear(pk);
		}
		catch (DataAccessException dae) {
			throw new SystemException(dae);
		}
	}

	public void removeUser(String pk, String userPK)
		throws NoSuchOrganizationException, 
			com.liferay.portal.NoSuchUserException, SystemException {
		try {
			removeUser.remove(pk, userPK);
		}
		catch (DataAccessException dae) {
			throw new SystemException(dae);
		}
	}

	public void removeUser(String pk, com.liferay.portal.model.User user)
		throws NoSuchOrganizationException, 
			com.liferay.portal.NoSuchUserException, SystemException {
		try {
			removeUser.remove(pk, user.getPrimaryKey());
		}
		catch (DataAccessException dae) {
			throw new SystemException(dae);
		}
	}

	public void removeUsers(String pk, String[] userPKs)
		throws NoSuchOrganizationException, 
			com.liferay.portal.NoSuchUserException, SystemException {
		try {
			for (int i = 0; i < userPKs.length; i++) {
				removeUser.remove(pk, userPKs[i]);
			}
		}
		catch (DataAccessException dae) {
			throw new SystemException(dae);
		}
	}

	public void removeUsers(String pk, List users)
		throws NoSuchOrganizationException, 
			com.liferay.portal.NoSuchUserException, SystemException {
		try {
			for (int i = 0; i < users.size(); i++) {
				com.liferay.portal.model.User user = (com.liferay.portal.model.User)users.get(i);
				removeUser.remove(pk, user.getPrimaryKey());
			}
		}
		catch (DataAccessException dae) {
			throw new SystemException(dae);
		}
	}

	public void setUsers(String pk, String[] userPKs)
		throws NoSuchOrganizationException, 
			com.liferay.portal.NoSuchUserException, SystemException {
		try {
			clearUsers.clear(pk);

			for (int i = 0; i < userPKs.length; i++) {
				addUser.add(pk, userPKs[i]);
			}
		}
		catch (DataAccessException dae) {
			throw new SystemException(dae);
		}
	}

	public void setUsers(String pk, List users)
		throws NoSuchOrganizationException, 
			com.liferay.portal.NoSuchUserException, SystemException {
		try {
			clearUsers.clear(pk);

			for (int i = 0; i < users.size(); i++) {
				com.liferay.portal.model.User user = (com.liferay.portal.model.User)users.get(i);
				addUser.add(pk, user.getPrimaryKey());
			}
		}
		catch (DataAccessException dae) {
			throw new SystemException(dae);
		}
	}

	protected void initDao() {
		containsGroup = new ContainsGroup(this);
		addGroup = new AddGroup(this);
		clearGroups = new ClearGroups(this);
		removeGroup = new RemoveGroup(this);
		containsUser = new ContainsUser(this);
		addUser = new AddUser(this);
		clearUsers = new ClearUsers(this);
		removeUser = new RemoveUser(this);
	}

	protected ContainsGroup containsGroup;
	protected AddGroup addGroup;
	protected ClearGroups clearGroups;
	protected RemoveGroup removeGroup;
	protected ContainsUser containsUser;
	protected AddUser addUser;
	protected ClearUsers clearUsers;
	protected RemoveUser removeUser;

	protected class ContainsGroup extends MappingSqlQuery {
		protected ContainsGroup(OrganizationPersistence persistence) {
			super(persistence.getDataSource(), _SQL_CONTAINSGROUP);
			declareParameter(new SqlParameter(Types.VARCHAR));
			declareParameter(new SqlParameter(Types.VARCHAR));
			compile();
		}

		protected Object mapRow(ResultSet rs, int rowNumber)
			throws SQLException {
			return new Integer(rs.getInt("COUNT_VALUE"));
		}

		protected boolean contains(String organizationId, String groupId) {
			List results = execute(new Object[] { organizationId, groupId });

			if (results.size() > 0) {
				Integer count = (Integer)results.get(0);

				if (count.intValue() > 0) {
					return true;
				}
			}

			return false;
		}
	}

	protected class AddGroup extends SqlUpdate {
		protected AddGroup(OrganizationPersistence persistence) {
			super(persistence.getDataSource(),
				"INSERT INTO Groups_Orgs (organizationId, groupId) VALUES (?, ?)");
			_persistence = persistence;
			declareParameter(new SqlParameter(Types.VARCHAR));
			declareParameter(new SqlParameter(Types.VARCHAR));
			compile();
		}

		protected void add(String organizationId, String groupId) {
			if (!_persistence.containsGroup.contains(organizationId, groupId)) {
				update(new Object[] { organizationId, groupId });
			}
		}

		private OrganizationPersistence _persistence;
	}

	protected class ClearGroups extends SqlUpdate {
		protected ClearGroups(OrganizationPersistence persistence) {
			super(persistence.getDataSource(),
				"DELETE FROM Groups_Orgs WHERE organizationId = ?");
			declareParameter(new SqlParameter(Types.VARCHAR));
			compile();
		}

		protected void clear(String organizationId) {
			update(new Object[] { organizationId });
		}
	}

	protected class RemoveGroup extends SqlUpdate {
		protected RemoveGroup(OrganizationPersistence persistence) {
			super(persistence.getDataSource(),
				"DELETE FROM Groups_Orgs WHERE organizationId = ? AND groupId = ?");
			declareParameter(new SqlParameter(Types.VARCHAR));
			declareParameter(new SqlParameter(Types.VARCHAR));
			compile();
		}

		protected void remove(String organizationId, String groupId) {
			update(new Object[] { organizationId, groupId });
		}
	}

	protected class ContainsUser extends MappingSqlQuery {
		protected ContainsUser(OrganizationPersistence persistence) {
			super(persistence.getDataSource(), _SQL_CONTAINSUSER);
			declareParameter(new SqlParameter(Types.VARCHAR));
			declareParameter(new SqlParameter(Types.VARCHAR));
			compile();
		}

		protected Object mapRow(ResultSet rs, int rowNumber)
			throws SQLException {
			return new Integer(rs.getInt("COUNT_VALUE"));
		}

		protected boolean contains(String organizationId, String userId) {
			List results = execute(new Object[] { organizationId, userId });

			if (results.size() > 0) {
				Integer count = (Integer)results.get(0);

				if (count.intValue() > 0) {
					return true;
				}
			}

			return false;
		}
	}

	protected class AddUser extends SqlUpdate {
		protected AddUser(OrganizationPersistence persistence) {
			super(persistence.getDataSource(),
				"INSERT INTO Users_Orgs (organizationId, userId) VALUES (?, ?)");
			_persistence = persistence;
			declareParameter(new SqlParameter(Types.VARCHAR));
			declareParameter(new SqlParameter(Types.VARCHAR));
			compile();
		}

		protected void add(String organizationId, String userId) {
			if (!_persistence.containsUser.contains(organizationId, userId)) {
				update(new Object[] { organizationId, userId });
			}
		}

		private OrganizationPersistence _persistence;
	}

	protected class ClearUsers extends SqlUpdate {
		protected ClearUsers(OrganizationPersistence persistence) {
			super(persistence.getDataSource(),
				"DELETE FROM Users_Orgs WHERE organizationId = ?");
			declareParameter(new SqlParameter(Types.VARCHAR));
			compile();
		}

		protected void clear(String organizationId) {
			update(new Object[] { organizationId });
		}
	}

	protected class RemoveUser extends SqlUpdate {
		protected RemoveUser(OrganizationPersistence persistence) {
			super(persistence.getDataSource(),
				"DELETE FROM Users_Orgs WHERE organizationId = ? AND userId = ?");
			declareParameter(new SqlParameter(Types.VARCHAR));
			declareParameter(new SqlParameter(Types.VARCHAR));
			compile();
		}

		protected void remove(String organizationId, String userId) {
			update(new Object[] { organizationId, userId });
		}
	}

	private static final String _SQL_GETGROUPS = "SELECT {Group_.*} FROM Group_ INNER JOIN Groups_Orgs ON (Groups_Orgs.organizationId = ?) AND (Groups_Orgs.groupId = Group_.groupId)";
	private static final String _SQL_GETGROUPSSIZE = "SELECT COUNT(*) AS COUNT_VALUE FROM Groups_Orgs WHERE organizationId = ?";
	private static final String _SQL_CONTAINSGROUP = "SELECT COUNT(*) AS COUNT_VALUE FROM Groups_Orgs WHERE organizationId = ? AND groupId = ?";
	private static final String _SQL_GETUSERS = "SELECT {User_.*} FROM User_ INNER JOIN Users_Orgs ON (Users_Orgs.organizationId = ?) AND (Users_Orgs.userId = User_.userId)";
	private static final String _SQL_GETUSERSSIZE = "SELECT COUNT(*) AS COUNT_VALUE FROM Users_Orgs WHERE organizationId = ?";
	private static final String _SQL_CONTAINSUSER = "SELECT COUNT(*) AS COUNT_VALUE FROM Users_Orgs WHERE organizationId = ? AND userId = ?";
	private static Log _log = LogFactory.getLog(OrganizationPersistence.class);
}