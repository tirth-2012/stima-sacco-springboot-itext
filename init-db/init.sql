DO
$$
BEGIN
   IF NOT EXISTS (
      SELECT FROM pg_database WHERE datname = 'flowable'
   ) THEN
      CREATE DATABASE flowable;
   END IF;
END
$$;