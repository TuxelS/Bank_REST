INSERT INTO users (id, username, password, role_id)
VALUES 
    (1, 'user', '$2a$10$PVAUv5gAIYJWOQQnjrunYeeY9PNjCeV7CuAeE1WkbHZPGqhcmDLTe', 1),
    (2, 'admin', '$2a$10$go62gG0jyC5AIdbFOPc14.02OhI3H9yRAAhVcBtbmsAgRTySpL8Vi', 2);

SELECT setval('users_id_seq', (SELECT MAX(id) FROM users));