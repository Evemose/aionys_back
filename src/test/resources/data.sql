insert into users (id, username, password,
                   role, created_at, last_modified_at,
                   account_non_expired, account_non_locked,
                   credentials_non_expired, enabled)
values (1, 'user1', '$2a$12$KbHgv/MYovSFtDGl7S1u..yIozxnEM6LTzVWBFj6vSfEv.vqGalBu', -- password: 123Ffg%1!
        'USER',
        now(), now(), true, true, true, true),
       (2, 'user2', '$2a$12$KbHgv/MYovSFtDGl7S1u..yIozxnEM6LTzVWBFj6vSfEv.vqGalBu', -- password: 123Ffg%1!
        'USER',
        now(), now(), true, true, true, true);

insert into note (id, title, content, created_at, last_modified_at, owner_id)
values (1, 'Note 1', 'Content 1', now(), now(), 1),
       (2, 'Note 2', 'Content 2', now(), now(), 1),
       (3, 'Note 3', 'Content 3', now(), now(), 2);