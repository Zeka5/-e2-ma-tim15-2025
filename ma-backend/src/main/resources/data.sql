-- Users (password is bcrypt encoded version of their username in lowercase)
INSERT INTO public.users (created_at, email, username, password, role, avatar_id, is_activated) VALUES
('2025-05-25 15:47:28.0106', 'zeka@test.com', 'Zeka', '$2y$10$KXQ1h0lesVbTnO8JInnakukv5lSmthZqw0SMGjtnBH9bwX6P3wIRy', 'USER', 5, true),
('2025-05-17 21:20:01.573378', 'ivorad@test.com', 'Ivorad', '$2y$10$YSEexlyymWeX1HkDVO9EL.8gNUejeduzua1RZONJK5L2OmbdE96ra', 'USER', 2, true),
('2025-05-10 10:15:00.000000', 'kimi@test.com', 'Kimi', '$2y$10$bIoTFfOnL4WbUZSWMMSQF.Mi5.7rhfBCnc6L2wyiXsT3QMwKIo8U6', 'USER', 1, true);

-- User game stats (give them some coins and level to test shop)
INSERT INTO public.user_game_stats (user_id, level, title, power_points, experience_points, coins, qr_code) VALUES
(1, 3, 'ADVENTURER', 150, 500, 1000, 'ZEKA_QR_123'),
(2, 2, 'APPRENTICE', 120, 200, 500, 'IVORAD_QR_456'),
(3, 1, 'NOVICE', 100, 200, 2000, 'KIMI_QR_789');

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

-- Potion Templates (based on specification lines 264-272)
-- Prices are calculated as: boss_reward * multiplier
INSERT INTO public.potion_templates (name, power_bonus, is_permanent, price_multiplier, description, icon_url) VALUES
('One-Time Power +20%', 20, false, 0.5, 'Increases power by 20% for the next boss battle only', null),
('One-Time Power +40%', 40, false, 0.7, 'Increases power by 40% for the next boss battle only', null),
('Permanent Power +5%', 5, true, 2.0, 'Permanently increases your power by 5%', null),
('Permanent Power +10%', 10, true, 10.0, 'Permanently increases your power by 10%', null);

-- Clothing Templates (based on specification lines 277-283)
INSERT INTO public.clothing_templates (name, clothing_type, bonus_percentage, price_multiplier, description, icon_url) VALUES
('Power Gloves', 'GLOVES', 10, 0.6, 'Increases power by 10% for 2 boss battles', null),
('Guardian Shield', 'SHIELD', 10, 0.6, 'Increases attack success rate by 10% for 2 boss battles', null),
('Swift Boots', 'BOOTS', 40, 0.8, 'Gives 40% chance of extra attack for 2 boss battles', null);

-- Weapon Templates (based on specification lines 288-294)
-- Weapons are only obtainable from boss drops, not purchasable
INSERT INTO public.weapon_templates (name, weapon_type, base_bonus_percentage, upgrade_price_multiplier, description, icon_url) VALUES
('Ancient Sword', 'SWORD', 5.0, 0.6, 'Permanently increases power by bonus', null),
('Mystic Bow', 'BOW_ARROW', 5.0, 0.6, 'Permanently increases coin rewards by bonus', null);

-- Friendships (bidirectional)
-- Zeka (id=1) and Ivorad (id=2) are friends
-- Zeka (id=1) and Kimi (id=3) are friends
INSERT INTO public.friendships (user_id, friend_id) VALUES
(1, 2),
(2, 1),
(1, 3),
(3, 1);

-- Guild (Zeka is leader, Ivorad and Kimi are members)
INSERT INTO public.guilds (name, leader_id, has_active_mission, created_at) VALUES
('Dragon Slayers', 1, false, '2025-05-26 10:00:00.000000');

-- Update users to be in the guild
UPDATE public.users SET guild_id = 1 WHERE id IN (1, 2, 3);

