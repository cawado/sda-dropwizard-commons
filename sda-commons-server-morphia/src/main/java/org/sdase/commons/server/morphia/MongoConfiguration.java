package org.sdase.commons.server.morphia;

import org.hibernate.validator.constraints.NotEmpty;

public class MongoConfiguration  {

   /**
    * <p>
    *    Comma separated list of hosts with their port that build the MongoDB cluster:
    * </p>
    * <pre>{@code mongo-db-a:27018,mongo-db-b:27018,mongo-db-c:27018}</pre>
    * <p>
    *    The default port if no port is specified is {@code :27017}:
    * </p>
    * <p>
    *    {@code mongo-db-a,mongo-db-b,mongo-db-c} is equal to {@code mongo-db-a:27017,mongo-db-b:27017,mongo-db-c:27017}
    * </p>
    * <p>
    *    Details in the
    *    <a href="https://docs.mongodb.com/manual/reference/connection-string/">connection string documentation</a> for
    *    host1 to hostN.
    * </p>
    */
   @NotEmpty
   private String hosts;

   /**
    * <p>
    *    The name of the mongo database to access.
    * </p>
    * <p>
    *    Details in the
    *    <a href="https://docs.mongodb.com/manual/reference/connection-string/">connection string documentation</a> for
    *    database.
    * </p>
    */
   @NotEmpty
   private String database;

   /**
    * <p>
    *    Additional options for the connection.
    * </p>
    * <p>
    *    Details in the
    *    <a href="https://docs.mongodb.com/manual/reference/connection-string/">connection string documentation</a> for
    *    options.
    * </p>
    */
   private String options = "";

   /**
    * <p>
    *    The username used for login at the MongoDB.
    * </p>
    * <p>
    *    Details in the
    *    <a href="https://docs.mongodb.com/manual/reference/connection-string/">connection string documentation</a> for
    *    username:password.
    * </p>
    */
   private String username;

   /**
    * <p>
    *    The password used for login at the MongoDB.
    * </p>
    * <p>
    *    Details in the
    *    <a href="https://docs.mongodb.com/manual/reference/connection-string/">connection string documentation</a> for
    *    username:password.
    * </p>
    */
   private String password;

   /**
    * If SSL should be used for the database connection.
    */
   private boolean useSsl;

   /**
    * The content of a CA certificate (list) in PEM format. This certificates are added to the
    * {@link javax.net.ssl.TrustManager}s to verify the connection. The string represents the content of a regular PEM
    * file, e.g.:
    * <pre><code>
    * -----BEGIN CERTIFICATE-----
    * MIIEkjCCA3qgAwIBAgIQCgFBQgAAAVOFc2oLheynCDANBgkqhkiG9w0BAQsFADA/
    * MSQwIgYDVQQKExtEaWdpdGFsIFNpZ25hdHVyZSBUcnVzdCBDby4xFzAVBgNVBAMT
    * ...
    * X4Po1QYz+3dszkDqMp4fklxBwXRsW10KXzPMTZ+sOPAveyxindmjkW8lGy+QsRlG
    * PfZ+G6Z6h7mjem0Y+iWlkYcV4PIWL1iwBi8saCbGS5jN2p8M+X+Q7UNKEkROb3N6
    * KOqkqm57TH2H3eDJAkSnh6/DNFu0Qg==
    * -----END CERTIFICATE-----
    * </code></pre>
    */
   private String caCertificate;

   public String getHosts() {
      return hosts;
   }

   public MongoConfiguration setHosts(String hosts) {
      this.hosts = hosts;
      return this;
   }

   public String getDatabase() {
      return database;
   }

   public MongoConfiguration setDatabase(String database) {
      this.database = database;
      return this;
   }

   public String getOptions() {
      return options;
   }

   public MongoConfiguration setOptions(String options) {
      this.options = options;
      return this;
   }

   public String getUsername() {
      return username;
   }

   public MongoConfiguration setUsername(String username) {
      this.username = username;
      return this;
   }

   public String getPassword() {
      return password;
   }

   public MongoConfiguration setPassword(String password) {
      this.password = password;
      return this;
   }

   public boolean isUseSsl() {
      return useSsl;
   }

   public MongoConfiguration setUseSsl(boolean useSsl) {
      this.useSsl = useSsl;
      return this;
   }

   public String getCaCertificate() {
      return caCertificate;
   }

   public MongoConfiguration setCaCertificate(String caCertificate) {
      this.caCertificate = caCertificate;
      return this;
   }

}
