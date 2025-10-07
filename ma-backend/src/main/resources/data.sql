-- Users (password is bcrypt encoded version of their username in lowercase)
INSERT INTO public.users (created_at, email, username, password, role, avatar_id, is_activated) VALUES
('2025-05-25 15:47:28.0106', 'zeka@test.com', 'Zeka', '$2y$10$KXQ1h0lesVbTnO8JInnakukv5lSmthZqw0SMGjtnBH9bwX6P3wIRy', 'USER', 5, true),
('2025-05-17 21:20:01.573378', 'ivorad@test.com', 'Ivorad', '$2y$10$YSEexlyymWeX1HkDVO9EL.8gNUejeduzua1RZONJK5L2OmbdE96ra', 'USER', 2, true),
('2025-05-10 10:15:00.000000', 'kimi@test.com', 'Kimi', '$2y$10$bIoTFfOnL4WbUZSWMMSQF.Mi5.7rhfBCnc6L2wyiXsT3QMwKIo8U6', 'USER', 1, true);

-- User game stats (give them some coins and level to test shop)
INSERT INTO public.user_game_stats (user_id, level, title, power_points, experience_points, coins, qr_code) VALUES
(1, 3, 'ADVENTURER', 150, 500, 1000, 'ZEKA_QR_123'),
(2, 2, 'APPRENTICE', 120, 200, 500, 'IVORAD_QR_456'),
(3, 1, 'NOVICE', 100, 50, 300, 'KIMI_QR_789');

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
('Ancient Sword', 'SWORD', 5.0, 0.6, 'Permanently increases power by 5%', null),
('Mystic Bow', 'BOW_ARROW', 5.0, 0.6, 'Permanently increases coin rewards by 5%', null);

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