-- Categories for Kimi (user_id = 3)
INSERT INTO public.categories (user_id, name, color, created_at, updated_at) VALUES
(3, 'Work', '#FF5733', '2025-05-10 10:20:00.000000', '2025-05-10 10:20:00.000000'),
(3, 'Personal', '#33C1FF', '2025-05-10 10:21:00.000000', '2025-05-10 10:21:00.000000');

-- Tasks for Kimi (user_id = 3)
-- difficulty_xp: VERY_EASY=1, EASY=3, HARD=7, EXTREMELY_HARD=20
-- importance_xp: NORMAL=1, IMPORTANT=3, EXTREMELY_IMPORTANT=10, SPECIAL=100
INSERT INTO public.tasks (user_id, category_id, title, description, difficulty, difficulty_xp, importance, importance_xp, total_xp, is_repeating, recurrence_interval, recurrence_unit, start_date, end_date, created_at, updated_at) VALUES
(3, 1, 'Complete project report', 'Finish the monthly project report', 'HARD', 7, 'EXTREMELY_IMPORTANT', 10, 17, false, null, null, '2025-05-10 09:00:00.000000', '2025-05-15 18:00:00.000000', '2025-05-10 10:22:00.000000', '2025-05-10 10:22:00.000000'),
(3, 1, 'Review code', 'Review pull requests from team', 'EASY', 3, 'IMPORTANT', 3, 6, false, null, null, '2025-05-11 09:00:00.000000', '2025-05-16 18:00:00.000000', '2025-05-10 10:23:00.000000', '2025-05-10 10:23:00.000000'),
(3, 2, 'Exercise', 'Go for a run', 'EASY', 3, 'NORMAL', 1, 4, false, null, null, '2025-05-12 06:00:00.000000', '2025-05-17 20:00:00.000000', '2025-05-10 10:24:00.000000', '2025-05-10 10:24:00.000000');

-- Task instances for Kimi (all completed to give XP)
INSERT INTO public.task_instances (task_id, start_date, status, xp_awarded, xp_amount, completed_at, created_at, updated_at) VALUES
(1, '2025-05-10 09:00:00.000000', 'COMPLETED', true, 17, '2025-05-12 17:30:00.000000', '2025-05-10 10:22:00.000000', '2025-05-12 17:30:00.000000'),
(2, '2025-05-11 09:00:00.000000', 'COMPLETED', true, 6, '2025-05-13 16:00:00.000000', '2025-05-10 10:23:00.000000', '2025-05-13 16:00:00.000000'),
(3, '2025-05-12 06:00:00.000000', 'COMPLETED', true, 4, '2025-05-14 07:30:00.000000', '2025-05-10 10:24:00.000000', '2025-05-14 07:30:00.000000');

-- Equipment for Kimi (user_game_stats_id = 3)
-- One-time potion (+20%)
INSERT INTO public.user_potions (user_game_stats_id, potion_template_id, quantity, is_activated, acquired_at) VALUES
(3, 1, 1, false, '2025-05-15 10:00:00.000000');

-- Permanent potion (+5%)
INSERT INTO public.user_potions (user_game_stats_id, potion_template_id, quantity, is_activated, acquired_at) VALUES
(3, 3, 1, false, '2025-05-15 10:05:00.000000');

-- Clothing (Power Gloves)
INSERT INTO public.user_clothing (user_game_stats_id, clothing_template_id, accumulated_bonus, battles_remaining, is_active, acquired_at) VALUES
(3, 1, 10, 2, false, '2025-05-15 10:10:00.000000');

-- Weapon (Ancient Sword with 5% base bonus)
INSERT INTO public.user_weapons (user_game_stats_id, weapon_template_id, current_bonus_percentage, upgrade_level, duplicate_count, acquired_at) VALUES
(3, 1, 5.0, 0, 0, '2025-05-15 10:15:00.000000');
