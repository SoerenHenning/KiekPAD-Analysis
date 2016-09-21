package kiekpad.analysis;

import java.time.Duration;
import java.time.Instant;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.exceptions.InvalidQueryException;
import com.datastax.driver.core.exceptions.NoHostAvailableException;

public class CassandraManager {

	private Session session;

	private final static int WAITING_SLEEP_MILLIS = 1000;

	// BETTER A logger should be used to replace the System.out.println()

	public CassandraManager(final String host, final int port, final String keyspace, final int timeoutInMillis) {
		createSession(host, port, keyspace, timeoutInMillis);
	}

	public Session getSession() {
		return session;
	}

	private void createSession(final String host, final int port, final String keyspace, final int timeoutInMillis) {
		final Instant start = Instant.now();

		Cluster cluster = Cluster.builder().addContactPoint(host).withPort(port).build();
		while (true) {
			try {
				this.session = cluster.connect(keyspace);
				break;
			} catch (NoHostAvailableException exception) {
				// Host not unavailable
				// System.out.println("Waiting for host...");
				if (Duration.between(start, Instant.now()).toMillis() < timeoutInMillis) {
					cluster.close();
					cluster = Cluster.builder().addContactPoint(host).withPort(port).build();
					try {
						Thread.sleep(WAITING_SLEEP_MILLIS);
					} catch (InterruptedException e) {
						throw new IllegalStateException(e);
					}
				} else {
					throw exception;
				}
			} catch (InvalidQueryException exception) {
				// Keyspace does not exist
				// System.out.println("Create Keyspace...");
				createKeyspaceIfNotExists(cluster, keyspace);
			}
		}
	}

	private void createKeyspaceIfNotExists(final Cluster cluster, final String keyspace) {
		Session session = cluster.connect();
		session.execute("CREATE KEYSPACE IF NOT EXISTS " + keyspace + " WITH replication " +
				"= {'class':'SimpleStrategy', 'replication_factor':1};");
		session.close();
	}

}
