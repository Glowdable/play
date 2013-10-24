import static org.easymock.EasyMock.createNiceMock;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Connection;
import java.util.Map;
import java.util.Set;

import javax.naming.NamingException;
import javax.naming.Reference;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;

import models.DeliveryVouch;
import models.DeliveryVouchSub;
import models.IWorkFlow;
import models.MockWorkFlow;
import models.OutboundVouch;
import models.OutboundVouchSub;
import models.Person;
import models.StandingCrop;

import org.hibernate.Cache;
import org.hibernate.HibernateException;
import org.hibernate.Interceptor;
import org.hibernate.StatelessSession;
import org.hibernate.Transaction;
import org.hibernate.TypeHelper;
import org.hibernate.classic.Session;
import org.hibernate.engine.FilterDefinition;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.metadata.CollectionMetadata;
import org.hibernate.stat.Statistics;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import play.db.jpa.JPA;
import play.db.jpa.NoTransaction;
import play.mvc.Finally;
import play.test.Fixtures;
import play.test.UnitTest;

//import org.easymock.*;

public class OthersTest extends UnitTest {
	@Before
	public void setup() {
		Fixtures.deleteAll();
		createTestData();
	}

	@Test
	@Ignore
	public void testSessionTras() throws HibernateException {
		// Logger.log4j.de日志
		// org.hibernate.SessionFactory sessionFactoty =
		// HibernateSessionFactory;
		javax.persistence.EntityManager em = JPA.em();
		em.clear();
		EntityTransaction tx = em.getTransaction();
		try {
			tx.begin();
			Query query = em
					.createQuery("from OutboundVouch where cstockcode=?");
			query.setParameter(0, "00000001");
			// OutboundVouch out1 = (OutboundVouch) query.();
			query = null;
			tx.commit();
		} catch (HibernateException e) {
			throw e;
		} finally {
			if (tx != null) {
				tx.commit();
			}
		}
	}

	@Test
	public void testTrasaction() {

		// 每个action都会开始一个事务

		// Interface used to interact with the entity manager factory for the
		// persistence unit

		JPA.em().getTransaction().commit();// 下面就没有事务了
		// JPA.em().clear();

		EntityManagerFactory test = JPA.entityManagerFactory;

		if (test.isOpen()) {

			EntityManager manager = JPA.em();

			manager.getTransaction().begin();

			OutboundVouch outVouch = new OutboundVouch("00000002", 123, "test");

			OutboundVouchSub vouchsub = new OutboundVouchSub(outVouch,

			outVouch.cReceiptMainID, BigInteger.valueOf(1), 1, 123, "TEST",

			BigDecimal.valueOf(100));

			outVouch.addRowWithoutSave(vouchsub);

			// DeliveryVouch vouch = new DeliveryVouch("00000001", "0",
			// "01","001");

			manager.persist(outVouch);

			// manager.flush();

			// manager.flush();//flush() without an active transaction is not
			// going to work.

			// flush有两种模式；auto和commit(default),commit模式在事务提交时，自动flush

			// auto

			// JPA.em().getTransaction().commit();

			// manager.persist(vouch);

			manager.flush();// 将sql写到数据库
			// manager.flush();//连续两次就是会重复Found two representations of same
			// collection: models.OutboundVouch.outboundVouchSubs
			manager.clear();// 此处必须clear，清空persist context，清空内存
			manager.getTransaction().commit();

		}
	}

	private static void createTestData() {
		OutboundVouch outVouch = new OutboundVouch("00000001", 123, "test")
				.save();

		OutboundVouchSub vouchsub = new OutboundVouchSub(outVouch,
				outVouch.cReceiptMainID, BigInteger.valueOf(1), 1, 123, "TEST",
				BigDecimal.valueOf(100));
		OutboundVouchSub vouchsub1 = new OutboundVouchSub(outVouch,
				outVouch.cReceiptMainID, BigInteger.valueOf(1), 1, 123, "TEST",
				BigDecimal.valueOf(100));
		OutboundVouchSub vouchsub2 = new OutboundVouchSub(outVouch,
				outVouch.cReceiptMainID, BigInteger.valueOf(1), 1, 123, "TEST",
				BigDecimal.valueOf(100));

		outVouch.addRow(vouchsub);
		outVouch.addRow(vouchsub1);
		outVouch.addRow(vouchsub2);
	}

}