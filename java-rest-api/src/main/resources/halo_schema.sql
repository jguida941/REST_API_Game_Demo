-- Player statistics table
CREATE TABLE player_stats (
    player_id BIGINT PRIMARY KEY REFERENCES users(id),
    total_kills INT DEFAULT 0,
    total_deaths INT DEFAULT 0,
    total_assists INT DEFAULT 0,
    headshots INT DEFAULT 0,
    matches_played INT DEFAULT 0,
    matches_won INT DEFAULT 0,
    rank_xp INT DEFAULT 0,
    rank_level INT DEFAULT 1,
    highest_skill INT DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Medals earned
CREATE TABLE player_medals (
    id BIGSERIAL PRIMARY KEY,
    player_id BIGINT REFERENCES users(id),
    medal_type VARCHAR(50),
    count INT DEFAULT 1,
    first_earned TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Custom maps
CREATE TABLE custom_maps (
    id BIGSERIAL PRIMARY KEY,
    map_name VARCHAR(100) NOT NULL,
    author_id BIGINT REFERENCES users(id),
    base_map VARCHAR(50),
    game_mode VARCHAR(50),
    description TEXT,
    map_data JSONB,
    download_count INT DEFAULT 0,
    rating DECIMAL(3,2) DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Weapon modifications
CREATE TABLE weapon_mods (
    id BIGSERIAL PRIMARY KEY,
    mod_name VARCHAR(100),
    author_id BIGINT REFERENCES users(id),
    base_weapon VARCHAR(50),
    damage_multiplier DECIMAL(3,2) DEFAULT 1.0,
    fire_rate_multiplier DECIMAL(3,2) DEFAULT 1.0,
    clip_size_modifier INT DEFAULT 0,
    accuracy_modifier DECIMAL(3,2) DEFAULT 1.0,
    custom_properties JSONB,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Match history
CREATE TABLE match_history (
    id BIGSERIAL PRIMARY KEY,
    match_id UUID UNIQUE NOT NULL,
    map_name VARCHAR(100),
    game_mode VARCHAR(50),
    winning_team INT,
    duration_seconds INT,
    started_at TIMESTAMP,
    ended_at TIMESTAMP
);

-- Player match results
CREATE TABLE player_match_results (
    id BIGSERIAL PRIMARY KEY,
    match_id UUID REFERENCES match_history(match_id),
    player_id BIGINT REFERENCES users(id),
    team INT,
    kills INT DEFAULT 0,
    deaths INT DEFAULT 0,
    assists INT DEFAULT 0,
    score INT DEFAULT 0,
    medals_earned JSONB,
    weapon_stats JSONB
);

-- Create indexes for performance
CREATE INDEX idx_player_stats_rank ON player_stats(rank_level DESC);
CREATE INDEX idx_custom_maps_rating ON custom_maps(rating DESC);
CREATE INDEX idx_match_history_player ON player_match_results(player_id, match_id);