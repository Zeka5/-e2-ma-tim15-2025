INSERT INTO public.users (created_at, email, username, password, role, avatar_id) VALUES
('2025-05-25 15:47:28.0106', 'ivorad@gmail.com', 'Ivorad', '$2a$10$dsBF/VbrKNd6QmLCE8Yb3OnKCKetEBXFGD2TQsI1DxaBF0h9pUOMG', 'USER', 4),
('2025-05-17 21:20:01.573378', 'zeka@gmail.com', 'Zeka', '$2a$10$6XZ/ZGSK/xVHul54/lscQuV1DC.iEXHZ5zLORgqZvQ9gTUSM6RZt2', 'USER', 5);

-- Boss data
-- HP formula: HP_previous * 2 + HP_previous / 2
-- Coins formula: Coins_previous * 1.2
INSERT INTO public.bosses (level, max_hp, coin_reward, name, description, image_url) VALUES
(1, 200, 200, 'Shadow Lurker', 'A mysterious creature that emerges from the darkness. The first challenge on your journey.', null),
(2, 500, 240, 'Stone Guardian', 'An ancient protector made of solid rock. Much tougher than the Shadow Lurker.', null),
(3, 1250, 288, 'Flame Titan', 'A massive being wreathed in eternal flames. Its power is unmatched.', null),
(4, 3125, 346, 'Ice Empress', 'Ruler of the frozen wastes. Her cold touch can freeze even the bravest warrior.', null),
(5, 7813, 415, 'Thunder Lord', 'Master of storms and lightning. The sky trembles at his command.', null),
(6, 19532, 498, 'Void Reaper', 'A being from beyond reality. Its very presence tears at the fabric of space.', null),
(7, 48830, 598, 'Ancient Dragon', 'The oldest and most powerful dragon in existence. Few have survived an encounter.', null),
(8, 122075, 717, 'Chaos Incarnate', 'Pure chaos given form. It defies all natural laws.', null),
(9, 305188, 861, 'Time Weaver', 'A being that exists outside of time. It can see all possible futures.', null),
(10, 762970, 1033, 'Eternal One', 'The ultimate challenge. A being of pure energy that has existed since the dawn of time.', null);