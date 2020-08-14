CREATE TABLE IF NOT EXISTS user (
  id SERIAL PRIMARY KEY,
  client_user_id UUID NOT NULL,
  created_at TIMESTAMP NOT NULL,
  name VARCHAR(255)
);
CREATE TABLE IF NOT EXISTS user_site (
  user_site_id UUID NOT NULL,
  client_user_id UUID,
  site_id UUID NOT NULL,
  foreign key (client_user_id) references user(client_user_id)
);