db = db.getSiblingDB('feedback_db');

db.users.insertOne({
    username: "test",
    email: "test@test.com",
    password: "$2b$12$VlHTkvX794RT97cMXXQ6iOx7MLeipgMqe8P.hfAM3GmVsFTTfZJuu",  // bcrypt hash for 'testtest'
    authority: "ROLE_USER",
    feedbacks: []
});

db.users.insertOne({
    username: "test2",
    email: "test2@test2.com",
    password: "$2b$12$VlHTkvX794RT97cMXXQ6iOx7MLeipgMqe8P.hfAM3GmVsFTTfZJuu",  // bcrypt hash for 'testtest'
    authority: "ROLE_USER",
    feedbacks: []
});

